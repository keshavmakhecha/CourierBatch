package com.planet.courier.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planet.courier.entity.Ethiopia;

@Repository
public interface EthiopiaRepository extends JpaRepository<Ethiopia, Integer> {

}
