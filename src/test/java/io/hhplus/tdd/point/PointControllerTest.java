package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PointControllerTest {

    private PointHistoryTable pointHistoryTable = new PointHistoryTable();
    private UserPointTable userPointTable = new UserPointTable();
    private PointService pointService = new PointServiceImpl(userPointTable,pointHistoryTable);
    private PointController pointController = new PointController(pointService);


    @Test
    public void 충전하고_사용이후_사용이력_조회() throws InterruptedException{
        //given
        Long userId=1L;
        Long amount=1000L;
        Long useAmount =500L;

        //when
        pointController.charge(userId, amount);
        pointController.use(userId,useAmount);
        List<PointHistory> history = pointController.history(userId);

        //then
        assertThat(history.size()).isEqualTo(2);
        assertThat(history.get(0).type()).isEqualTo(TransactionType.CHARGE);
        assertThat(history.get(0).amount()).isEqualTo(amount);
        assertThat(history.get(1).type()).isEqualTo(TransactionType.USE);
        assertThat(history.get(1).amount()).isEqualTo(useAmount);
    }

    @Test
    public void 충전_동시요청_순차처리() throws InterruptedException{
        //given
        Long userId=1L;
        Long amount=1000L;
        Long totalAmount=3000L;
        int numberOfThread=3;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        CountDownLatch doneSignal = new CountDownLatch(numberOfThread);
        ExecutorService executorService = new ScheduledThreadPoolExecutor(numberOfThread);

        //when
        chargePoint(userId,amount,successCount,failCount,doneSignal,executorService);
        chargePoint(userId,amount,successCount,failCount,doneSignal,executorService);
        chargePoint(userId,amount,successCount,failCount,doneSignal,executorService);
        doneSignal.await();
        executorService.shutdown();
        UserPoint result = pointController.point(userId);

        //then
        assertAll(
                () -> assertThat(successCount.get()).isEqualTo(3),
                () -> assertThat(failCount.get()).isEqualTo(0)
        );
        assertThat(result.point()).isEqualTo(totalAmount);
    }


    @Test
    public void 충전하고_포인트사용_동시요청_금액이내_요청은_성공_금액초과_요청은_실패() throws InterruptedException{
        //given
        Long userId=1L;
        Long amount=1000L;
        Long useAmount =500L;
        Long exceedAMount =600L;
        int numberOfThread=2;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        CountDownLatch doneSignal = new CountDownLatch(numberOfThread);
        ExecutorService executorService = new ScheduledThreadPoolExecutor(numberOfThread);

        //when
        pointController.charge(userId, amount);
        usePoint(userId, exceedAMount, successCount, failCount, doneSignal, executorService);
        usePoint(userId, useAmount, successCount, failCount, doneSignal, executorService);
        doneSignal.await();
        executorService.shutdown();

        //then
        assertAll(
                () -> assertThat(successCount.get()).isEqualTo(1),
                () -> assertThat(failCount.get()).isEqualTo(1)
        );
    }

    private void chargePoint(Long userId, Long amount, AtomicInteger successCount, AtomicInteger failCount, CountDownLatch doneSignal, ExecutorService executorService) {
        executorService.submit(()-> {
            try {
                pointController.charge(userId,amount);
                successCount.getAndIncrement();
            } catch (Exception e) {
                failCount.getAndIncrement();
            }finally {
                doneSignal.countDown();
            }
        });
    }

    private void usePoint(Long userId, Long useAmount, AtomicInteger successCount, AtomicInteger failCount, CountDownLatch doneSignal, ExecutorService executorService) {
        executorService.submit(()-> {
            try {
                pointController.use(userId,useAmount);
                successCount.getAndIncrement();
            } catch (Exception e) {
                failCount.getAndIncrement();
            }finally {
                doneSignal.countDown();
            }
        });
    }
}
