package com.example.flightreservationsystemfinal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Flight {
    // Properties for a single flight instance
    public int id;
    public String flightNumber;
    public String airlineName;
    public String originCity;
    public String destinationCity;
    public String departureDate;
    public String departureTime;
    public String arrivalTime;
    public int capacity;
    public double fare;
    public boolean available;
    public int availableSeats;

    // API-specific details
    public String duration;
    public String aircraftType;

    // --- GETTERS for JavaFX PropertyValueFactory ---
    public int getId() { return id; }
    public String getFlightNumber() { return flightNumber; }
    public String getAirlineName() { return airlineName; }
    public String getOriginCity() { return originCity; }
    public String getDestinationCity() { return destinationCity; }
    public String getDepartureDate() { return departureDate; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public int getCapacity() { return capacity; }
    public double getFare() { return fare; }
    public boolean isAvailable() { return available; }
    public int getAvailableSeats() { return availableSeats; }
    public String getDuration() { return duration; }
    public String getAircraftType() { return aircraftType; }


    public static List<Flight> getAvailableFlights() throws SQLException {
        List<Flight> flights = new ArrayList<>();
        String query = """
            SELECT
                f.id, f.flight_number, a.name AS airline_name,
                orig.city AS origin_city, dest.city AS destination_city,
                f.departure_date, f.departure_time, f.arrival_time, f.fare, f.capacity, f.available,
                (f.capacity - COALESCE(SUM(r.number_of_seats), 0)) AS available_seats
            FROM flights f
            JOIN airlines a ON f.airline_id = a.id
            JOIN airports orig ON f.origin_airport_id = orig.id
            JOIN airports dest ON f.destination_airport_id = dest.id
            LEFT JOIN reservations r ON f.id = r.flight_id
            GROUP BY f.id, a.name, orig.city, dest.city;
        """;

        ResultSet rs = (ResultSet) Database.databaseQuery(query);
        if (rs == null) return flights;

        while (rs.next()) {
            Flight flight = new Flight();
            flight.id = rs.getInt("id");
            flight.flightNumber = rs.getString("flight_number");
            flight.airlineName = rs.getString("airline_name");
            flight.originCity = rs.getString("origin_city");
            flight.destinationCity = rs.getString("destination_city");
            flight.departureDate = rs.getDate("departure_date").toString();
            flight.departureTime = rs.getTime("departure_time").toString();
            flight.arrivalTime = rs.getTime("arrival_time").toString();
            flight.fare = rs.getDouble("fare");
            flight.capacity = rs.getInt("capacity");
            flight.available = rs.getBoolean("available");
            flight.availableSeats = rs.getInt("available_seats");
            flights.add(flight);
        }
        rs.close();
        return flights;
    }

    public static Flight getFlightById(int flightId) throws SQLException {
        ResultSet rs = (ResultSet) Database.databaseQuery("SELECT * FROM flights WHERE id = ?", flightId);
        if (rs != null && rs.next()) {
            Flight flight = new Flight();
            flight.id = rs.getInt("id");
            flight.capacity = rs.getInt("capacity");
            // Populate other fields if necessary
            rs.close();
            return flight;
        }
        return null;
    }

    public static boolean addFlight(String flightNum, String airlineCode, String originCode, String destCode, String date, String depTime, String arrTime, int capacity, double fare) throws SQLException {
        int airlineId = getIdFromTable("airlines", "iata_code", airlineCode);
        int originId = getIdFromTable("airports", "iata_code", originCode);
        int destId = getIdFromTable("airports", "iata_code", destCode);

        if (airlineId == -1 || originId == -1 || destId == -1) {
            return false;
        }

        int affectedRows = (int) Database.databaseQuery(
                "INSERT INTO flights (flight_number, airline_id, origin_airport_id, destination_airport_id, departure_date, departure_time, arrival_time, capacity, fare, available) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE);",
                flightNum, airlineId, originId, destId, date, depTime, arrTime, capacity, fare
        );
        return affectedRows > 0;
    }

    public static boolean editFlight(int flightId, String airlineCode, String originCode, String destCode,
                                     String date, String depTime, String arrTime, int capacity,
                                     double fare, boolean isAvailable) throws SQLException {

        int airlineId = getIdFromTable("airlines", "iata_code", airlineCode);
        int originId = getIdFromTable("airports", "iata_code", originCode);
        int destId = getIdFromTable("airports", "iata_code", destCode);

        if (airlineId == -1 || originId == -1 || destId == -1) {
            return false; // Invalid IATA code
        }

        String sql = """
        UPDATE flights SET
            airline_id = ?,
            origin_airport_id = ?,
            destination_airport_id = ?,
            departure_date = ?,
            departure_time = ?,
            arrival_time = ?,
            capacity = ?,
            fare = ?,
            available = ?
        WHERE id = ?;
    """;

        int affectedRows = (int) Database.databaseQuery(sql, airlineId, originId, destId, date, depTime, arrTime,
                capacity, fare, isAvailable, flightId);
        return affectedRows > 0;
    }

    public static boolean removeFlight(int flightId) {
        int affectedRows = (int) Database.databaseQuery("DELETE FROM flights WHERE id = ?;", flightId);
        return affectedRows > 0;
    }

    private static int getIdFromTable(String tableName, String columnName, String value) throws SQLException {
        ResultSet rs = (ResultSet) Database.databaseQuery("SELECT id FROM " + tableName + " WHERE " + columnName + " = ?;", value);
        if (rs != null && rs.next()) {
            int id = rs.getInt("id");
            rs.close();
            return id;
        }
        return -1;
    }
}