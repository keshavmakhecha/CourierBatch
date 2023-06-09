package com.planet.courier.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.planet.courier.model.Courier;

public class RecordMappper implements FieldSetMapper<Courier> {

	public Courier mapFieldSet(FieldSet fieldSet) {

		Courier courier = new Courier();
		courier.setId(fieldSet.readInt("id"));
		courier.setEmail(fieldSet.readString("email"));
		courier.setPhoneNumber(fieldSet.readString("phone_number"));
		courier.setParcelWeight(fieldSet.readDouble("parcel_weight"));

		return courier;

	}

}