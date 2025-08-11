package ge.custom.rmlm.data.suspendrunners.errorhandlers

import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultErrorHandlerTest {

    @Test
    fun `handleError should return Error with the same exception`() = runTest {
        val handler = DefaultErrorHandler()
        val exception = NullPointerException()
        val result = handler.handleError(exception)
        assert(exception == result.exception)
    }
}