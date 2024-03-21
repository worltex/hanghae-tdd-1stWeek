package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryTable pointHistoryTable;

    @Override
    public List<PointHistory> getPointHistoryByUserId(Long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    @Override
    public void addPointHistory(Long userId, Long amount, TransactionType transactionType, long updateTime) {
        pointHistoryTable.insert(userId,amount,transactionType,updateTime);
    }
}
