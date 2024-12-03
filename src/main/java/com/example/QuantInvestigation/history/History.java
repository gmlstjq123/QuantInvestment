package com.example.QuantInvestigation.history;

import com.example.QuantInvestigation.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class History extends BaseTimeEntity {

    @Column
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId; // 고유 식별자


}
