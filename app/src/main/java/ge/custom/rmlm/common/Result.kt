package ge.custom.rmlm.common


sealed class Result<T> {
    data class Success<T>(
        val data: T
    ) : Result<T>()

    data class Error(
        val errorMessage: String?
    ): Result<Nothing>()

    data object Loading : Result<Nothing>()
}