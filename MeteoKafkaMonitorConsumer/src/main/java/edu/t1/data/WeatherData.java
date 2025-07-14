package edu.t1.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class WeatherData {

    private City city;
    private Condition condition;
    private LocalDateTime timestamp;
    private double temperature;

    @JsonCreator
    public WeatherData(
            @JsonProperty("city") City city,
            @JsonProperty("condition") Condition condition,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("temperature")  double temperature) {
        this.city = city;
        this.condition = condition;
        this.timestamp = timestamp;
        this.temperature = temperature;
    }
}
