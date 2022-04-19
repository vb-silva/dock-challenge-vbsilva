package com.dock.api.services;

import com.dock.api.dto.TransactionDto;
import com.dock.api.models.TransactionModel;
import com.dock.api.models.TransactionPage;
import com.dock.api.models.TransactionSearchCriteria;
import org.springframework.data.domain.Page;

public interface TransactionService {
    public TransactionModel createTransactionModel(TransactionDto transactionDto, long accountId);

    public TransactionModel save(TransactionModel transactionModel);

    public Page<TransactionModel> getAccountExtractWithFilter(long accountId, TransactionPage transactionPage, TransactionSearchCriteria transactionSearchCriteria);

    }