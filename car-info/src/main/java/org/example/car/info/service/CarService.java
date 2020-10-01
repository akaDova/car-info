package org.example.car.info.service;

import org.example.car.info.model.Car;
import org.example.car.info.dao.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CarService {
    @Autowired
    public CarRepository repository;

    public Iterable<Car> getAll() {
        return repository.findAll();
    }

    public Map<String, Long> getCarsCountPerMark() {
        return repository.countMarks();
    }

    public Map<String, Long> getCarsCountPerYear() {
        return repository.countYears();
    }

    public Map<String, Map<String, Long>> getCarsCountPerModel() {
        return repository.countModels();
    }


}
