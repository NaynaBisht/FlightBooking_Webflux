package com.flightapp.test;

import com.flightapp.exception.InvalidFlightTimeException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class InvalidFlightTimeExceptionTest {

	@Test
	void testMessageStored() {
		InvalidFlightTimeException ex = new InvalidFlightTimeException("Invalid timing");
		assertThat(ex.getMessage()).isEqualTo("Invalid timing");
	}
}
