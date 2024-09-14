package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude fields with null values
public class ForexResponse {
    private ErrorResponse error;
    private List<CurrencyDTO> currency;

    // You might want to add this constructor to handle error cases
    public ForexResponse(ErrorResponse error) {
        this.error = error;
        this.currency = null;
    }
}
