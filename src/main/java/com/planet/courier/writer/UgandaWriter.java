package com.planet.courier.writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.planet.courier.entity.Uganda;
import com.planet.courier.model.Courier;
import com.planet.courier.repo.UgandaRepository;

@Component
public class UgandaWriter implements ItemWriter<Courier> {
	
	@Autowired
	private UgandaRepository repository;

	@Override
	public void write(Chunk<? extends Courier> chunk) throws Exception {
		List<Uganda> list = new ArrayList<>();
		for (Iterator<? extends Courier> iterator = chunk.iterator(); iterator.hasNext();) {
			Courier courier = iterator.next();
			Uganda uganda= new Uganda();
			uganda.setId(courier.getId());
			uganda.setEmail(courier.getEmail());
			uganda.setParcelWeight(String.valueOf(courier.getParcelWeight()));
			uganda.setPhoneNumber(courier.getPhoneNumber());
			list.add(uganda);
		}
		repository.saveAll(list);
	}
}
