package com.example.QuantInvestigation.history;

import com.example.QuantInvestigation.user.dto.GetErrorLogReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

}
