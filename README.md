# FlightBooking_Webflux
A complete Flight Booking backend application built using Spring Boot, MongoDB, and REST APIs.
This system allows users to search flights, book flights, check PNR status, cancel bookings, and view flight/booking analytics.

#Features
1.Flight Management
  Add new flights
  Search flights with filters:
    Departing airport
    Arrival airport
    Date & time
    Price range
    Airline
  Sort by price, departure time, arrival time

2.Booking Management

  Book flights with passenger details
  Auto-generate PNR
  Update available seats
  View booking via:
    PNR
    Email + flightNumber
  Cancel booking

3.Admin Analytics (MongoDB Aggregation Pipelines)
  Total bookings per day
  Seats booked per flight
  Total revenue per flight

#Tech Stack
  Java 17+
  Spring Boot
  Spring Web
  Lombok
  MongoDB
  MongoDB Compass
  Postman

#Flight APIs
  Add Flight : POST /api/flight/airline/inventory/add
  Search Flights : POST /api/flight/search
  Book Flight : POST /api/flight/booking/{flightNumber}
  Get Booking by PNR : GET /api/flight/booking/pnr/{pnr}
  Get Booking by Email + Flight : GET /api/flight/booking/{flightNumber}/email/{email}
  Cancel Booking : DELETE /api/flight/cancel/{pnr}

#Aggregation Pipelines Used
  Total Bookings Per Day
  Seats Booked Per Flight
  Revenue Per Flight
Seats booked per flight

Total revenue per flight
