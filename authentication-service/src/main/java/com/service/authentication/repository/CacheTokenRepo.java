package com.service.authentication.repository;

import com.service.authentication.model.CacheToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheTokenRepo extends CrudRepository<CacheToken, String> {
}
