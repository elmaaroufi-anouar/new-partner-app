package com.done.core.domain.util.result

import com.done.core.domain.models.pagination.Pagination


sealed class Result<D, E : NetworkError>(
    val data: D? = null,
    val pagination: Pagination? = null,
    val error: NetworkError? = null,
) {
    class Success<D>(data: D?, pagination: Pagination? = null) : Result<D, NetworkError>(data, pagination)

    class Error<D>(error: NetworkError?) : Result<D, NetworkError>(null, null, error)
}