package com.done.partner.domain.repositories.auth

import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.Result

interface AuthRepository {
    suspend fun login(
        email: String, password: String
    ): Result<Unit, NetworkError>

    suspend fun isLoggedIn(): Boolean

    suspend fun logout(isFromCTA: Boolean)
}