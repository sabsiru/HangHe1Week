package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    final UserPointTable userPointTable;

    public UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }
}
