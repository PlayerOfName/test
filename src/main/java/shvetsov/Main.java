package shvetsov;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(new File("C:\\Java\\test\\src\\main\\resources\\tickets.json"));
            JsonNode ticketsNode = rootNode.path("tickets");

            Map<String, Integer> minFlightTimes = new HashMap<>();

            List<Integer> prices = new ArrayList<>();

            for (JsonNode ticketNode : ticketsNode) {
                String origin = ticketNode.path("origin").asText();
                String destination = ticketNode.path("destination").asText();

                if (origin.equals("VVO") && destination.equals("TLV")) {
                    String carrier = ticketNode.path("carrier").asText();
                    String departureTime = ticketNode.path("departure_time").asText();
                    String arrivalTime = ticketNode.path("arrival_time").asText();
                    int price = ticketNode.path("price").asInt();

                    int flightTime = calculateFlightTime(departureTime, arrivalTime);

                    minFlightTimes.merge(carrier, flightTime, Math::min);

                    prices.add(price);

                    System.out.println("Минимальное время полета для каждого перевозчика:");
                    for (Map.Entry<String, Integer> entry : minFlightTimes.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue() + " минут");
                    }


                    double averagePrice = prices.stream().mapToInt(Integer::intValue).average().orElse(0);
                    double medianPrice = calculateMedian(prices);

                    System.out.println("Разница между средней ценой и медианой: " + (averagePrice - medianPrice));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Метод для расчета времени полета в минутах
    private static int calculateFlightTime(String departureTime, String arrivalTime) {
        String[] depTime = departureTime.split(":");
        String[] arrTime = arrivalTime.split(":");

        int depHour = Integer.parseInt(depTime[0]);
        int depMinute = Integer.parseInt(depTime[1]);
        int arrHour = Integer.parseInt(arrTime[0]);
        int arrMinute = Integer.parseInt(arrTime[1]);

        int depTotalMinutes = depHour * 60 + depMinute;
        int arrTotalMinutes = arrHour * 60 + arrMinute;

        // Корректируем расчет на случай перелета через полночь
        if (arrTotalMinutes < depTotalMinutes) {
            arrTotalMinutes += 24 * 60;
        }

        return arrTotalMinutes - depTotalMinutes;
    }

    // Метод для расчета медианы
    private static double calculateMedian(List<Integer> prices) {
        List<Integer> sortedPrices = prices.stream().sorted().collect(Collectors.toList());
        int size = sortedPrices.size();

        if (size % 2 == 0) {
            return (sortedPrices.get(size / 2 - 1) + sortedPrices.get(size / 2)) / 2.0;
        } else {
            return sortedPrices.get(size / 2);
        }
    }
}