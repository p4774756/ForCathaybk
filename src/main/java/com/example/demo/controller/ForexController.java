package com.example.demo.controller;

import com.example.demo.DTO.CurrencyDTO;
import com.example.demo.DTO.ErrorResponse;
import com.example.demo.DTO.ForexQueryDTO;
import com.example.demo.DTO.ForexResponse;
import com.example.demo.repository.DailyForeignExchangeRatesRepository;
import com.example.demo.service.ForexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/")
public class ForexController {

    @Autowired
    private DailyForeignExchangeRatesRepository dailyForeignExchangeRatesRepository;
    @Autowired
    private ForexService forexService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @PostMapping
    public ForexResponse  searchForex(@RequestBody ForexQueryDTO query) {
        ErrorResponse errorResponse = validateDates(query);
        if (errorResponse != null) {
            return new ForexResponse(errorResponse);
        }

        List<CurrencyDTO> usdDailyHistories = forexService.performTask(query);

        errorResponse = new ErrorResponse("0000", "成功“");
        ForexResponse response = new ForexResponse(errorResponse, usdDailyHistories);

        return response;
    }

    private ErrorResponse validateDates(ForexQueryDTO query) {
        try {
            LocalDate startDate = LocalDate.parse(query.getStartDate(), DATE_FORMATTER);
            LocalDate endDate = LocalDate.parse(query.getEndDate(), DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            LocalDate oneYearAgo = today.minusYears(1);
            LocalDate yesterday = today.minusDays(1);

            if (startDate.isBefore(oneYearAgo) || startDate.isAfter(today) ||
                endDate.isAfter(yesterday) || endDate.isBefore(startDate)) {
                return new ErrorResponse("E001", "日期區間不符");
            }
        } catch (Exception e) {
            return new ErrorResponse("E001", "Invalid date format");
        }

        return null;
    }
}
