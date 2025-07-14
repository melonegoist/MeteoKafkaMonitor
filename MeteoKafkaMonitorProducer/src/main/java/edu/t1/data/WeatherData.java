package edu.t1.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WeatherData {

    private City city;
    private Condition condition;
    private LocalDateTime timestamp;
    private double temperature;
}
