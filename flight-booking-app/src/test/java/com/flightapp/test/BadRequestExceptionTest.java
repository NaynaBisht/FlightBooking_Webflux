package com.flightapp.test;

import com.flightapp.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BadRequestExceptionTest {

	@Test
	void testMessageStored() {
		BadRequestException ex = new BadRequestException("Bad request");
		assertThat(ex.getMessage()).isEqualTo("Bad request");
	}
}
