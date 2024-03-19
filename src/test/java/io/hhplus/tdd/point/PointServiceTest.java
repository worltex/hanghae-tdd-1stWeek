package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PointServiceTest {

    private PointService pointService;

    private UserPointTable userPointTable = new UserPointTable();
    private PointHistoryTable pointHistoryTable = new PointHistoryTable();

    @BeforeEach
    void setUp() {
        this.pointService=new PointServiceImpl(userPointTable,pointHistoryTable);
    }

    @Test
    public void 유저의_포인트를_조회한다() throws InterruptedException {
        //given
        Long userId=1L;

        //when
        UserPoint userPoint = pointService.getUserPointByUserId(userId);

        //then
        assertThat(userPoint.point()).isEqualTo(0L);
        assertThat(userPoint.id()).isEqualTo(userId);
    }

    @Test
    public void 유저의_포인트를_등록한다() throws InterruptedException {
        //given
        Long userId=1L;
        Long amount=1000L;

        //when
        UserPoint userPoint = pointService.chargePoint(userId,amount);

        //then
        assertThat(userPoint.point()).isEqualTo(amount);
        assertThat(userPoint.id()).isEqualTo(userId);
    }


    @Test
    public void 유저의_포인트를_등록하고_사용한다() throws InterruptedException {
        //given
        Long userId=1L;
        Long amount=1000L;
        Long useAmount =500L;
        Long remainingPoint = amount-useAmount;

        //when
        pointService.chargePoint(userId,amount);
        UserPoint userPoint= pointService.usePoint(userId, useAmount);

        //then
        assertThat(userPoint.point()).isEqualTo(remainingPoint);
        assertThat(userPoint.id()).isEqualTo(userId);
    }


    @Test
    public void 유저의_포인트를_등록하고_등록한_포인트보다_많이_사용할경우_에러() throws InterruptedException {
        //given
        Long userId=1L;
        Long amount=1000L;
        Long useAmount =1500L;

        //when
        pointService.chargePoint(userId,amount);

        //then
        assertThrows(RuntimeException.class, ()->pointService.usePoint(userId, useAmount));
    }

    @Test
    public void 유저의_포인트를_등록하고_기록_조회한다() throws InterruptedException {
        //given
        Long userId=1L;
        Long amount=1000L;

        //when
        UserPoint userPoint = pointService.chargePoint(userId, amount);
        List<PointHistory> historyList = pointService.getPointHistoryByUserId(userId);


        //then
        assertThat(userPoint.point()).isEqualTo(amount);
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(historyList.size()).isEqualTo(1);
        assertThat(historyList.get(0).amount()).isEqualTo(amount);
        assertThat(historyList.get(0).type()).isEqualTo(TransactionType.CHARGE);
    }

    @Test
    public void 유저의_포인트를_등록및_사용하고_기록_조회한다() throws InterruptedException {
        //given
        Long userId=1L;
        Long amount=1000L;
        Long useAmount =500L;

        //when
        UserPoint chargePoint = pointService.chargePoint(userId, amount);
        UserPoint usePoint = pointService.usePoint(userId, useAmount);
        List<PointHistory> historyList = pointService.getPointHistoryByUserId(userId);


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