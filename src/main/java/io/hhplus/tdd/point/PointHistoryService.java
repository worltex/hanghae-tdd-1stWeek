package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService{
    private final PointHistoryRepository pointHistoryRepository;

    public List<PointHistory> getPointHistoryByUserId(Long userId) {
        return pointHistoryRepository.getPointHistoryByUserId(userId);
    }

    public void addPointHistory(Long userId, Long amount, TransactionType transactionType, long updateTime){
        pointHistoryRepository.addPointHistory(userId,amount,transactionType,updateTime);
    }


}
