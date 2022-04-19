package com.dock.api.services;

import com.dock.api.dto.TransactionDto;
import com.dock.api.models.TransactionModel;
import com.dock.api.models.TransactionTypeModel;
import com.dock.api.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements  TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public TransactionModel createTransactionModel(TransactionDto transactionDto, long accountId) {
        long amountE2 = transactionDto.getAmount();
        TransactionTypeModel transactionType = TransactionTypeModel.valueOf(transactionDto.getTransactionType());
        BigDecimal amount = transactionType == TransactionTypeModel.DEPOSIT ? BigDecimal.valueOf(amountE2/100.0) : BigDecimal.valueOf(amountE2 * -1/100.0);
        LocalDateTime transactionTimeUTC = LocalDateTime.now(ZoneId.of("UTC"));

        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setAccountId(accountId);
        transactionModel.setAmount(amount);
        transactionModel.setTransactionTimeUTC(transactionTimeUTC);
        return transactionModel;
    }

    @Transactional
    public TransactionModel save(TransactionModel transactionModel) {
        return transactionRepository.save(transactionModel);
    }
}