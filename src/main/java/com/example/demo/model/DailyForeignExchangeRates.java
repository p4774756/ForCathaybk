package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "DailyForeignExchangeRates")
public class DailyForeignExchangeRates {
    @Id
    private String id;
    private String currency;
    private String date;
    private BigDecimal amount;
}
