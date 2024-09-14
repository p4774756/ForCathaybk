package com.example.demo.service;

import com.example.demo.model.DailyForeignExchangeRates;
import com.example.demo.repository.DailyForeignExchangeRatesRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import static com.example.demo.util.Util.convertFromWebToDatabaseDateFormat;

@Service
public class CronJobService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    DailyForeignExchangeRatesRepository dailyForeignExchangeRatesRepository;
    @Value("${api.taifex.url}")
    String webUrl;

    public void performTask() {
        try {
            // Fetch exchange rates
            List<Map<String, String>> exchangeRates = fetchExchangeRates();

            // Process and save exchange rates
            processAndSaveExchangeRates(exchangeRates);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Map<String, String>> fetchExchangeRates() throws Exception {
        // Set custom headers to mimic a browser
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Accept-Language", "en-US,en;q=0.9");
        headers.set("User-Agent", "Mozilla/5.0");

        // Create an HttpEntity with the headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Send GET request with headers
        ResponseEntity<String> response = restTemplate.exchange(webUrl, HttpMethod.GET, entity, String.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), new TypeReference<>() {});
    }

    private void processAndSaveExchangeRates(List<Map<String, String>> exchangeRates) {
        List<DailyForeignExchangeRates> dailyForeignExchangeRates = new ArrayList<>();

        exchangeRates.forEach(e -> {
            try {
                String dateStr = convertFromWebToDatabaseDateFormat(e.get("Date"));

                // Skip if record for the date already exists
                if (dailyForeignExchangeRatesRepository.existsByDate(dateStr)) {
                    System.out.println("Document for date " + dateStr + " already exists. Skipping save.");
                    return;
                }

                DailyForeignExchangeRates usdDaily = new DailyForeignExchangeRates();
                usdDaily.setDate(dateStr);
                usdDaily.setAmount(new BigDecimal(e.get("USD/NTD")));
                usdDaily.setCurrency("USD/NTD");

                dailyForeignExchangeRates.add(usdDaily);

            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        });

        dailyForeignExchangeRatesRepository.saveAll(dailyForeignExchangeRates);
    }
}
