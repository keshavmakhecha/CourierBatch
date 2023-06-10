package com.planet.courier.writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.planet.courier.entity.Ethiopia;
import com.planet.courier.model.Courier;
import com.planet.courier.repo.EthiopiaRepository;

@Component
public class EthiopiaWriter implements ItemWriter<Courier> {
	
	@Autowired
	private EthiopiaRepository repository;

	@Override
	public void write(Chunk<? extends Courier> chunk) throws Exception {
		List<Ethiopia> list = new ArrayList<>();
		for (Iterator<? extends Courier> iterator =  chunk.iterator(); iterator.hasNext();) {
			Courier courier = iterator.next();
			Ethiopia ethiopia= new Ethiopia();
			ethiopia.setId(courier.getId());
			ethiopia.setEmail(courier.getEmail());
			ethiopia.setParcelWeight(String.valueOf(courier.getParcelWeight()));
			ethiopia.setPhoneNumber(courier.getPhoneNumber());
			list.add(ethiopia);
		}
		repository.saveAll(list);
	}

}
