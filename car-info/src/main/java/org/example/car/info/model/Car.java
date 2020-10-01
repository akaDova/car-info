package org.example.car.info.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @NotNull(message = "id is null")
    @Pattern(regexp = "^\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}$", message = "id does not match the pattern")
    private String id;

    @NotEmpty(message = "mark is empty")
    private String mark;

    @NotEmpty(message = "model is empty")
    private String model;

    @NotEmpty(message = "color is empty")
    private String color;

    @NotNull(message = "year is null")
    @Pattern(regexp = "\\d{4}", message = "year does not match the pattern")
    private String year;

    @NotEmpty(message = "currentOwner is empty")
    private String currentOwner;

    private String oldOwners;

    public Car() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(String currentOwner) {
        this.currentOwner = currentOwner;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setOldOwners(String oldOwners) {
        this.oldOwners = oldOwners;
    }

    public String getOldOwners() {
        return oldOwners;
    }
}


