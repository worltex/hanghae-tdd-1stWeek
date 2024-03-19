package io.hhplus.tdd.point;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequestMapping("/point")
@RestController
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable Long id) throws InterruptedException {
        return pointService.getUserPointByUserId(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable Long id) {
        return  pointService.getPointHistoryByUserId(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public synchronized UserPoint charge(@PathVariable Long id, @RequestBody Long amount) throws InterruptedException {
        pointService.chargePoint(id,amount);
        return pointService.getUserPointByUserId(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public synchronized UserPoint use(@PathVariable Long id, @RequestBody Long amount) throws InterruptedException {
        pointService.usePoint(id,amount);
        return pointService.getUserPointByUserId(id);
    }
}
