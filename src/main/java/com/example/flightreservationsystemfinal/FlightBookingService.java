package com.example.flightreservationsystemfinal;
import java.sql.*;
import java.util.UUID;

public class FlightBookingService {

    private final Connection connection;

    public FlightBookingService(Connection dbConnection) {
        this.connection = dbConnection;
    }

    public int getOrCreateFlight(ApiFlight flight) throws SQLException {
        String checkSql = "SELECT id FROM flights WHERE flight_number = ? AND departure_date = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
            ps.setString(1, flight.getFlightNumber());
            ps.setDate(2, Date.valueOf(flight.getDepartureDate()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Flight already exists
            }
        }

        int airlineId = getOrCreateAirline(flight.getAirlineName(), flight.getAirlineIata());
        int originAirportId = getOrCreateAirport(flight.getOriginAirportIata());
        int destAirportId = getOrCreateAirport(flight.getDestinationAirportIata());

        String insertSql = "INSERT INTO flights (flight_number, airline_id, origin_airport_id, destination_airport_id, departure_date, departure_time, arrival_time, fare, capacity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, flight.getFlightNumber());
            ps.setInt(2, airlineId);
            ps.setInt(3, originAirportId);
            ps.setInt(4, destAirportId);
            ps.setDate(5, Date.valueOf(flight.getDepartureDate()));
            ps.setTime(6, Time.valueOf(flight.getDepartureTime()));
            ps.setTime(7, Time.valueOf(flight.getArrivalTime()));
            ps.setBigDecimal(8, flight.getFare());
            ps.setInt(9, 180); // Default capacity

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public String createReservation(int userId, int flightId, int numSeats, String seatNumbers) throws SQLException {
        String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sql = "INSERT INTO reservations (ticket_id, user_id, flight_id, number_of_seats, seat_numbers) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ticketId);
            ps.setInt(2, userId);
            ps.setInt(3, flightId);
            ps.setInt(4, numSeats);
            ps.setString(5, seatNumbers);
            ps.executeUpdate();
        }
        return ticketId;
    }

    private int getOrCreateAirline(String name, String iata) throws SQLException {
        String checkSql = "SELECT id FROM airlines WHERE iata_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
            ps.setString(1, iata);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        String insertSql = "INSERT INTO airlines (name, iata_code) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name != null ? name : "Unknown Airline");
            ps.setString(2, iata);
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating airline failed, no ID obtained.");
    }

    private int getOrCreateAirport(String iata) throws SQLException {
        String checkSql = "SELECT id FROM airports WHERE iata_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
            ps.setString(1, iata);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        String insertSql = "INSERT INTO airports (name, city, iata_code) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Airport " + iata); // Placeholder name
            ps.setString(2, "City " + iata);     // Placeholder city
            ps.setString(3, iata);
            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating airport failed, no ID obtained.");
    }
}