package org.example.car.info.dao;

import org.example.car.info.model.Car;
import org.springframework.data.repository.CrudRepository;

public interface CarRepository extends CrudRepository<Car, String>, CustomizedCarRepository {

}
