package io.hhplus.tdd.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    public UserPoint getUserPointByUserId(Long userId) {
        return pointRepository.selectByUserId(userId);
    }


    public UserPoint chargePoint(Long userId, Long amount) {
        if (amount < 0) {
            throw new RuntimeException("금액은 0원 이상이어야합니다.");
        }
        UserPoint userPoint = pointRepository.selectByUserId(userId);
        Long updatedAmount = amount + userPoint.point();
        return pointRepository.chargePoint(userId, updatedAmount);
    }
 
    public UserPoint usePoint(Long userId, Long amount) {
        if (amount < 0) {
            throw new RuntimeException("금액은 0원 이상이어야합니다.");
        }
        UserPoint userPoint = pointRepository.selectByUserId(userId);
        if (userPoint.point() < amount) {
            throw new RuntimeException("point 부족합니다.");
        }
        long updatedAmount = userPoint.point() - amount;
        return pointRepository.usePoint(userId, updatedAmount);
    }
}
