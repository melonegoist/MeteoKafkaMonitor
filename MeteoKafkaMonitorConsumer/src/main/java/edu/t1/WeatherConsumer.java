package edu.t1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.t1.data.WeatherData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

public class WeatherConsumer {

    private static final String TOPIC = "weather-data-topic";
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static void main(String[] args) {
        Properties props =  new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "weather-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC));
            System.out.println("Consumer subscribed to topic " + TOPIC);

            Map<String, List<Double>> cityTemperature = new HashMap<>();
            Map<String, Integer> conditionCount = new HashMap<>();

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        WeatherData data = MAPPER.readValue(record.value(), WeatherData.class);

                        System.out.printf("""
                                Received weather update:
                                City: %s
                                Time: %s
                                Temp: %.1f°C
                                Condition: %s
                                Partition: %d
                                Offset: %d
                                --------------------------
                                """,
                                data.getCity(),
                                data.getTimestamp(),
                                data.getTemperature(),
                                data.getCondition(),
                                record.partition(),
                                record.offset()
                                );

                        cityTemperature.computeIfAbsent(String.valueOf(data.getCity()), k -> new ArrayList<>()).add(data.getTemperature());
                        conditionCount.merge(data.getCondition().name(), 1, Integer::sum);
                    } catch (Exception e) {
                        System.err.println("Error reading record: " + e.getMessage());
                    }
                }

                if (!records.isEmpty()) {
                    printStatistics(cityTemperature, conditionCount);
                }
            }
        }
    }

    private static void printStatistics(
            Map<String, List<Double>> cityTemperatures,
            Map<String, Integer> conditionCounts) {

        System.out.println("\n=== Current Statistics ===");

        // Средняя температура по городам
        cityTemperatures.forEach((city, temps) -> {
            double avg = temps.stream().mapToDouble(d -> d).average().orElse(0);
            System.out.printf("%s: Avg temp %.1f°C (%d samples)%n",
                    city, avg, temps.size());
        });

        // Количество погодных условий
        conditionCounts.forEach((condition, count) -> {
            System.out.printf("%s: %d times%n", condition, count);
        });

        System.out.println("========================\n");
    }
}
