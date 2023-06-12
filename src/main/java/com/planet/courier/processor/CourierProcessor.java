package com.planet.courier.processor;

import static com.planet.courier.constant.Country.Cameroon;
import static com.planet.courier.constant.Country.Ethiopia;
import static com.planet.courier.constant.Country.Morocco;
import static com.planet.courier.constant.Country.Mozambique;
import static com.planet.courier.constant.Country.Uganda;

import org.springframework.batch.item.ItemProcessor;

import com.planet.courier.exception.MissingCountryException;
import com.planet.courier.model.Courier;

public class CourierProcessor implements ItemProcessor<Courier, Courier> {

	public Courier process(Courier courier) {

		if (courier.getPhoneNumber().matches("(237) ?[2368]\\d{7,8}$")) {
			courier.setCountry(Cameroon);
		} else if (courier.getPhoneNumber().matches("(251) ?[1-59]\\d{8}$ ")) {
			courier.setCountry(Ethiopia);
		} else if (courier.getPhoneNumber().matches("(212) ?[5-9]\\d{8}$")) {
			courier.setCountry(Morocco);
		} else if (courier.getPhoneNumber().matches("(258) ?[28]\\d{7,8}$")) {
			courier.setCountry(Mozambique);
		} else if (courier.getPhoneNumber().matches("(256) ?\\d{9}$")) {
			courier.setCountry(Uganda);
		} else {
			throw new MissingCountryException(courier);
		}
		return courier;
	}
}
