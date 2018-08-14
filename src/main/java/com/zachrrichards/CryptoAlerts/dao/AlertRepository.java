package com.zachrrichards.CryptoAlerts.dao;

import com.zachrrichards.CryptoAlerts.models.Alert;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends CrudRepository<Alert, Integer> {
    Alert findById(int id);
}
