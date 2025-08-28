package com.example.flightreservationsystemfinal;
public class Reservation {
    private String ticketId;
    private String flightNumber;
    private String airlineName;
    private String departureDate;
    private String originCity;
    private String destinationCity;
    private String seatNumbers;

    public Reservation(String ticketId, String flightNumber, String airlineName, String departureDate, String originCity, String destinationCity, String seatNumbers) {
        this.ticketId = ticketId;
        this.flightNumber = flightNumber;
        this.airlineName = airlineName;
        this.departureDate = departureDate;
        this.originCity = originCity;
        this.destinationCity = destinationCity;
        this.seatNumbers = seatNumbers;
    }

    // --- Getters for TableView ---
    public String getTicketId() { return ticketId; }
    public String getFlightNumber() { return flightNumber; }
    public String getAirlineName() { return airlineName; }
    public String getDepartureDate() { return departureDate; }
    public String getOriginCity() { return originCity; }
    public String getDestinationCity() { return destinationCity; }
    public String getSeatNumbers() { return seatNumbers; }
}