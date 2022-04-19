package com.dock.api.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ACCOUNT")
@NoArgsConstructor
@Getter
@Setter
public class AccountModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long accountId;

    @Column(nullable = false)
    private long personId;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private BigDecimal dailyWithdrawalLimit;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private LocalDateTime creationUTC;

}