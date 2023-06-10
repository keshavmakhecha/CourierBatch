package com.planet.courier.writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.planet.courier.entity.Mozambique;
import com.planet.courier.model.Courier;
import com.planet.courier.repo.MozambiqueRepository;

@Component
public class MozambiqueWriter implements ItemWriter<Courier> {
	
	@Autowired
	private MozambiqueRepository repository;

	@Override
	public void write(Chunk<? extends Courier> chunk) throws Exception {
		List<Mozambique> list = new ArrayList<>();
		for (Iterator<? extends Courier> iterator = chunk.iterator(); iterator.hasNext();) {
			Courier courier = iterator.next();
			Mozambique mozambique= new Mozambique();
			mozambique.setId(courier.getId());
			mozambique.setEmail(courier.getEmail());
			mozambique.setParcelWeight(String.valueOf(courier.getParcelWeight()));
			mozambique.setPhoneNumber(courier.getPhoneNumber());
			list.add(mozambique);
		}
		repository.saveAll(list);
	}

}
