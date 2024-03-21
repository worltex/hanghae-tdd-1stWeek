package io.hhplus.tdd.point;

import java.util.List;

public interface PointHistoryRepository {

    List<PointHistory> getPointHistoryByUserId(Long userId);

    void addPointHistory(Long userId, Long amount, TransactionType transactionType, long updateTime);
}
