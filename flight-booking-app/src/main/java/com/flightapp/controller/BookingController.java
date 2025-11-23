package com.flightapp.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.flightapp.entity.Booking;
import com.flightapp.request.BookingRequest;
import com.flightapp.response.BookingResponse;
import com.flightapp.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("api/v1.0/flight")
@RequiredArgsConstructor
public class BookingController {

	private BookingService bookingService;

	@GetMapping("/ticket/{pnr}")
	public Mono<ResponseEntity<Booking>> getTicketDetailsByPnr(@PathVariable String pnr) {
		log.info("Fetching ticket details for PNR={}", pnr);

		return bookingService.getBookingByPnr(pnr).map(response -> ResponseEntity.ok(response));
	}

	@GetMapping("/booking/history/{emailId}")
	public Flux<Booking> getBookingHistory(@PathVariable String emailId) {
		log.info("Fetching booking history for emailId={}", emailId);

		return bookingService.getBookingHistoryByEmailId(emailId);
	}

	@PostMapping("/booking/{flightNumber}")
	public Mono<ResponseEntity<BookingResponse>> bookFlight(@PathVariable String flightNumber,
			@Valid @RequestBody BookingRequest request) {

		log.info("Booking flight {} for {}", flightNumber, request.getEmailId());
		return bookingService.bookFlight(flightNumber, request)
				.map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response)).onErrorResume(ex -> {

					String message = ex.getMessage() != null ? ex.getMessage() : "Unexpected error";

					if (message.contains("Flight not found")) {
						return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
								.body(new BookingResponse(null, 0, message)));
					}

					return Mono.just(
							ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BookingResponse(null, 0, message)));
				});

	}

	@DeleteMapping("/booking/cancel/{pnr}")
	public Mono<ResponseEntity<Void>> cancelBooking(@PathVariable String pnr) {
		log.warn("Cancel request received for PNR={}", pnr);

		return bookingService.cancelBooking(pnr).then(Mono.just(ResponseEntity.noContent().<Void>build()))
				.onErrorResume(ex -> {
					String message = ex.getMessage() == null ? "" : ex.getMessage();

					if (message.contains("not found")) {
						return Mono.just(ResponseEntity.notFound().<Void>build());
					}

					return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).<Void>build());
				});
	}

}
