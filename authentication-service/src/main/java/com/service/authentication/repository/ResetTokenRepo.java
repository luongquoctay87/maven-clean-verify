package com.service.authentication.repository;

import com.service.authentication.model.ResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetTokenRepo extends CrudRepository<ResetToken, String> {
}
