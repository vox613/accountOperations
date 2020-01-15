package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.OperationEntity;
import com.iteco.a.alexandrov.accountOperations.Errors.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OperationServiceImpl implements OperationService {
    OperationRepository operationRepository;

    @Autowired
    public OperationServiceImpl(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }





    @Override
    public ResponseEntity<List<OperationEntity>> findAllOperationsFromAllAccounts() {
        return new ResponseEntity<>(operationRepository.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<OperationEntity>> findAllOperationsFromAccountId(long id) {
        return new ResponseEntity<>(operationRepository.findAllByAccountId(id), HttpStatus.OK);
    }



    @Override
    public ResponseEntity<?> findOperationIdFromAllAccounts(long id) {
        Optional<OperationEntity> operationEntityOptional = operationRepository.findById(id);
        if (!operationEntityOptional.isPresent()) {
            log.error("Operation with id {} not found.", id);
            return new ResponseEntity<>(
                    new CustomErrorResponse(String.format("Operation with id: %d not found!", id)),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(operationEntityOptional.get(), HttpStatus.OK);
    }



    @Override
    public ResponseEntity<?> findOperationIdFromAccountId(long idAccount, long idOperation) {
        Optional<OperationEntity> operationEntityOptional = operationRepository.findOperationEntityByIdAndAccountId(idOperation, idAccount);
        if (!operationEntityOptional.isPresent()) {
            log.error("Operation with id {} for account {} not found.", idOperation, idAccount);
            return new ResponseEntity<>(
                    new CustomErrorResponse(String.format("Operation with id: %d for account %d not found!", idOperation, idAccount)),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(operationEntityOptional.get(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createOperation(OperationEntity operationEntity) {
        return null;
    }

    @Override
    public ResponseEntity<?> executeOperation(OperationEntity newAccount) {
        return null;
    }
}
