package com.flightapp.test;

import com.flightapp.exception.PassengerValidationException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PassengerValidationExceptionTest {

	@Test
	void testMessageStored() {
		PassengerValidationException ex = new PassengerValidationException("Invalid passenger");
		assertThat(ex.getMessage()).isEqualTo("Invalid passenger");
	}
}
