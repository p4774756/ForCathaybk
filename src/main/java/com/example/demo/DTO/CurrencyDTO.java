package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CurrencyDTO {
    private String date;
    private BigDecimal usd;
}
