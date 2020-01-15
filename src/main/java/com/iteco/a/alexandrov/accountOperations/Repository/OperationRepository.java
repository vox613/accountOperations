package com.iteco.a.alexandrov.accountOperations.Repository;

import com.iteco.a.alexandrov.accountOperations.Entity.OperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, Long> {

    List<OperationEntity> findAllByAccountId(long id);

    Optional<OperationEntity> findOperationEntityByIdAndAccountId(long operationId, long accountId);
}
