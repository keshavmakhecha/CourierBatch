package com.planet.courier.processor;

import static com.planet.courier.constant.Country.Cameroon;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.planet.courier.model.Courier;

@SpringBootTest
public class CourierProcessorTest {

	private CourierProcessor courierProcessor=new CourierProcessor();

	private Courier courier;

	@Before
	public void setUp() {
		courier = new Courier();
		courier.setId(1);
		courier.setEmail("email1@gmail.com");
		courier.setParcelWeight(91.0);
		courier.setPhoneNumber("237 209993809");
	}

	@Test
	public void testProcess() {
		courier = courierProcessor.process(courier);

		assertTrue(courier.getCountry().equals(Cameroon));
	}

}
