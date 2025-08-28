               SkyPass Airlines Reservation System
============================================================================================
SkyPass is a comprehensive desktop application for flight booking and management, built with JavaFX. This project demonstrates a complete airline reservation system, featuring separate interfaces for passengers and administrators, real-time flight data integration, and a secure database backend. The application was originally a command-line tool that has been fully migrated to a modern, responsive Graphical User Interface (GUI).

Link for the image :-   https://ibb.co/album/mFw0kj

                
Features
===========

Passenger Features
----------------------

Secure Authentication: Secure user registration and login with hashed passwords (jBCrypt).

Real-time Flight Search: Search for available flights by route and date using the Amadeus Flight Offers API.

Interactive Seat Selection: A visual seat map allows users to select their desired seats before booking.

Booking and Reservations: Book one or more seats on a selected flight.

Reservation Management: View a list of all personal reservations.

Ticket Cancellation: Cancel existing bookings.

E-Ticket Generation: View and save a formatted e-ticket for any reservation.

Administrator Features
-----------------------------

Admin Dashboard: A separate, secure portal for system management.

Flight Management:
---------------------
View all simulated flights in the system.

Add new flights with complete details (route, schedule, capacity, fare).

Edit the details of existing flights.

Remove flights from the system.

User Management:
---------------------
View a list of all registered users in the system.

Promote standard passenger accounts to an admin role.

Demote admin accounts back to a passenger role.


Technologies Used
========================
Language: Java (Recommended: JDK 21 - Long-Term Support)

UI Framework: JavaFX

Database: MySQL

Build Tool: Apache Maven

Key Libraries:
-----------------

MySQL Connector/J: For database connectivity.

jBCrypt: For secure password hashing.

OkHttp & Gson: For making HTTP requests to the flight data API.

Amadeus for Developers API: For real-time flight search data.


Setup and Installation
=============================
To get this project running on your local machine, please follow these steps.

1. Prerequisites
Make sure you have the following software installed:

Git

Java Development Kit (JDK) - Version 21 (LTS) is recommended.

Apache Maven

2. Clone the Repository
Open your terminal and clone this repository to your local machine:

git clone https://github.com/username/repository-name.git

cd your-repository-name

3. Database Setup
Start your MySQL server.

Create a new database named java_project.

Run the New.sql script provided in the root of this project to create all the necessary tables and sample data.

4. Configuration
Database Credentials: Open the src/main/java/.../util/Database.java file and update the DB_USER and DB_PASSWORD variables to match your MySQL setup.

API Keys: Open the src/main/java/.../service/FlightApiClient.java file and replace the placeholder values for API_KEY and API_SECRET with your own keys from the Amadeus for Developers portal.

5. Build the Project
Use Maven to download all dependencies and compile the project.

mvn clean install

6. Run the Application
You can run the application directly from an IDE like IntelliJ IDEA.

Open the project in your IDE.

Open the Run/Debug Configurations for the Main.java file.

Crucially, you must add the following VM options to link the JavaFX library. Replace the path with the location of your JavaFX SDK's lib folder.

--module-path "C:\path\to\your\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml

Run the Main.java file.
