package com.planet.courier.writer;

import java.util.List;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.classify.Classifier;
import org.springframework.stereotype.Component;

import com.planet.courier.model.Courier;

@Component
public class ClassifierWriter implements Classifier<Courier, JdbcBatchItemWriter<? super List<Courier>>> {

	private static final long serialVersionUID = 1L;

	@Autowired
	private JdbcBatchItemWriter<List<Courier>> cameroonWriter;
	@Autowired
	private JdbcBatchItemWriter<List<Courier>> mozambiqueWriter;
	@Autowired
	private JdbcBatchItemWriter<List<Courier>> ugandaWriter;
	@Autowired
	private JdbcBatchItemWriter<List<Courier>> ethipoiaWriter;
	@Autowired
	private JdbcBatchItemWriter<List<Courier>> morocooWriter;

	@Override
	public JdbcBatchItemWriter<? super List<Courier>> classify(Courier customer) {
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