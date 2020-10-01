package org.example.car.info.dao;

import java.util.Map;

public interface CustomizedCarRepository {
    Map<String, Long> countMarks();

    Map<String, Long> countYears();

    Map<String, Map<String, Long>> countModels();
}
