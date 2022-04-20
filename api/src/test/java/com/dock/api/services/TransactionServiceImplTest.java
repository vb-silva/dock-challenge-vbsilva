package com.dock.api.services;

import com.dock.api.dto.TransactionDto;
import com.dock.api.models.TransactionModel;
import com.dock.api.models.TransactionPage;
import com.dock.api.models.TransactionSearchCriteria;
import com.dock.api.repositories.TransactionCriteriaRepository;
import com.dock.api.repositories.TransactionRepository;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {
    private TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private TransactionCriteriaRepository transactionCriteriaRepository = mock(TransactionCriteriaRepository.class);
    private Clock clock = mock(Clock.class);
    private TransactionServiceImpl transactionService = new TransactionServiceImpl(transactionRepository, transactionCriteriaRepository, clock);

    private final static LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2022, 01, 13, 0, 0, 0, 0);

    @Test
    void createDepositTransactionModel() {
        var validTransactionDto = new TransactionDto(1000, "DEPOSIT");
        var validAccountId = 1L;
        var expectedAmount = BigDecimal.valueOf(validTransactionDto.getAmount()/100.0);

        var expectedTransactionModel = new TransactionModel();
        expectedTransactionModel.setAmount(expectedAmount);
        expectedTransactionModel.setAccountId(validAccountId);
        expectedTransactionModel.setTransactionTimeUTC(LOCAL_DATE_TIME);

        doReturn(LOCAL_DATE_TIME.toInstant(ZoneOffset.UTC)).when(clock).instant();
        doReturn(ZoneOffset.UTC).when(clock).getZone();

        var transactionModel = transactionService.createTransactionModel(validTransactionDto, validAccountId);
        assertThat(expectedTransactionModel).usingRecursiveComparison().isEqualTo(transactionModel);

        verify(clock, times(1)).instant();
        verify(clock, times(1)).getZone();
    }

    @Test
    void createWithdrawTransactionModel() {
        var validTransactionDto = new TransactionDto(1000, "WITHDRAW");
        var validAccountId = 1L;
        var expectedAmount = BigDecimal.valueOf(-1*validTransactionDto.getAmount()/100.0);

        var expectedTransactionModel = new TransactionModel();
        expectedTransactionModel.setAmount(expectedAmount);
        expectedTransactionModel.setAccountId(validAccountId);
        expectedTransactionModel.setTransactionTimeUTC(LOCAL_DATE_TIME);

        doReturn(LOCAL_DATE_TIME.toInstant(ZoneOffset.UTC)).when(clock).instant();
        doReturn(ZoneOffset.UTC).when(clock).getZone();

        var transactionModel = transactionService.createTransactionModel(validTransactionDto, validAccountId);
        assertThat(expectedTransactionModel).usingRecursiveComparison().isEqualTo(transactionModel);

        verify(clock, times(1)).instant();
        verify(clock, times(1)).getZone();
    }

    @Test
    void saveShouldRunSuccessfully() {
        var expectedAmount = BigDecimal.valueOf(1000/100.0);

        var validTransactionModel = new TransactionModel();
        validTransactionModel.setAmount(expectedAmount);
        validTransactionModel.setAccountId(1L);
        validTransactionModel.setTransactionTimeUTC(LOCAL_DATE_TIME);

        var expectedTransactionModel = validTransactionModel;
        expectedTransactionModel.setTransactionId(1L);

        when(transactionRepository.save(eq(validTransactionModel))).thenReturn(expectedTransactionModel);

        var transactionModel = transactionService.save(validTransactionModel);
        assertThat(expectedTransactionModel).usingRecursiveComparison().ignoringFields("transactionId").isEqualTo(transactionModel);
        verify(transactionRepository, times(1)).save(validTransactionModel);
    }

    @Test
    void getAccountExtractWithFilterShouldRunSuccessfully() {
        var validAccountId = 1L;
        var transactionPage = new TransactionPage();
        var startDate = "2022-04-20 12:30:00";
        var endDate = "2022-04-21 12:30:00";
        var transactionSearchCriteria =  new TransactionSearchCriteria();
        transactionSearchCriteria.setStartDate(startDate);
        transactionSearchCriteria.setEndDate(endDate);
        transactionSearchCriteria.setAccountId(validAccountId);

        List<TransactionModel> list = Collections.emptyList();
        var expectedPage = new PageImpl<>(list);

        when(transactionCriteriaRepository.getAccountExtractWithFilter(transactionPage, transactionSearchCriteria)).thenReturn(expectedPage);
        var pages = transactionService.getAccountExtractWithFilter(validAccountId, transactionPage, transactionSearchCriteria);

        assertThat(expectedPage).usingRecursiveComparison().ignoringFields("transactionId").isEqualTo(pages);
        verify(transactionCriteriaRepository, times(1)).getAccountExtractWithFilter(transactionPage, transactionSearchCriteria);
    }
}