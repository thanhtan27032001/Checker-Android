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
 * NOTE: using virtual delay() + counting advanceUntilIdle()/runCurrent() calls to "guess"
 * exactly when a coroutine is mid-execution is UNRELIABLE —
 * because MockK wraps suspend functions through several internal continuation layers,
 * the number of "ticks" needed is not fixed. A more reliable approach: use
 * CompletableDeferred as a manual "handshake" — the mock ACTIVELY signals when it has
 * started running, and WAITS until the test allows it to return a result. The test
 * knows with 100% certainty when it's safe to check state, without depending on
 * scheduler tick counts.
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
    fun `initial state is Ready and not checked in`() = runTest {
        val viewModel = CheckInViewModel(checkInStrategy)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem() as CheckInUiState.Ready
            assertEquals(AttendanceStatus.NOT_CHECKED_IN, state.status)
        }
    }

    @Test
    fun `successful check-in transitions to CHECKED_IN state`() = runTest {
        val fakeRecord = AttendanceRecord(
            id = "1",
            checkinTime = Clock.System.now(),
            checkoutTime = null,
            method = CheckInMethod.BUTTON,
            status = AttendanceStatus.CHECKED_IN,
        )
        // Manual "handshake": the mock signals it has been called (checkInCalled),
        // then WAITS for a signal from the test (letItFinish) before returning a result.
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
        checkInCalled.await() // ensure the strategy HAS been called and is waiting

        // At this point it's 100% certain: isSubmitting=true, because the mock is
        // "parked" inside performCheckin() and hasn't returned a result yet.
        val submittingState = viewModel.uiState.value as CheckInUiState.Ready
        assertTrue("Loading must show immediately after clicking the button", submittingState.isSubmitting)

        letItFinish.complete(Unit) // allow the mock to return its result
        testDispatcher.scheduler.advanceUntilIdle()

        val successState = viewModel.uiState.value as CheckInUiState.Ready
        assertEquals(AttendanceStatus.CHECKED_IN, successState.status)
        assertTrue("Loading must turn off once a result is received", !successState.isSubmitting)
    }

    @Test
    fun `failed check-in keeps the previous state instead of switching to Error`() = runTest {
        coEvery { checkInStrategy.performCheckin() } returns
                Result.failure(RuntimeException("Network error"))

        val viewModel = CheckInViewModel(checkInStrategy)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCheckInClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        val afterFailure = viewModel.uiState.value as CheckInUiState.Ready
        // Key point: it does NOT switch to CheckInUiState.Error,
        // it keeps the old status so the user doesn't lose screen context
        assertEquals(AttendanceStatus.NOT_CHECKED_IN, afterFailure.status)
        assertTrue(!afterFailure.isSubmitting)
    }

    @Test
    fun `clicking the button while submitting does not call the strategy again`() = runTest {
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
        checkInCalled.await() // ensure the 1st click HAS entered the strategy and is submitting=true

        viewModel.onCheckInClicked() // 2nd click while definitely submitting => must be blocked

        letItFinish.complete(Unit)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { checkInStrategy.performCheckin() }
    }
}