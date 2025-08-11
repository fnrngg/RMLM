package ge.custom.rmlm.data.suspendrunners.errorhandlers

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RecordingIOErrorsHandlerTest {

    @Test
    fun `invoke should return Error when execute function throws SecurityException`() = runTest {
        val handler = RecordingIOErrorsHandler()
        val result = handler.handleError(SecurityException())
        assertTrue(result.exception is PermissionDeniedException)
    }

    @Test
    fun `invoke should return Error when execute function throws Exception`() = runTest {
        val handler = RecordingIOErrorsHandler()
        val result = handler.handleError(NullPointerException())
        assertTrue(result.exception is NullPointerException)
    }
}