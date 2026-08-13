package com.gaden.checkin

import app.cash.turbine.test
import com.gaden.checkin.domain.model.AttendanceRecord
import com.gaden.checkin.domain.model.AttendanceStatus
import com.gaden.checkin.domain.model.CheckInMethod
import com.gaden.checkin.domain.model.CheckInStrategy
import com.gaden.checkin.presentation.checkin.CheckInUiState
import com.gaden.checkin.presentation.checkin.CheckInViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Clock

/**
 * LƯU Ý: dùng delay() ảo + đếm số lần advanceUntilIdle()/runCurrent() để "đoán"
 * đúng thời điểm coroutine đang ở giữa chừng là cách KHÔNG ĐÁNG TIN CẬY —
 * vì MockK bọc suspend function qua nhiều lớp continuation nội bộ, số lần
 * "tick" cần thiết không cố định. Cách chắc chắn hơn: dùng CompletableDeferred
 * làm "cần gạt" thủ công — mock CHỦ ĐỘNG báo khi nó đã bắt đầu chạy, và
 * CHỜ đến khi test cho phép mới trả kết quả. Test biết chính xác 100%
 * khi nào an toàn để kiểm tra trạng thái, không phụ thuộc đếm scheduler tick.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CheckInViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var checkInStrategy: CheckInStrategy

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        checkInStrategy = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `khoi tao thanh cong thi trang thai la Ready va chua check-in`() = runTest {
        val viewModel = CheckInViewModel(checkInStrategy)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem() as CheckInUiState.Ready
            assertEquals(AttendanceStatus.NOT_CHECKED_IN, state.status)
        }
    }

    @Test
    fun `bam check-in thanh cong thi chuyen sang trang thai CHECKED_IN`() = runTest {
        val fakeRecord = AttendanceRecord(
            id = "1",
            checkinTime = Clock.System.now(),
            checkoutTime = null,
            method = CheckInMethod.BUTTON,
            status = AttendanceStatus.CHECKED_IN,
        )
        // "Cần gạt" thủ công: mock báo hiệu đã được gọi (checkInCalled),
        // rồi CHỜ tín hiệu từ test (letItFinish) mới trả kết quả.
        val checkInCalled = CompletableDeferred<Unit>()
        val letItFinish = CompletableDeferred<Unit>()
        coEvery { checkInStrategy.performCheckin() } coAnswers {
            checkInCalled.complete(Unit)
            letItFinish.await()
            Result.success(fakeRecord)
        }

        val viewModel = CheckInViewModel(checkInStrategy)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCheckInClicked()
        testDispatcher.scheduler.advanceUntilIdle()
        checkInCalled.await() // đảm bảo strategy ĐÃ được gọi và đang chờ

        // Tại đây chắc chắn 100%: isSubmitting=true, vì mock đang "đứng chờ"
        // ngay bên trong performCheckin(), chưa trả kết quả.
        val submittingState = viewModel.uiState.value as CheckInUiState.Ready
        assertTrue("Phải hiện loading ngay khi bấm nút", submittingState.isSubmitting)

        letItFinish.complete(Unit) // cho phép mock trả kết quả
        testDispatcher.scheduler.advanceUntilIdle()

        val successState = viewModel.uiState.value as CheckInUiState.Ready
        assertEquals(AttendanceStatus.CHECKED_IN, successState.status)
        assertTrue("Loading phải tắt sau khi có kết quả", !successState.isSubmitting)
    }

    @Test
    fun `check-in that bai thi giu nguyen trang thai cu khong chuyen sang Error`() = runTest {
        coEvery { checkInStrategy.performCheckin() } returns
                Result.failure(RuntimeException("Network lỗi"))

        val viewModel = CheckInViewModel(checkInStrategy)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCheckInClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        val afterFailure = viewModel.uiState.value as CheckInUiState.Ready
        // Đây là điểm quan trọng: KHÔNG chuyển sang CheckInUiState.Error,
        // vẫn giữ status cũ để người dùng không mất context màn hình
        assertEquals(AttendanceStatus.NOT_CHECKED_IN, afterFailure.status)
        assertTrue(!afterFailure.isSubmitting)
    }

    @Test
    fun `bam nut khi dang submitting thi khong goi strategy lan nua`() = runTest {
        val fakeRecord = AttendanceRecord(
            id = "1",
            checkinTime = Clock.System.now(),
            checkoutTime = null,
            method = CheckInMethod.BUTTON,
            status = AttendanceStatus.CHECKED_IN,
        )
        val checkInCalled = CompletableDeferred<Unit>()
        val letItFinish = CompletableDeferred<Unit>()
        coEvery { checkInStrategy.performCheckin() } coAnswers {
            checkInCalled.complete(Unit)
            letItFinish.await()
            Result.success(fakeRecord)
        }

        val viewModel = CheckInViewModel(checkInStrategy)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCheckInClicked()
        testDispatcher.scheduler.advanceUntilIdle()
        checkInCalled.await() // đảm bảo lần bấm 1 ĐÃ vào trong strategy, đang isSubmitting=true

        viewModel.onCheckInClicked() // bấm lần 2 lúc chắc chắn đang submitting => phải bị chặn

        letItFinish.complete(Unit)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { checkInStrategy.performCheckin() }
    }
}