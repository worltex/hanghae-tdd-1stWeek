package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Test
    public void 유저의_포인트를_조회한다() {
        //given
        Long userId=1L;
        UserPoint testPoint = new UserPoint(userId,0L,0L);
        when(pointRepository.selectByUserId(any())).thenReturn(testPoint);

        //when
        UserPoint userPoint = pointService.getUserPointByUserId(userId);

        //then
        assertThat(userPoint.point()).isEqualTo(0L);
        assertThat(userPoint.id()).isEqualTo(userId);
    }

    @Test
    public void 유저의_포인트를_등록한다() {
        //given
        Long userId=1L;
        Long amount=1000L;
        UserPoint testPoint = new UserPoint(userId,0L,0L);
        UserPoint updatedPoint = new UserPoint(userId,amount,0L);
        when(pointRepository.selectByUserId(any())).thenReturn(testPoint);
        when(pointRepository.chargePoint(any(),any())).thenReturn(updatedPoint);

        //when
        UserPoint userPoint = pointService.chargePoint(userId,amount);

        //then
        assertThat(userPoint.point()).isEqualTo(amount);
        assertThat(userPoint.id()).isEqualTo(userId);
    }


    @Test
    public void 유저의_포인트를_등록하고_사용한다() {
        //given
        Long userId=1L;
        Long amount=1000L;
        Long useAmount =500L;
        Long remainingPoint = amount-useAmount;
        UserPoint testPoint = new UserPoint(userId,0L,0L);
        UserPoint chargedPoint = new UserPoint(userId,amount,0L);
        UserPoint updatedPoint = new UserPoint(userId,useAmount,0L);
        when(pointRepository.selectByUserId(any())).thenReturn(testPoint).thenReturn(chargedPoint);
        when(pointRepository.chargePoint(any(),any())).thenReturn(chargedPoint);
        when(pointRepository.usePoint(any(),any())).thenReturn(updatedPoint);

        //when
        pointService.chargePoint(userId,amount);
        UserPoint userPoint= pointService.usePoint(userId, useAmount);

        //then
        assertThat(userPoint.point()).isEqualTo(remainingPoint);
        assertThat(userPoint.id()).isEqualTo(userId);
    }


    @Test
    public void 유저의_포인트를_등록하고_등록한_포인트보다_많이_사용할경우_에러() {
        //given
        Long userId=1L;
        Long amount=1000L;
        Long useAmount =1500L;
        UserPoint testPoint = new UserPoint(userId,0L,0L);
        UserPoint chargedPoint = new UserPoint(userId,amount,0L);
        when(pointRepository.selectByUserId(any())).thenReturn(testPoint);
        when(pointRepository.chargePoint(any(),any())).thenReturn(chargedPoint);

        //when
        pointService.chargePoint(userId,amount);

        //then
        assertThrows(RuntimeException.class, ()->pointService.usePoint(userId, useAmount));
    }
}