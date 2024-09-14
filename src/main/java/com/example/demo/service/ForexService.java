package com.example.demo.service;

import com.example.demo.DTO.CurrencyDTO;
import com.example.demo.DTO.ForexQueryDTO;
import com.example.demo.model.DailyForeignExchangeRates;
import com.example.demo.repository.DailyForeignExchangeRatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.util.Util.convertFromQueryToDatabaseDateFormat;

@Service
public class ForexService {
    @Autowired
    private DailyForeignExchangeRatesRepository dailyForeignExchangeRatesRepository;

    public List<CurrencyDTO> performTask(ForexQueryDTO query) {
        List<DailyForeignExchangeRates> usdHistories = null;
        try {
            System.out.println("startDate:" + convertFromQueryToDatabaseDateFormat(query.getStartDate()));
            System.out.println("endDate:" + convertFromQueryToDatabaseDateFormat(query.getEndDate()));
            System.out.println("currency:" + query.getCurrency());
            usdHistories = dailyForeignExchangeRatesRepository.findByCurrencyAndDateRange(query.getCurrency(),
                    convertFromQueryToDatabaseDateFormat(query.getStartDate()),
                    convertFromQueryToDatabaseDateFormat(query.getEndDate()));
        } catch (Exception e ){
            e.printStackTrace();
        }

        List<CurrencyDTO> currencyDTOList = usdHistories.stream().
                map(usdHistory ->
                        new CurrencyDTO(usdHistory.getDate().toString(),
                                usdHistory.getAmount()))
                .collect(Collectors.toList());

        return currencyDTOList;
    }
}
