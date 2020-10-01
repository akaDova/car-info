package org.example.car.info.dao.impl;

import org.example.car.info.dao.CustomizedCarRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomizedCarRepositoryImpl implements CustomizedCarRepository {
    @Autowired
    EntityManager em;

    @Override
    public Map<String, Long> countMarks() {
        var rezMap = new HashMap<String, Long>();
        List<Object[]> results = em.createQuery("SELECT t.mark, count(t.mark) AS mark_count FROM Car t GROUP BY t.mark").getResultList();

        for (Object[] result : results) {
            rezMap.put((String) result[0], (Long) result[1]);
        }

        return rezMap;
    }

    @Override
    public Map<String, Long> countYears() {
        var rezMap = new HashMap<String, Long>();
        List<Object[]> results = em.createQuery("SELECT t.year, count(t.year) AS year_count FROM Car t GROUP BY t.year").getResultList();

        for (Object[] result : results) {
            rezMap.put((String) result[0], (Long) result[1]);
        }

        return rezMap;
    }

    @Override
    public Map<String, Map<String, Long>> countModels() {
        var rezMap = new HashMap<String, Map<String, Long>>();
        List<Object[]> results = em.createQuery("SELECT t.mark, t.model, count(t.model) AS model_count FROM Car t GROUP BY t.mark, t.model").getResultList();

        for (Object[] result : results) {
            String mark = (String) result[0];
            if (!rezMap.containsKey(mark)) {
                rezMap.put(mark, new HashMap<String, Long>());
            }
            Map<String, Long> map = rezMap.get(mark);
            map.put((String) result[1], (Long) result[2]);
        }

        return rezMap;
    }
}
