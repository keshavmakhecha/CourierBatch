package com.planet.courier.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.planet.courier.entity.Cameroon;

@Repository
public interface CameroonRepository extends JpaRepository<Cameroon, Integer> {

}
