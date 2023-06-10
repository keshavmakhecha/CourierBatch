package com.planet.courier.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.classify.Classifier;
import org.springframework.stereotype.Component;

import com.planet.courier.model.Courier;

@Component
public class ClassifierWriter implements Classifier<Courier, ItemWriter<? super Courier>> {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CameroonWriter cameroonWriter;
	@Autowired
	private MozambiqueWriter mozambiqueWriter;
	@Autowired
	private UgandaWriter ugandaWriter;
	@Autowired
	private EthiopiaWriter ethipoiaWriter;
	@Autowired
	private MorocooWriter morocooWriter;

	@Override
	public ItemWriter<? super Courier> classify(Courier customer) {
			switch (customer.getCountry()) {
			case Cameroon:
				return cameroonWriter;
			case Ethiopia:
				return ethipoiaWriter;
			case Morocco:
				return morocooWriter;
			case Mozambique:
				return mozambiqueWriter;
			case Uganda:
				return ugandaWriter;
			default:
				throw new IllegalArgumentException("Unexpected value: " + customer.getCountry());
			}
		} 
}