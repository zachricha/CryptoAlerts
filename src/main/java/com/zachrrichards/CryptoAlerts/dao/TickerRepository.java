package com.zachrrichards.CryptoAlerts.dao;

import com.zachrrichards.CryptoAlerts.models.Ticker;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TickerRepository extends CrudRepository<Ticker, Integer> {
    Ticker findBySymbol(String symbol);
    @Query(value = "SELECT u FROM Ticker u")
    List<Ticker> findAllTickers(Sort sort);
}
