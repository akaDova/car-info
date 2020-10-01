package org.example.car.info.config.batch;

import org.example.car.info.model.Car;
import org.example.car.info.dao.CarRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public CarRepository carRepository;

    @Value("${home.dir}")
    public String homeDir;

    protected static class CarItemProcessor implements ItemProcessor<Car, Car> {

        @Override
        public Car process(Car item) throws Exception {
            return item;
        }
    }

    protected static class CarFieldSetMapper implements FieldSetMapper<Car> {

        @Override
        public Car mapFieldSet(FieldSet fieldSet) {

            if (fieldSet == null) {
                return null;
            }

            var car = new Car();
//            "<id>", "<car mark>", "<car model>", "<year>", "<color>", "<current owner>", "<old owners>"
            car.setId(fieldSet.readString("<id>"));
            car.setMark(fieldSet.readString("<car mark>"));
            car.setModel(fieldSet.readString("<car model>"));
            car.setYear(fieldSet.readString("<year>"));
            car.setColor(fieldSet.readString("<color>"));
            car.setCurrentOwner(fieldSet.readString("<current owner>"));
            car.setOldOwners(fieldSet.readString("<old owners>"));

            return car;
        }
    }

    public static class InvalidItemsListener implements ItemProcessListener<Car, Car> {

        @Override
        public void beforeProcess(Car car) {
        }

        @Override
        public void afterProcess(Car car, Car result) {
            if (result == null) {
                System.out.println(car + " has been filtered because it is invalid");
            }
        }

        @Override
        public void onProcessError(Car car, Exception e) {
            System.out.println(car + " is invalid due to " + e.getMessage());
        }
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Car> reader(@Value("#{jobParameters[file]}") String filePath) {
        return new FlatFileItemReaderBuilder<Car>()
                .name("carItemReader")
                .resource(new FileSystemResource(Path.of(homeDir + "/" + filePath)))
                .delimited()
                .delimiter(", ")
                .names("<id>", "<car mark>", "<car model>", "<year>", "<color>", "<current owner>", "<old owners>")
                .fieldSetMapper(new CarFieldSetMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Car, Car> processor() {
        var validatingItemProcessor = new BeanValidatingItemProcessor<Car>();

        return validatingItemProcessor;
    }

    @Bean
    public ItemWriter<Car> writer() {
        return new RepositoryItemWriterBuilder<Car>()
                .repository(carRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step importFileToFileStep() {
        return stepBuilderFactory.get("importFileToFileStep")
                .<Car, Car>chunk(10)
                .reader(reader(null))
                .listener(new InvalidItemsListener())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean("registerFileJob")
    public Job registerFileJob() {
        return jobBuilderFactory.get("registerFileJob")
                .validator(parameters -> {
                    String fileName = parameters.getString("file");

                    if (fileName != null) {
                        if (fileName.endsWith(".txt")) {
                            try {
                                String fileType = Files.probeContentType(Paths.get(homeDir + "/" + fileName));
                                if (!fileType.startsWith("text")) {
                                    throw new JobParametersInvalidException("file must be text format");
                                }
                            } catch (IOException e) {
                                throw new JobParametersInvalidException("cannot open file: " + e);
                            }
                        } else {
                            throw new JobParametersInvalidException("file format must be .txt");
                        }
                    } else {
                        throw new JobParametersInvalidException("file parameter is null");
                    }
                })
                .flow(importFileToFileStep())
                .end()
                .build();
    }
}
