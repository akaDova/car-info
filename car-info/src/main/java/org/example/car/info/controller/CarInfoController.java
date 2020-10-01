package org.example.car.info.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.car.info.service.CarService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CarInfoController {

    @Autowired
    public JobLauncher jobLauncher;

    @Autowired
    @Qualifier("registerFileJob")
    public Job registerFileJob;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public CarService carService;

    @RequestMapping("/hello")
    public String getHello() {
        return "hello";
    }

    @RequestMapping("/run")
    public String run() throws Exception {
        JobExecution jobExecution = jobLauncher.run(registerFileJob, new JobParameters());
        return jobExecution.getStatus().toString();
    }

    @GetMapping("/statistic/mark")
    @ResponseBody
    public Map<String, Long> getCarsCountPerMark() {
        return carService.getCarsCountPerMark();
    }

    @GetMapping("/statistic/year")
    @ResponseBody
    public Map<String, Long> getCarsPerYear() {
        return carService.getCarsCountPerYear();

    }

    @GetMapping("/statistic/model")
    @ResponseBody
    public Map<String, Map<String, Long>> getCarsPerModel() {
        return carService.getCarsCountPerModel();

    }

    @PostMapping("/car")
    public ResponseEntity<String> uploadFile(@RequestParam("file_path") String filePath) throws Exception {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("file", filePath)
                    .addDate("date", new Date())
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(registerFileJob, jobParameters);
            jobExecution.getFailureExceptions();
            if (jobExecution.getStatus().isUnsuccessful()) {
                List<Throwable> exceptions = jobExecution.getStepExecutions().stream().findAny()
                        .get().getFailureExceptions().stream()
                        .filter(x -> x.getClass().equals(ValidationException.class))
                        .collect(Collectors.toList());

                if (!exceptions.isEmpty()) {
                    return new ResponseEntity<>(exceptions.toString(), HttpStatus.BAD_REQUEST);
                } else {
                    return new ResponseEntity<>(jobExecution.getStatus().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            return new ResponseEntity<>(jobExecution.getStatus().toString(), HttpStatus.OK);
        } catch (JobParametersInvalidException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }
}
