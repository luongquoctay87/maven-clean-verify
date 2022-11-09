package com.service.gateway.repository;

import com.service.gateway.model.CacheToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheTokenRepo extends CrudRepository<CacheToken, String> {
}
