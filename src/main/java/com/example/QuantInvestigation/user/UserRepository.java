package com.example.QuantInvestigation.user;

import com.example.QuantInvestigation.user.dto.RefreshTokenReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.userId = :userId")
    Optional<User> findUserByUserId(@Param("userId") Long userId);

    @Query("select u from User u where u.id = :id")
    Optional<User> findUserById(@Param("id") String id);

    @Query("select u.accessToken from User u where u.userId = :userId")
    Optional<String> findAccessTokenByUserId(@Param("userId") Long userId);

    @Query("select u.appKey from User u where u.userId = :userId")
    Optional<String> findAppKeyByUserId(@Param("userId") Long userId);

    @Query("select u.appSecret from User u where u.userId = :userId")
    Optional<String> findAppSecretByUserId(@Param("userId") Long userId);

    @Query("select u.accountNumber from User u where u.userId = :userId")
    Optional<String> findAccountNumByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.example.QuantInvestigation.user.dto.RefreshTokenReq(u.userId, u.appKey, u.appSecret) FROM User u")
    Optional<List<RefreshTokenReq>> findRefreshTokenReq();

    @Modifying
    @Query("UPDATE User u SET u.accessToken = :accessToken WHERE u.userId = :userId")
    void updateAccessTokenByUserId(@Param("userId") Long userId,  @Param("accessToken") String accessToken);

}
