package com.planet.courier.writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.planet.courier.entity.Morocoo;
import com.planet.courier.model.Courier;
import com.planet.courier.repo.MorocooRepository;

@Component
public class MorocooWriter implements ItemWriter<Courier> {
	
	@Autowired
	private MorocooRepository repository;

	@Override
	public void write(Chunk<? extends Courier> chunk) throws Exception {
		List<Morocoo> list = new ArrayList<>();
		for (Iterator<? extends Courier> iterator = chunk.iterator(); iterator.hasNext();) {
			Courier courier = iterator.next();
			Morocoo morocoo= new Morocoo();
			morocoo.setId(courier.getId());
			morocoo.setEmail(courier.getEmail());
			morocoo.setParcelWeight(String.valueOf(courier.getParcelWeight()));
			morocoo.setPhoneNumber(courier.getPhoneNumber());
			list.add(morocoo);
		}
		repository.saveAll(list);
	}

}
