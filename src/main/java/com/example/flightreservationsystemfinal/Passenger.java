package com.example.flightreservationsystemfinal;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Passenger {

    public static List<Reservation> getMyReservations(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = """
            SELECT
                r.ticket_id, f.flight_number, a.name AS airline_name, r.seat_numbers,
                f.departure_date, orig.city AS origin_city, dest.city AS destination_city
            FROM reservations r
            JOIN flights f ON r.flight_id = f.id
            JOIN airlines a ON f.airline_id = a.id
            JOIN airports orig ON f.origin_airport_id = orig.id
            JOIN airports dest ON f.destination_airport_id = dest.id
            WHERE r.user_id = ?;
        """;
        ResultSet rs = (ResultSet) Database.databaseQuery(query, userId);

        if (rs == null) return reservations;

        while (rs.next()) {
            reservations.add(new Reservation(
                    rs.getString("ticket_id"),
                    rs.getString("flight_number"),
                    rs.getString("airline_name"),
                    rs.getDate("departure_date").toString(),
                    rs.getString("origin_city"),
                    rs.getString("destination_city"),
                    rs.getString("seat_numbers")
            ));
        }
        rs.close();
        return reservations;
    }

    public static String generateTicketString(String ticketId) throws SQLException {
        String query = """
            SELECT
                r.ticket_id, r.number_of_seats, r.seat_numbers,
                u.first_name, u.last_name,
                f.flight_number, f.departure_date, f.departure_time, f.arrival_time, f.fare,
                al.name AS airline_name,
                orig.name AS origin_airport_name, orig.city AS origin_city,
                dest.name AS destination_airport_name, dest.city AS destination_city
            FROM reservations r
            JOIN users u ON r.user_id = u.id
            JOIN flights f ON r.flight_id = f.id
            JOIN airlines al ON f.airline_id = al.id
            JOIN airports orig ON f.origin_airport_id = orig.id
            JOIN airports dest ON f.destination_airport_id = dest.id
            WHERE r.ticket_id = ?;
        """;

        ResultSet rs = (ResultSet) Database.databaseQuery(query, ticketId);

        if (rs != null && rs.next()) {
            String passengerLine = String.format("Passenger: %-25s   Ticket ID: %s",
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("ticket_id"));

            String flightLine = String.format("Flight: %-28s   Date: %s",
                    rs.getString("flight_number") + " (" + rs.getString("airline_name") + ")",
                    rs.getDate("departure_date"));

            String fromLine = String.format("From: %-30s   (%s)",
                    rs.getString("origin_airport_name"),
                    rs.getString("origin_city"));

            String toLine = String.format("To:   %-30s   (%s)",
                    rs.getString("destination_airport_name"),
                    rs.getString("destination_city"));

            String timeLine = String.format("Departure: %-25s   Arrival: %s",
                    rs.getTime("departure_time"),
                    rs.getTime("arrival_time"));

            String seatsLine = String.format("Seats Booked: %-22s   Total Fare: Rs %.2f",
                    rs.getString("seat_numbers"),
                    rs.getBigDecimal("fare").multiply(java.math.BigDecimal.valueOf(rs.getInt("number_of_seats"))));

            String ticketDetails = String.format("""
                            +--------------------------------------------------------------------+
                            |                        SKYPASS E-TICKET                            |
                            +--------------------------------------------------------------------+
                            | %-66s |
                            | %-66s |
                            | %-66s |
                            | %-66s |
                            | %-66s |
                            | %-66s |
                            | %-66s |
                            | %-66s |
                            +--------------------------------------------------------------------+
                            |          Thank you for flying with us! Safe travels.               |
                            +--------------------------------------------------------------------+
                            """,
                    passengerLine, flightLine, "", fromLine, toLine, "", timeLine, seatsLine);

            try (FileWriter writer = new FileWriter(ticketId + ".txt")) {
                writer.write(ticketDetails);
                System.out.println("Ticket also saved to " + ticketId + ".txt");
            } catch (IOException e) {
                System.err.println("Could not save ticket to file: " + e.getMessage());
            }

            rs.close();
            return ticketDetails;
        }
        return null;
    }
    /**
     * Cancels a reservation by deleting it from the database.
     * @param ticketId The ID of the ticket to cancel.
     * @return True if the cancellation was successful, false otherwise.
     */
    public static boolean cancelReservation(String ticketId) {
        // The database query returns the number of affected rows.
        // If it's greater than 0, the deletion was successful.
        int affectedRows = (int) Database.databaseQuery("DELETE FROM reservations WHERE ticket_id = ?;", ticketId);
        return affectedRows > 0;
    }

    public static ApiFlight convertToApiFlight(Flight flight, String departureDateStr) {
        ApiFlight apiFlight = new ApiFlight();
        apiFlight.setFlightNumber(flight.flightNumber);
        apiFlight.setAirlineIata(flight.flightNumber.substring(0, 2));
        apiFlight.setAirlineName(flight.airlineName);
        apiFlight.setOriginAirportIata(flight.originCity);
        apiFlight.setDestinationAirportIata(flight.destinationCity);
        apiFlight.setDepartureDate(LocalDate.parse(departureDateStr));
        apiFlight.setDepartureTime(LocalTime.parse(flight.departureTime));
        apiFlight.setArrivalTime(LocalTime.parse(flight.arrivalTime));
        apiFlight.setFare(BigDecimal.valueOf(flight.fare));
        return apiFlight;
    }
}