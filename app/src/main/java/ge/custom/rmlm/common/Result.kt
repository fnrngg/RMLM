package ge.custom.rmlm.common


sealed class Result<out T> {

    fun <R> map(mapper: (T) -> R): Result<R> = when (this) {
        is Success -> Success(mapper(data))
        is Error -> Error(exception)
        Loading -> Loading
    }
    data class Success<T>(
        val data: T
    ) : Result<T>()

    data class Error(
        val exception: Exception
    ): Result<Nothing>()

    data object Loading : Result<Nothing>()
}