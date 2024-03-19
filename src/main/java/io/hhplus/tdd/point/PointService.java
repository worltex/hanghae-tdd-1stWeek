package io.hhplus.tdd.point;

import java.util.List;

public interface PointService {

    UserPoint getUserPointByUserId(Long userId) throws InterruptedException;

    List<PointHistory> getPointHistoryByUserId(Long userId);

    UserPoint chargePoint(Long userId, Long amount) throws InterruptedException;

    UserPoint usePoint(Long userId, Long amount) throws InterruptedException;
}
