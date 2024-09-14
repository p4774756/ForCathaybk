package com.example.demo.repository;

import com.example.demo.model.DailyForeignExchangeRates;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DailyForeignExchangeRatesRepository extends MongoRepository<DailyForeignExchangeRates, String> {

    @Query("{ 'currency': { $regex: ?0, $options: 'i' }, "+
            "'date': { $gte: ?1, $lte: ?2 } }")
    List<DailyForeignExchangeRates> findByCurrencyAndDateRange(String currency, String startDate, String endDate);

    boolean existsByDate(String date); // Method to check if a document exists by date
}
