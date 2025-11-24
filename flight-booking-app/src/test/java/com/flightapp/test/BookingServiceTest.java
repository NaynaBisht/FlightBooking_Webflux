package com.flightapp.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.flightapp.entity.Booking;
import com.flightapp.entity.Flight;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.request.BookingRequest;
import com.flightapp.request.PassengerRequest;
import com.flightapp.response.BookingResponse;
import com.flightapp.service.BookingService;
import com.flightapp.service.PnrGeneratorService;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@SpringBootTest
class BookingServiceTest {

	@MockBean
	private BookingRepository bookingRepository;

	@MockBean
	private FlightRepository flightRepository;

	@MockBean
	private PnrGeneratorService pnrGeneratorService;

	@Autowired
	private BookingService bookingService;

	@Test
	void testBookFlightSuccess() {

		Flight flight = new Flight();
		flight.setId("1");
		flight.setAvailableSeats(10);
		flight.setPrice(2000);
		flight.setDepartureTime(LocalDateTime.now().plusHours(1));
		flight.setArrivalTime(LocalDateTime.now().plusHours(3));

		when(flightRepository.findByFlightNumber("AI202")).thenReturn(Mono.just(flight));

		when(pnrGeneratorService.generatePnr()).thenReturn("PNR001");

		when(bookingRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

		when(flightRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

		BookingRequest req = new BookingRequest();
		req.setEmailId("test@gmail.com");
		req.setContactNumber("9876543210");
		req.setNumberOfSeats(1);

		PassengerRequest p = new PassengerRequest();
		p.setPassengerName("John");
		p.setAge(25);
		p.setGender("Male");
		p.setSeatNum("A1");
		p.setMealPref("Veg");

		req.setPassengers(List.of(p));

		BookingResponse response = bookingService.bookFlight("AI202", req).block();

		assertNotNull(response);
		assertEquals("PNR001", response.getPnr());
		assertEquals("You have successfully booked the flight", response.getMessage());

		verify(bookingRepository, times(1)).save(any());
		verify(flightRepository, times(1)).save(any());
	}

	@Test
	void testBookFlight_FlightNotFound() {

		when(flightRepository.findByFlightNumber("X999")).thenReturn(Mono.empty());

		BookingRequest req = new BookingRequest();
		req.setEmailId("test@gmail.com");
		req.setContactNumber("9876543210");
		req.setNumberOfSeats(1);

		RuntimeException ex = assertThrows(RuntimeException.class,
				() -> bookingService.bookFlight("X999", req).block());

		assertTrue(ex.getMessage().contains("No flights found"));
	}

	@Test
	void testBookFlight_NotEnoughSeats() {

		Flight flight = new Flight();
		flight.setAvailableSeats(1);

		when(flightRepository.findByFlightNumber("AI202")).thenReturn(Mono.just(flight));

		BookingRequest req = new BookingRequest();
		req.setEmailId("test@gmail.com");
		req.setContactNumber("9876543210");
		req.setNumberOfSeats(5);

		RuntimeException ex = assertThrows(RuntimeException.class,
				() -> bookingService.bookFlight("AI202", req).block());

		assertEquals("Enough seats are not available", ex.getMessage());
	}

	@Test
	void testGetBookingByPnr() {

		Booking b = new Booking();
		b.setPnr("PNR123");

		when(bookingRepository.findByPnr("PNR123")).thenReturn(Mono.just(b));

		Booking result = bookingService.getBookingByPnr("PNR123").block();

		assertEquals("PNR123", result.getPnr());
	}

	@Test
	void testGetBookingHistory() {

		Booking b = new Booking();
		b.setEmailId("abc@gmail.com");

		when(bookingRepository.findByEmailId("abc@gmail.com")).thenReturn(Flux.just(b));

		List<Booking> list = bookingService.getBookingHistoryByEmailId("abc@gmail.com").collectList().block();

		assertEquals(1, list.size());
	}

	@Test
	void testCancelBooking_Success() {

		Booking booking = new Booking();
		booking.setPnr("P001");
		booking.setStatus("BOOKED");

		when(bookingRepository.findByPnr("P001")).thenReturn(Mono.just(booking));

		when(bookingRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

		bookingService.cancelBooking("P001").block();

		assertEquals("CANCELLED", booking.getStatus());
		verify(bookingRepository).save(booking);
	}

}
