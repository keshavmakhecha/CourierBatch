package com.planet.courier.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planet.courier.entity.Uganda;

@Repository
public interface UgandaRepository extends JpaRepository<Uganda, Integer> {

}
