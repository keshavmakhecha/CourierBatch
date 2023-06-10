package com.planet.courier.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planet.courier.entity.Morocoo;

@Repository
public interface MorocooRepository extends JpaRepository<Morocoo, Integer> {

}
