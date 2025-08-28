package com.example.flightreservationsystemfinal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SeatManager {

    public Set<String> getBookedSeats(int flightId) throws SQLException {
        Set<String> bookedSeats = new HashSet<>();
        String query = "SELECT seat_numbers FROM reservations WHERE flight_id = ? AND seat_numbers IS NOT NULL;";
        ResultSet rs = (ResultSet) Database.databaseQuery(query, flightId);

        if (rs != null) {
            while (rs.next()) {
                String seatsStr = rs.getString("seat_numbers");
                if (seatsStr != null && !seatsStr.isEmpty()) {
                    String[] seats = seatsStr.split(",\\s*");
                    for (String seat : seats) {
                        bookedSeats.add(seat.toUpperCase());
                    }
                }
            }
            rs.close();
        }
        return bookedSeats;
    }
}