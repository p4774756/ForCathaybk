package com.example.demo.service;

import com.example.demo.model.DailyForeignExchangeRates;
import com.example.demo.repository.DailyForeignExchangeRatesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.example.demo.util.Util.convertFromWebToDatabaseDateFormat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CronJobServiceTest {

    @Mock
    private DailyForeignExchangeRatesRepository dailyForeignExchangeRatesRepository;

    @MockBean
    private RestTemplate restTemplate;

    @InjectMocks
    private CronJobService cronJobService;

    @Value("${api.taifex.url}")
    private String webUrl;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // Manually inject the webUrl value into the cronJobService
        cronJobService.webUrl = webUrl;
    }

    @Test
    public void testPerformTask_SaveNewRecords() throws Exception {
        // Sample response body (JSON) from the API
        String jsonResponse = "[{\"Date\":\"20230912\",\"USD/NTD\":\"31.25\"}]";

        // Mock the RestTemplate response
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Accept-Language", "en-US,en;q=0.9");
        headers.set("User-Agent", "Mozilla/5.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(eq(webUrl), eq(HttpMethod.GET), eq(entity), eq(String.class)))
                .thenReturn(responseEntity);

        // Mock that no record exists for the given date
        when(dailyForeignExchangeRatesRepository.existsByDate(convertFromWebToDatabaseDateFormat("20230912"))).thenReturn(false);

        // Call the method under test
        cronJobService.performTask();

        // Verify that a record was saved
        verify(dailyForeignExchangeRatesRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testPerformTask_ExistingRecord() throws Exception {
        // Sample response body (JSON) from the API
        String jsonResponse = "[{\"Date\":\"20230912\",\"USD/NTD\":\"31.25\"}]";

        // Mock the RestTemplate response
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        headers.set("Accept", "application/json");
        headers.set("Accept-Language", "en-US,en;q=0.9");
        headers.set("User-Agent", "Mozilla/5.0");
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(eq(webUrl), eq(HttpMethod.GET), eq(entity), eq(String.class)))
                .thenReturn(responseEntity);

        // Mock that the record already exists for the given date
        when(dailyForeignExchangeRatesRepository.existsByDate(convertFromWebToDatabaseDateFormat("20230912"))).thenReturn(true);

        // Call the method under test
        cronJobService.performTask();

        // Capture the argument passed to saveAll
        ArgumentCaptor<List<DailyForeignExchangeRates>> captor = ArgumentCaptor.forClass(List.class);
        verify(dailyForeignExchangeRatesRepository).saveAll(captor.capture());

        // Verify that the saveAll method was called with an empty list
        List<DailyForeignExchangeRates> savedRecords = captor.getValue();
        assertTrue(savedRecords.isEmpty(), "Expected saveAll to be called with an empty list but it wasn't.");
    }

    @Test
    public void testPerformTask_EmptyApiResponse() throws Exception {
        // Mock an empty response body
        String jsonResponse = "[]";

        // Mock the RestTemplate response
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        headers.set("Accept", "application/json");
        headers.set("Accept-Language", "en-US,en;q=0.9");
        headers.set("User-Agent", "Mozilla/5.0");
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(eq(webUrl), eq(HttpMethod.GET), eq(entity), eq(String.class)))
                .thenReturn(responseEntity);

        // Call the method under test
        cronJobService.performTask();

        // Capture the argument passed to saveAll
        ArgumentCaptor<List<DailyForeignExchangeRates>> captor = ArgumentCaptor.forClass(List.class);
        verify(dailyForeignExchangeRatesRepository).saveAll(captor.capture());

        // Verify that the saveAll method was called with an empty list
        List<DailyForeignExchangeRates> savedRecords = captor.getValue();
        assertTrue(savedRecords.isEmpty(), "Expected saveAll to be called with an empty list but it wasn't.");
    }
}
