package com.flightapp.test;

import com.flightapp.controller.AirlineController;
import com.flightapp.entity.Flight;
import com.flightapp.exception.ResourceNotFoundException;
import com.flightapp.request.AddFlightRequest;
import com.flightapp.service.AirlineService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;

@WebFluxTest(AirlineController.class)
class AirlineControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private AirlineService airlineService;

	@Test
	void testAddFlight_Success() {
		AddFlightRequest request = new AddFlightRequest();
		request.setFlightNumber("AI203");
		request.setAirlineName("Air India");
		request.setDepartingAirport("DEL");
		request.setArrivalAirport("BOM");
		request.setDepartureTime(LocalDateTime.now().plusDays(1));
		request.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
		request.setPrice(5000);
		request.setTotalSeats(120);

		Flight savedFlight = new Flight();
		savedFlight.setId("flight123");
		savedFlight.setFlightNumber("AI203");

		Mockito.when(airlineService.addFlight(Mockito.any())).thenReturn(Mono.just(savedFlight));

		webTestClient.post().uri("/api/flight/airline/inventory/add").contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request).exchange().expectStatus().isCreated().expectBody().jsonPath("$.flightNumber")
				.isEqualTo("AI203");
	}

}
