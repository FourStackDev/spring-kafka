package org.fourstack.springkafkaproducer.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

public class RandomIdGenerator {

    private static final String[] alpha = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static String generateRandomId() {
        LocalDateTime current = LocalDateTime.now(ZoneId.of("UTC"));
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(dynamicAlpha(new Random().nextInt(3)))
                .append(getYear(current))
                .append("-")
                .append(getMonth(current))
                .append(dynamicAlpha(new Random().nextInt(2)))
                .append(getDay(current))
                .append("-")
                .append(getHour(current))
                .append(dynamicAlpha(new Random().nextInt(1)))
                .append(getMinute(current))
                .append("-")
                .append(getSecond(current))
                .append(dynamicAlpha(new Random().nextInt(3)))
                .append("-")
                .append(getNanoSecond(current));
        return strBuilder.toString();
    }

    private static String dynamicAlpha(int numberOfTimes) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i <= numberOfTimes; i++) {
            strBuilder.append(alpha[new Random().nextInt(26)]);
        }
        return strBuilder.toString();
    }

    private static int getYear(LocalDateTime time) {
        return time.getYear();
    }

    private static int getMonth(LocalDateTime time) {
        return time.getMonthValue();
    }

    private static int getDay(LocalDateTime time) {
        return time.getDayOfMonth();
    }

    private static int getHour(LocalDateTime time) {
        return time.getHour();
    }

    private static int getMinute(LocalDateTime time) {
        return time.getMinute();
    }

    private static int getSecond(LocalDateTime time) {
        return time.getSecond();
    }

    private static int getNanoSecond(LocalDateTime time) {
        return time.getNano();
    }
}
