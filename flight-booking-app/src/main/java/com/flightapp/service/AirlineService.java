package com.flightapp.service;

import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightapp.entity.Flight;
import com.flightapp.repository.FlightRepository;
import com.flightapp.request.AddFlightRequest;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AirlineService {

	@Autowired
	private FlightRepository flightRepository;

	public Mono<Flight> addFlight(AddFlightRequest request) {
		
		log.info("Received request to add new flight: {}", request.getFlightNumber());

		if (request.getDepartureTime().isBefore(LocalDateTime.now())) {
            return Mono.error(new IllegalArgumentException(
                    "Departure time must be in the future"));
        }
		
		if (!request.getArrivalTime().isAfter(request.getDepartureTime())) {
            return Mono.error(new IllegalArgumentException(
                    "Arrival time must be after departure time"));
        }
		
		Flight flight = new Flight();
        BeanUtils.copyProperties(request, flight);

        return flightRepository.save(flight)
                .doOnSuccess(f -> log.info("Flight added: {}", f.getFlightNumber()));
	}
}
