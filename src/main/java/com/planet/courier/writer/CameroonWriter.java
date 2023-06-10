package com.planet.courier.writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.planet.courier.entity.Cameroon;
import com.planet.courier.model.Courier;
import com.planet.courier.repo.CameroonRepository;

@Component
public class CameroonWriter implements ItemWriter<Courier> {
	
	@Autowired
	private CameroonRepository cameroonRepository;
	
	@Override
	public void write(Chunk<? extends Courier> chunk) throws Exception {
		List<Cameroon> list = new ArrayList<>();
		for (Iterator<? extends Courier> iterator = chunk.iterator(); iterator.hasNext();) {
			Courier courier = iterator.next();
			Cameroon cameroon= new Cameroon();
			cameroon.setId(courier.getId());
			cameroon.setEmail(courier.getEmail());
			cameroon.setParcelWeight(String.valueOf(courier.getParcelWeight()));
			cameroon.setPhoneNumber(courier.getPhoneNumber());
			list.add(cameroon);
		}
		cameroonRepository.saveAll(list);
	}

}
