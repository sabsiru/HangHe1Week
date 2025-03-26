package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public static UserPoint charge(UserPoint current, long amount) {
        long newPoint = current.point() + amount;
        if (newPoint > 100_000) {
            throw new IllegalArgumentException("충전 가능한 최대 포인트는 100,000입니다.");
        }
        return new UserPoint(current.id(), newPoint, System.currentTimeMillis());
    }

    public static UserPoint use(UserPoint current, long amount) {
        long newPoint = current.point() - amount;
         if(newPoint<0){
             throw new IllegalArgumentException("포인트가 부족합니다.");
         }
        return new UserPoint(current.id(), newPoint, System.currentTimeMillis());
    }
}
