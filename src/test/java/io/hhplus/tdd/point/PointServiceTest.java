package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointService pointService;

    @Test
    public void 사용자의_포인트_조회_성공() throws Exception{
        //given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());
        when(userPointTable.selectById(1L)).thenReturn(userPoint);

        //when
        UserPoint result = pointService.getUserPoint(userPoint.id());

        //then
        assertEquals(5000L,result.point());
        assertEquals(1L,result.id());

    }
    @Test
    public void 존재하지_않는_사용자는_empty_포인트_반환() throws Exception{
        //given
        long id = 99L;
        when(userPointTable.selectById(id)).thenReturn(UserPoint.empty(id));

        //when
        UserPoint result = pointService.getUserPoint(id);

        //then
        assertEquals(0,result.point());
        assertEquals(99L,result.id());

    }

    @Test
    public void 포인트_충전_성공() throws Exception{
        //given
        UserPoint current = new UserPoint(1L, 1000L, System.currentTimeMillis());
        when(userPointTable.selectById(1L)).thenReturn(current);

        //when
        UserPoint result = pointService.chargePoint(current, 3000L);

        //then
        assertEquals(4000L,result.point());
        assertEquals(1L,result.id());

    }

    @Test
    public void 충전시_최대한도_초과시_예외발생() throws Exception{
        //given
        UserPoint current = new UserPoint(1L, 90000L, System.currentTimeMillis());
        when(userPointTable.selectById(1L)).thenReturn(current);

        //when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> pointService.chargePoint(current, 10001L));

        //then
        assertEquals("충전 가능한 최대 포인트는 100,000입니다.", e.getMessage());
    }
    
    @Test
    public void 충전_성공시_히스토리_저장_성공() throws Exception{
        //given
        UserPoint current = new UserPoint(1L, 1000L, System.currentTimeMillis());

        when(userPointTable.selectById(1L)).thenReturn(current);

        //when
        UserPoint charged = pointService.chargePoint(current, 3000L);

        //then
        verify(pointHistoryTable, times(1)).insert(
                eq(1L),
                eq(3000L),
                eq(TransactionType.CHARGE),
                anyLong()
        );
    }

    @Test
    public void 포인트_사용_성공() throws Exception{
        //given
        UserPoint current = new UserPoint(1L, 5000L, System.currentTimeMillis());
        when(userPointTable.selectById(1L)).thenReturn(current);

        //when
        UserPoint used = pointService.usePoint(current, 3000L);

        //then
        assertEquals(2000L,used.point());
        assertEquals(1L,used.id());
    }
    
    @Test
    public void 포인트_사용시_잔고가_0미만일시_예외발생() throws Exception{
        //given
        UserPoint current = new UserPoint(1L, 1000L, System.currentTimeMillis());
        when(userPointTable.selectById(1L)).thenReturn(current);

        //when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> pointService.usePoint(current, 1001L));

        //then
        assertEquals("포인트가 부족합니다.", e.getMessage());
    }

    @Test
    public void 포인트_사용성공시_히스토리_저장_성공() throws Exception{
        //given
        UserPoint current = new UserPoint(1L, 4000L, System.currentTimeMillis());
        when(userPointTable.selectById(1L)).thenReturn(current);

        //when
        UserPoint used = pointService.usePoint(current, 4000L);

        //then
        verify(pointHistoryTable, times(1)).insert(
                eq(1L),
                eq(4000L),
                eq(TransactionType.USE),
                anyLong()
        );
    }

}