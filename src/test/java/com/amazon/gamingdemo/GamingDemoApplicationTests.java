package com.amazon.gamingdemo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GamingDemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testApplicationStartsSuccess() {
		Assertions.assertDoesNotThrow(() -> GamingDemoApplication.main(new String[]{}));
	}

}
