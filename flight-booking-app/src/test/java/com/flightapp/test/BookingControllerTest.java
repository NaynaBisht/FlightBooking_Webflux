package com.flightapp.test;

import com.flightapp.controller.BookingController;
import com.flightapp.exception.GlobalExceptionHandler;
import com.flightapp.exception.BadRequestException;
import com.flightapp.exception.ResourceNotFoundException;
import com.flightapp.request.BookingRequest;
import com.flightapp.request.PassengerRequest;
import com.flightapp.service.BookingService;
import com.flightapp.service.PnrGeneratorService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.List;

@WebFluxTest(controllers = BookingController.class)
@AutoConfigureWebTestClient
@Import(GlobalExceptionHandler.class)
class BookingControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private BookingService bookingService;

	@MockBean
	private PnrGeneratorService pnrGeneratorService;

	@Test
	void testCancelBookingSuccess() {
		String pnr = "PNR001";

		Mockito.when(bookingService.cancelBooking(pnr)).thenReturn(Mono.empty());

		webTestClient.delete().uri("/api/flight/booking/cancel/{pnr}", pnr).exchange().expectStatus().isNoContent();
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
}
