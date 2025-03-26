package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    final UserPointTable userPointTable;
    final PointHistoryTable pointHistoryTable;

    //포인트 조회
    public UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    //포인트 충전
    public UserPoint chargePoint(UserPoint current, long amount) {

        UserPoint userPoint = userPointTable.selectById(current.id());
        UserPoint charged = UserPoint.charge(userPoint, amount);

        userPointTable.insertOrUpdate(charged.id(),charged.point());

        pointHistoryTable.insert(charged.id(),amount,TransactionType.CHARGE, System.currentTimeMillis());

        return charged;
    }

    //포인트 사용
    public UserPoint usePoint(UserPoint current, long amount) {

        UserPoint userPoint = userPointTable.selectById(current.id());
        UserPoint used = UserPoint.use(userPoint, amount);

        userPointTable.insertOrUpdate(used.id(),used.point());

        pointHistoryTable.insert(used.id(),amount,TransactionType.USE, System.currentTimeMillis());

        return used;
    }
}
