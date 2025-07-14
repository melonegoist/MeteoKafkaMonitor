package edu.t1.data;

import java.util.List;
import java.util.Random;

public enum Condition {
    SUNNY,
    CLOUDY,
    RAINY,
    SNOWY;

    private static final List<Condition> VALUES = List.of(values());
    private static final Random RANDOM = new Random();

    public static Condition getRandom() {
        return VALUES.get(RANDOM.nextInt(VALUES.size()));
    }
}