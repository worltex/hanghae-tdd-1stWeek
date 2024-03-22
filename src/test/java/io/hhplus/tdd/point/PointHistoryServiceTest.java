package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

    @InjectMocks
    PointHistoryService pointHistoryService;

    @InjectMocks
    PointService pointService;

    @Mock
    PointRepository pointRepository;

    @Mock
    PointHistoryRepository pointHistoryRepository;

 
    @Test
    public void 유저의_포인트를_등록하고_기록_조회한다() {
        //given
        Long userId=1L;
        Long amount=1000L;
        UserPoint testPoint= new UserPoint(userId,amount,0L);
        when(pointRepository.selectByUserId(any())).thenReturn(new UserPoint(userId,0L,0L));
        when(pointRepository.chargePoint(any(),any())).thenReturn(testPoint);
        List<PointHistory> testList = Arrays.asList(new PointHistory(0L,userId,amount,TransactionType.CHARGE, 0L));
        when(pointHistoryRepository.getPointHistoryByUserId(any())).thenReturn(testList);

        //when
        UserPoint userPoint = pointService.chargePoint(userId, amount);
        List<PointHistory> historyList = pointHistoryService.getPointHistoryByUserId(userId);


        //then
        assertThat(userPoint.point()).isEqualTo(amount);
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(historyList.size()).isEqualTo(1);
        assertThat(historyList.get(0).amount()).isEqualTo(amount);
        assertThat(historyList.get(0).type()).isEqualTo(TransactionType.CHARGE);
    }

    @Test
    public void 유저의_포인트를_등록및_사용하고_기록_조회한다() {
        //given
        Long userId=1L;
        Long amount=1000L;
        Long useAmount =500L;
        UserPoint testPoint= new UserPoint(userId,amount,0L);
        when(pointRepository.selectByUserId(any())).thenReturn(new UserPoint(userId,0L,0L));
        when(pointRepository.chargePoint(any(),any())).thenReturn(testPoint);

        when(pointRepository.selectByUserId(any())).thenReturn(testPoint);
        when(pointRepository.usePoint(any(),any())).thenReturn(new UserPoint(userId, amount-useAmount,0L));

        List<PointHistory> testList = Arrays.asList(new PointHistory(0L,userId,amount,TransactionType.CHARGE, 0L)
        ,new PointHistory(1L,userId,useAmount,TransactionType.USE, 0L));
        when(pointHistoryRepository.getPointHistoryByUserId(any())).thenReturn(testList);

        //when
        UserPoint chargePoint = pointService.chargePoint(userId, amount);
        UserPoint usePoint = pointService.usePoint(userId, useAmount);
        List<PointHistory> historyList = pointHistoryService.getPointHistoryByUserId(userId);


        //then
        assertThat(chargePoint.point()).isEqualTo(amount);
        assertThat(chargePoint.id()).isEqualTo(userId);
        assertThat(usePoint.point()).isEqualTo(useAmount);
        assertThat(usePoint.id()).isEqualTo(userId);
        assertThat(historyList.size()).isEqualTo(2);
        assertThat(historyList.get(0).amount()).isEqualTo(amount);
        assertThat(historyList.get(0).type()).isEqualTo(TransactionType.CHARGE);
        assertThat(historyList.get(1).amount()).isEqualTo(useAmount);
        assertThat(historyList.get(1).type()).isEqualTo(TransactionType.USE);
    }

}