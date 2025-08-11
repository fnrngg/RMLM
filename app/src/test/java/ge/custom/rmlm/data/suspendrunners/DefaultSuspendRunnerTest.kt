package ge.custom.rmlm.data.suspendrunners

import ge.custom.rmlm.common.Result
import ge.custom.rmlm.data.suspendrunners.errorhandlers.ErrorHandler
import ge.custom.rmlm.domain.usecase.base.SuspendRunner
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

class DefaultSuspendRunnerTest {
    private lateinit var defaultSuspendRunner: SuspendRunner
    private lateinit var errorHandler: ErrorHandler

    @Before
    fun setUp() {
        errorHandler = mockk {
            coEvery { handleError(any()) } answers { Result.Error(firstArg()) }
        }
        defaultSuspendRunner = DefaultSuspendRunner(
            dispatcher = UnconfinedTestDispatcher(),
            errorHandler = errorHandler
        )
    }

    @Test
    fun `return success when suspend function doesn't throw exception`() = runTest {
        val res = defaultSuspendRunner.invoke {
            true
        }
        coVerify { errorHandler wasNot called }
        assert(res is Result.Success)
        assert((res as Result.Success).data)
    }

    @Test
    fun `return error when suspend function throws exception`() = runTest {
        val exception = NullPointerException()
        val res = defaultSuspendRunner.invoke {
            throw exception
        }
        coVerify { errorHandler.handleError(exception) }
        assert(res is Result.Error)
        assert((res as Result.Error).exception == exception)
    }

    @Test
    fun `throw cancellationException when suspend function throws cancellationException exception`() =
        runTest {
            try {
                defaultSuspendRunner.invoke {
                    throw CancellationException()
                }
                assert(false)
            } catch (_: CancellationException) {
                assert(true)
            } catch (_: Exception) {
                assert(false)
            }
            coVerify { errorHandler wasNot called }
        }
}