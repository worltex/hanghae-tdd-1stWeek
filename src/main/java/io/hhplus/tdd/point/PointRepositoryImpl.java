package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {
 
    private final UserPointTable userPointTable;

    @Override
    public UserPoint selectByUserId(Long userId) {
       return userPointTable.selectById(userId);
    }

    @Override
    public UserPoint chargePoint(Long userId, Long amount) {
        return userPointTable.insertOrUpdate(userId, amount);
    }

    @Override
    public UserPoint usePoint(Long userId, Long amount) {
        return userPointTable.insertOrUpdate(userId,amount);
    }
}
