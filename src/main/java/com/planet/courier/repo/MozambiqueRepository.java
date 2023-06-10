package com.planet.courier.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planet.courier.entity.Mozambique;

@Repository
public interface MozambiqueRepository extends JpaRepository<Mozambique, Integer> {

}
