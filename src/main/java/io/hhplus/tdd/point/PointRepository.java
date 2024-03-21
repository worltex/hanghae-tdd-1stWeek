package io.hhplus.tdd.point;

public interface PointRepository {

    UserPoint selectByUserId(Long userId);

    UserPoint chargePoint(Long userId, Long amount);

    UserPoint usePoint(Long userId, Long amount);
}
