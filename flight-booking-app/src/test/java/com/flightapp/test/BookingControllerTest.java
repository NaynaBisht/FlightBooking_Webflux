package com.flightapp.test;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.flightapp.exception.BadRequestException;
import com.flightapp.exception.ResourceNotFoundException;
import com.flightapp.request.BookingRequest;
import com.flightapp.service.BookingService;

import reactor.core.publisher.Mono;

public class BookingControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private BookingService bookingService;

	@Test
	void testCancelBookingSuccess() {
		String pnr = "PNR001";

		Mockito.when(bookingService.cancelBooking(pnr)).thenReturn(Mono.empty());

		webTestClient.delete().uri("/api/flight/booking/cancel/{pnr}", pnr).exchange().expectStatus().isNoContent()
				.expectBody().isEmpty();
	}

	@Test
	void testCancelBooking_NotFound() {
		String pnr = "INVALIDPNR";

		Mockito.when(bookingService.cancelBooking(Mockito.eq(pnr)))
				.thenReturn(Mono.error(new ResourceNotFoundException("Booking not found")));

		webTestClient.delete().uri("/api/flight/booking/cancel/{pnr}", pnr).exchange().expectStatus().isNotFound()
				.expectBody().jsonPath("$.message").isEqualTo("Booking not found");
	}

	@Test
	void testBookingValidation_MissingEmail() {
		String json = """
				{
				  "contactNumber": "9876543210",
				  "numberOfSeats": 1,
				  "passengers": [
				    { "passengerName": "Rush", "age": 25, "gender": "MALE", "seatNum": "A51", "mealPref": "VEG" }
				  ]
				}
				""";

		webTestClient.post().uri("/api/flight/booking/AI203").contentType(MediaType.APPLICATION_JSON).bodyValue(json)
				.exchange().expectStatus().isBadRequest();
	}

	@Test
	void testBookingValidation_InvalidEmailFormat() {
		String json = """
				{
				  "emailId": "wrong-email",
				  "contactNumber": "9876543210",
				  "numberOfSeats": 1,
				  "passengers": [
				    { "passengerName": "Nimish", "age": 56, "gender": "MALE", "seatNum": "B1", "mealPref": "NON-VEG" }
				  ]
				}
				""";

		webTestClient.post().uri("/api/flight/booking/AI203").contentType(MediaType.APPLICATION_JSON).bodyValue(json)
				.exchange().expectStatus().isBadRequest();
	}

	@Test
	void testBookingValidation_InvalidContactNumber() {
		String json = """
				{
				  "emailId": "nayna@gmail.com",
				  "contactNumber": "123456",
				  "numberOfSeats": 1,
				  "passengers": [
				    { "passengerName": "Nayna", "age": 22, "gender": "FEMALE", "seatNum": "A1", "mealPref": "VEG" }
				  ]
				}
				""";

		webTestClient.post().uri("/api/flight/booking/AI203").contentType(MediaType.APPLICATION_JSON).bodyValue(json)
				.exchange().expectStatus().isBadRequest();
	}

	@Test
	void testBookingValidation_ZeroSeats() {
		String json = """
				{
				  "emailId": "nayna@gmail.com",
				  "contactNumber": "8766557890",
				  "numberOfSeats": 0,
				  "passengers": []
				}
				""";

		webTestClient.post().uri("/api/flight/booking/AI203").contentType(MediaType.APPLICATION_JSON).bodyValue(json)
				.exchange().expectStatus().isBadRequest();
	}

	@Test
	void testPassengerSeatMismatch() {
		BookingRequest request = new BookingRequest();
		request.setEmailId("nayna@gmail.com");
		request.setContactNumber("7645543210");
		request.setNumberOfSeats(2);
		request.setPassengers(List.of());

		Mockito.when(bookingService.bookFlight(Mockito.eq("AI203"), Mockito.any()))
				.thenReturn(Mono.error(new BadRequestException("Passenger count must match number of seats booked")));

		webTestClient.post().uri("/api/flight/booking/AI203").contentType(MediaType.APPLICATION_JSON).bodyValue(request)
				.exchange().expectStatus().isBadRequest().expectBody().jsonPath("$.message")
				.isEqualTo("Passenger count must match number of seats booked");
	}

}
