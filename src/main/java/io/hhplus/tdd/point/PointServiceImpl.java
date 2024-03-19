package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService{
    private final UserPointTable userPointTable;

    private final PointHistoryTable pointHistoryTable;
    @Override
    public UserPoint getUserPointByUserId(Long userId) {
        return userPointTable.selectById(userId);
    }

    @Override
    public List<PointHistory> getPointHistoryByUserId(Long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    @Override
    public UserPoint chargePoint(Long userId, Long amount) {
        UserPoint userPoint = userPointTable.selectById(userId);
        UserPoint result = userPointTable.insertOrUpdate(userId, userPoint.point()+amount);
        pointHistoryTable.insert(userId,amount,TransactionType.CHARGE,result.updateMillis());
        return result;
    }

    @Override
    public UserPoint usePoint(Long userId, Long amount) {
        UserPoint userPoint = userPointTable.selectById(userId);
        if(userPoint.point()<amount){
            throw new RuntimeException("point 부족합니다.");
        }
        long updatedAmount = userPoint.point() - amount;
        pointHistoryTable.insert(userId,amount,TransactionType.USE,userPoint.updateMillis());
        return userPointTable.insertOrUpdate(userId,updatedAmount);
    }
}
