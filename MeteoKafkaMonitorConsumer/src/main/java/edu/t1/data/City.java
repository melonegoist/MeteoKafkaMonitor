package edu.t1.data;

import java.util.List;
import java.util.Random;

public enum City {
    MOSCOW,
    ST_PETERSBURG,
    MAGADAN,
    TYUMEN,
    SOCHI;

    private static final List<City> VALUES = List.of(values());
    private static final Random RANDOM = new Random();

    public static City getRandom() {
        return VALUES.get(RANDOM.nextInt(VALUES.size()));
    }
}
