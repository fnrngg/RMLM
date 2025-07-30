package ge.custom.rmlm.data.suspendrunners

import ge.custom.rmlm.common.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordingErrorsSuspendRunnerTest {

    @Test
    fun `invoke should return Success when execute function succeeds`() = runTest {
        val runner = RecordingErrorsSuspendRunner(dispatcher = UnconfinedTestDispatcher())
        val result = runner.invoke { 42 }
        println(result)
        assertTrue(result is Result.Success)
        assertEquals(42, (result as Result.Success).data)
    }

    @Test
    fun `invoke should throw CancellationException when execute function throws CancellationException`() = runTest {
        val runner = RecordingErrorsSuspendRunner(dispatcher = UnconfinedTestDispatcher())
        try {
            runner.invoke { throw CancellationException() }
        } catch (e: Exception) {
            assertTrue(e is CancellationException)
        }
    }

    @Test
    fun `invoke should return Error when execute function throws SecurityException`() = runTest {
        val runner = RecordingErrorsSuspendRunner(dispatcher = UnconfinedTestDispatcher())
        val result = runner.invoke { throw SecurityException() }
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is PermissionDeniedException)
    }

    @Test
    fun `invoke should return Error when execute function throws Exception`() = runTest {
        val runner = RecordingErrorsSuspendRunner(dispatcher = UnconfinedTestDispatcher())
        val result = runner.invoke { throw Exception() }
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception !is PermissionDeniedException)
    }

//    ...
}