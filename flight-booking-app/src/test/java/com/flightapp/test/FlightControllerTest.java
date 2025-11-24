package com.flightapp.test;

import com.flightapp.controller.FlightSearchController;
import com.flightapp.request.FlightSearchRequest;
import com.flightapp.request.PassengerCount;
import com.flightapp.response.FlightSearchResponse;
import com.flightapp.service.FlightService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = FlightSearchController.class)
class FlightSearchControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private FlightService flightService;

	private FlightSearchRequest getValidRequest() {
		FlightSearchRequest request = new FlightSearchRequest();
		request.setDepartingAirport("DEL");
		request.setArrivalAirport("BOM");
		request.setDepartDate(LocalDate.of(2025, 1, 10));

		PassengerCount passengers = new PassengerCount();
		passengers.setAdults(1);
		passengers.setChildren(0);

		request.setPassengers(passengers);
		return request;
	}

	@Test
	void testSearchFlights_NoFlightsFound() {

		Mockito.when(flightService.searchFlights(any())).thenReturn(Mono.empty());

		webTestClient.post().uri("/api/flight/search").contentType(MediaType.APPLICATION_JSON)
				.bodyValue(getValidRequest()).exchange().expectStatus().isNotFound().expectBody().jsonPath("$.message")
				.isEqualTo("No flights found for selected date and route").jsonPath("$.totalFlights").isEqualTo(0);
	}

	@Test
	void testSearchFlights_ErrorResponse() {

		Mockito.when(flightService.searchFlights(any()))
				.thenReturn(Mono.error(new RuntimeException("Something went wrong")));

		webTestClient.post().uri("/api/flight/search").contentType(MediaType.APPLICATION_JSON)
				.bodyValue(getValidRequest()).exchange().expectStatus().isBadRequest().expectBody()
				.jsonPath("$.message").isEqualTo("Something went wrong").jsonPath("$.totalFlights").isEqualTo(0);
	}
}
