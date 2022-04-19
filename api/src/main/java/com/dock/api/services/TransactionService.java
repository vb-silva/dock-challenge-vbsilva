package com.dock.api.services;

import com.dock.api.dto.TransactionDto;
import com.dock.api.models.TransactionModel;

public interface TransactionService {
    public TransactionModel createTransactionModel(TransactionDto transactionDto, long accountId);

    public TransactionModel save(TransactionModel transactionModel);
}