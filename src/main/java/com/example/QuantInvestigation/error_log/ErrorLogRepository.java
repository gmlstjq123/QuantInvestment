package com.example.QuantInvestigation.error_log;

import com.example.QuantInvestigation.user.User;
import com.example.QuantInvestigation.user.dto.GetErrorLogReq;
import com.example.QuantInvestigation.user.dto.GetErrorLogRes;
import com.example.QuantInvestigation.user.dto.RefreshTokenReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {

    @Query("SELECT new com.example.QuantInvestigation.user.dto.GetErrorLogReq(e.date, e.time, e.message) FROM ErrorLog e where e.user.userId = :userId")
    List<GetErrorLogReq> findGetErrorLogReq(@Param("userId") Long userId);

}
