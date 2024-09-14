package com.example.demo.controller;

import com.example.demo.DTO.CurrencyDTO;
import com.example.demo.DTO.ForexQueryDTO;
import com.example.demo.service.ForexService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ForexControllerTest {

    @Mock
    private ForexService forexService;

    @InjectMocks
    private ForexController forexController;

    private MockMvc mockMvc;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Test
    public void testSearchForex_ValidDates() throws Exception {
        // Setup MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(forexController).build();

        // Mock the service call
        List<CurrencyDTO> mockResponse = Collections.emptyList();
        when(forexService.performTask(Mockito.any(ForexQueryDTO.class)))
                .thenReturn(mockResponse);

        // Get dynamic dates
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusDays(7);
        LocalDate twoWeekAgo = today.minusWeeks(2);
        String startDate = twoWeekAgo.format(DATE_FORMATTER);
        String endDate = oneWeekAgo.format(DATE_FORMATTER);

        // Create valid request body with dynamic dates
        String validRequest = String.format("{ \"startDate\": \"%s\", \"endDate\": \"%s\" }", startDate, endDate);

        // Perform POST request
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error.code").value("0000"))
                .andExpect(jsonPath("$.error.message").value("成功“"));
    }

    @Test
    public void testSearchForex_InvalidDates() throws Exception {
        // Setup MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(forexController).build();

        // Get dynamic dates
        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);
        String startDate = oneYearAgo.format(DATE_FORMATTER);
        String endDate = today.format(DATE_FORMATTER);

        // Create invalid request body (startDate after endDate)
        String invalidRequest = String.format("{ \"startDate\": \"%s\", \"endDate\": \"%s\" }", endDate, startDate);

        // Perform POST request
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error.code").value("E001"))
                .andExpect(jsonPath("$.error.message").value("日期區間不符"));
    }

    @Test
    public void testSearchForex_InvalidDateFormat() throws Exception {
        // Setup MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(forexController).build();

        // Create request body with invalid date format
        String invalidFormatRequest = "{ \"startDate\": \"09-13-2023\", \"endDate\": \"09-14-2023\" }";

        // Perform POST request
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidFormatRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error.code").value("E001"))
                .andExpect(jsonPath("$.error.message").value("Invalid date format"));
    }
}
