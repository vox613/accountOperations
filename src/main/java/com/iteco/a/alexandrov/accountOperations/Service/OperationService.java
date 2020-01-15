package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.OperationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OperationService {
    Logger log = LoggerFactory.getLogger(OperationService.class);

    ResponseEntity<List<OperationEntity>> findAllOperationsFromAllAccounts();
    ResponseEntity<List<OperationEntity>> findAllOperationsFromAccountId(long id);
    ResponseEntity<?> findOperationIdFromAllAccounts(long id);
    ResponseEntity<?> findOperationIdFromAccountId(long idAccount, long idOperation);
    ResponseEntity<?> createOperation(OperationEntity operationEntity);

    ResponseEntity<?> executeOperation(OperationEntity newAccount);
}
