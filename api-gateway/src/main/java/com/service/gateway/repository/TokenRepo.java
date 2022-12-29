package com.service.gateway.repository;

import com.service.gateway.model.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo extends CrudRepository<Token, String> {
}
