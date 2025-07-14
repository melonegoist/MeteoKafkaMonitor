package edu.t1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.t1.data.City;
import edu.t1.data.Condition;
import edu.t1.data.WeatherData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherProducer {

    private static final String TOPIC = "weather-data-topic";

    public static double getRandomTemperature() {
        Random rand = new Random();

        return Math.round(rand.nextDouble() * 100.0) % 30;
    }

    public static void main(String[] args) {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            WeatherData data = new WeatherData(City.getRandom(), Condition.getRandom(), LocalDateTime.now(), getRandomTemperature());

            try {
                String json = mapper.writeValueAsString(data);

                producer.send(new ProducerRecord<>(TOPIC, data.getCity().toString(), json), (recordMetadata, e) -> {

                    if (e == null) {
                        System.out.println("OK");
                    } else {
                        System.out.println(e.getMessage());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            producer.close();
            System.out.println("Shutting down");
        }));
    }

}
