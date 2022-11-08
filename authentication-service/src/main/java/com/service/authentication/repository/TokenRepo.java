package com.service.authentication.repository;

import com.service.authentication.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {
    Token findByToken(String refreshToken);

    @Query(value = "SELECT * FROM tbl_token r WHERE r.user_id=:userId", nativeQuery = true)
    Token findByUserId(long userId);

}
