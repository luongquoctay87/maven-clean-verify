package com.service.gateway.service;

import com.service.gateway.model.CacheToken;
import com.service.gateway.repository.CacheTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheTokenService {

    @Autowired
    private CacheTokenRepo cacheTokenRepo;

    public CacheToken findById(String id) {
        return cacheTokenRepo.findById(id).get();
    }
}
