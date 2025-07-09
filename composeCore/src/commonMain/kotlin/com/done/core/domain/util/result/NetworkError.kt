package com.done.core.domain.util.result

open class NetworkError(
    val name: NetworkErrorName,
    val code: Int = 0,
    val message: String? = null
)

enum class NetworkErrorName {
    CLIENT_ERROR,
    REDIRECTION_ERROR,
    SERVER_ERROR,
    NO_INTERNET_ERROR,
    SERIALIZATION_ERROR,
    UNKNOWN
}


const val UNPROCESSABLE_ENTITY_CODE: Int = 422