package com.example.QuantInvestigation.user.user_option;

import com.example.QuantInvestigation.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserOptionRepository extends JpaRepository<UserOption, Long> {

    @Query("select o.isRunning from UserOption o join o.user u where u.userId = :userId")
    Optional<Boolean> findIsRunningByUserId(@Param("userId") Long userId);

    @Query("select o from UserOption o where o.user.userId = :userId")
    Optional<UserOption> findUserOptionByUserId(@Param("userId") Long userId);

}
