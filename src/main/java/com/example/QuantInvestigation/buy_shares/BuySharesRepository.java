package com.example.QuantInvestigation.buy_shares;

import com.example.QuantInvestigation.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuySharesRepository extends JpaRepository<BuyShares, Long> {

    @Query("select count(b) from BuyShares b where b.user.userId = :userId")
    Integer findBuySharesCountByUserId(@Param("userId") Long userId);

    @Query("select b from BuyShares b where b.user.userId = :userId")
    List<BuyShares> findBuySharesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from BuyShares b where b.user.userId = :userId and :close >= b.price * (1.0 + :adjustmentFactor)")
    void deleteSoldShares(@Param("userId") Long userId, @Param("close") Float close, @Param("adjustmentFactor") Float adjustmentFactor);

}