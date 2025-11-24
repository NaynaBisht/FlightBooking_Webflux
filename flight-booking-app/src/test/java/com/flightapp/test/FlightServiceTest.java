package com.flightapp.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.flightapp.entity.Flight;
import com.flightapp.repository.FlightRepository;
import com.flightapp.request.FlightSearchRequest;
import com.flightapp.request.PassengerCount;
import com.flightapp.response.FlightSearchResponse;
import com.flightapp.service.FlightService;

import reactor.core.publisher.Flux;

@SpringBootTest
class FlightServiceTest {

	@MockBean
	private FlightRepository flightRepository;

	@Autowired
	private FlightService flightService;

	@Test
	void testSearchFlightsSuccess() {

		FlightSearchRequest request = new FlightSearchRequest();
		request.setDepartingAirport("DEL");
		request.setArrivalAirport("BLR");
		request.setDepartDate(LocalDate.now());

		PassengerCount passengers = new PassengerCount();
		passengers.setAdults(2);
		passengers.setChildren(1);
		request.setPassengers(passengers);

		Flight flight = new Flight();
		flight.setId("101");
		flight.setFlightNumber("AI203");
		flight.setAirlineName("Air India");
		flight.setDepartingAirport("DEL");
		flight.setArrivalAirport("BLR");
		flight.setDepartureTime(LocalDateTime.now().plusHours(2));
		flight.setArrivalTime(LocalDateTime.now().plusHours(4));
		flight.setPrice(5000);
		flight.setTotalSeats(180);
		flight.setAvailableSeats(50);

		when(flightRepository.findByDepartingAirportAndArrivalAirportAndDepartureTimeBetween(any(), any(), any(),
				any())).thenReturn(Flux.just(flight));

		FlightSearchResponse response = flightService.searchFlights(request).block();

		assertEquals(1, response.getTotalFlights());
		assertEquals(1, response.getFlights().size());

		FlightSearchResponse.FlightInfo result = response.getFlights().get(0);
		assertEquals("AI203", result.getFlightNumber());
		assertEquals("Air India", result.getAirlineName());

		verify(flightRepository, times(1)).findByDepartingAirportAndArrivalAirportAndDepartureTimeBetween(any(), any(),
				any(), any());
	}

	@Test
	void testSearchFlightsNoResults() {

		FlightSearchRequest request = new FlightSearchRequest();
		request.setDepartingAirport("DEL");
		request.setArrivalAirport("BLR");
		request.setDepartDate(LocalDate.now());

		PassengerCount pc = new PassengerCount();
		pc.setAdults(1);
		pc.setChildren(0);
		request.setPassengers(pc);

		when(flightRepository.findByDepartingAirportAndArrivalAirportAndDepartureTimeBetween(any(), any(), any(),
				any())).thenReturn(Flux.empty());

		FlightSearchResponse response = flightService.searchFlights(request).block();

		assertEquals(0, response.getTotalFlights());
	}
}
