package com.service.gateway;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CacheTokenRepo extends CrudRepository<CacheToken, String> {

    Optional<CacheToken> findByAccessToken(String accessToken);
}
