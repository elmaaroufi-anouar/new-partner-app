package com.done.core.data.services.api

//import com.done.core.BuildConfig
import com.done.core.data.util.jsonWithUnknownKeys
import com.done.core.domain.services.auth_response.AuthResponseService
import com.done.core.domain.models.error_response.ErrorResponse
import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.NetworkErrorName
import com.done.core.domain.util.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlin.coroutines.cancellation.CancellationException

class KtorApiService(
    val httpClient: HttpClient,
    val authResponseService: AuthResponseService
) {

    suspend inline fun <reified Response : Any> post(
        route: String,
        body: Any? = null,
        headers: Map<String, String> = mapOf(),
        isRaw: Boolean = false,
        useDoneBaseUrl: Boolean = true
    ): Result<Response, NetworkError> {
        return safeCall {
            httpClient.post {
                if (useDoneBaseUrl) {
//                    url(BuildConfig.BASE_URL + "/" + route)
                } else {
                    url(route)
                }

                if (isRaw) {
                    contentType(ContentType.Application.Json)
                }

                setBody(body)

                header("Authorization", "Bearer ${authResponseService.getAuthToken()}")
                headers.forEach { (key, value) ->
                    header(key, value)
                }
            }
        }
    }

    suspend inline fun <reified Response : Any> put(
        route: String,
        body: Any? = null,
        queryParameters: Map<String, Any?> = mapOf(),
        headers: Map<String, String> = mapOf()
    ): Result<Response, NetworkError> {
        return safeCall(execute = {
            httpClient.put() {
//                url(BuildConfig.BASE_URL + "/" + route)

                queryParameters.forEach { (key, value) ->
                    parameter(key, value)
                }

                setBody(body)

                header("Authorization", "Bearer ${authResponseService.getAuthToken()}")
                headers.forEach { (key, value) ->
                    header(key, value)
                }
            }
        })
    }

    suspend inline fun <reified Response : Any> postFormData(
        route: String,
        fileName: String,
        fileBytes: ByteArray,
        requestBodyName: String,
        headers: Map<String, String> = mapOf()
    ): Result<Response, NetworkError> {

        return safeCall {
            httpClient.post {

//                url(BuildConfig.BASE_URL + "/" + route)

                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(requestBodyName, fileBytes, Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    ContentType.Application.OctetStream.toString()
                                )
                                append(HttpHeaders.ContentDisposition, "filename=$fileName")
                            })
                        }
                    )
                )

                header("Authorization", "Bearer ${authResponseService.getAuthToken()}")
                headers.forEach { (key, value) ->
                    header(key, value)
                }
            }
        }
    }

    suspend inline fun <reified Response : Any> get(
        baseUrl: String = "BuildConfig.BASE_URL",
        route: String,
        queryParameters: Map<String, Any?> = mapOf(),
        headers: Map<String, String> = mapOf(),
    ): Result<Response, NetworkError> {
        return safeCall(
            execute = {
                httpClient.get {

                    url("$baseUrl/$route")

                    queryParameters.forEach { (key, value) ->
                        parameter(key, value)
                    }

                    header("Authorization", "Bearer ${authResponseService.getAuthToken()}")
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                }
            }
        )
    }

    suspend inline fun <reified Response : Any> patch(
        route: String,
        body: HashMap<String, Any?> = hashMapOf(),
        headers: Map<String, String> = mapOf(),
        bodyJsonObject: JsonObject? = null,
        bodyJsonArray: JsonArray? = null
    ): Result<Response, NetworkError> {
        return safeCall {
            httpClient.patch {
//                url(BuildConfig.BASE_URL + "/" + route)

                setBody(body)

                bodyJsonObject?.let { setBody(bodyJsonObject) }

                bodyJsonArray?.let { setBody(bodyJsonArray) }

                header("Authorization", "Bearer ${authResponseService.getAuthToken()}")
                headers.forEach { (key, value) ->
                    header(key, value)
                }
            }
        }
    }

    suspend inline fun <reified T> safeCall(
        execute: () -> HttpResponse
    ): Result<T, NetworkError> {
        val response = try {
            execute()
        } catch (e: UnresolvedAddressException) {
            e.printStackTrace()
            return Result.Error(NetworkError(NetworkErrorName.NO_INTERNET_ERROR))
        } catch (e: SerializationException) {
            e.printStackTrace()
            return Result.Error(
                NetworkError(NetworkErrorName.SERIALIZATION_ERROR)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            return Result.Error(NetworkError(NetworkErrorName.UNKNOWN))
        }

        return responseToResult(response)
    }

    suspend inline fun <reified D> responseToResult(response: HttpResponse): Result<D, NetworkError> {
        val contentType = response.headers["Content-Type"]?.lowercase() ?: ""
        if (contentType.contains("text/html")) {
            return Result.Error(
                NetworkError(NetworkErrorName.SERIALIZATION_ERROR)
            )
        }

        var message: String? = null
        if (response.status.value >= 300) {
            val bodyText = response.bodyAsText()
            if (bodyText.isNotBlank()) {
                try {
                    println("networkErrorToast: $bodyText")
                    val responseMessage = jsonWithUnknownKeys.decodeFromString<ErrorResponse>(bodyText)
                    message = responseMessage.message
                } catch (e: Exception) {
                    e.printStackTrace()
                    message = null
                }
            }
        }

        return when (response.status.value) {
            in 200..299 -> Result.Success(response.body<D>())
            in 300..308 -> Result.Error(
                NetworkError(
                    NetworkErrorName.REDIRECTION_ERROR,
                    response.status.value,
                    message
                )
            )

            in 400..499 -> Result.Error(
                NetworkError(
                    NetworkErrorName.CLIENT_ERROR,
                    response.status.value,
                    message
                )
            )

            in 500..599 -> Result.Error(
                NetworkError(
                    NetworkErrorName.SERVER_ERROR,
                    response.status.value,
                    message
                )
            )

            else -> Result.Error(NetworkError(NetworkErrorName.UNKNOWN, response.status.value))
        }
    }
}