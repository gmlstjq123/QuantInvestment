package com.example.QuantInvestigation.user.user_option;

import com.example.QuantInvestigation.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOption {
    @Column
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId; // 고유 식별자

    @Column(nullable = false, columnDefinition = "INT DEFAULT 10") // 기본 값 : 10
    private Integer divisions; // 분할 수 (8 ~ 10)

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE") // 기본 값 : True
    private Boolean isRunning; // 매매 실행 or 중단 여부

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0") // 기본 값 : 0
    private Integer T; // 기준 T 값

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
