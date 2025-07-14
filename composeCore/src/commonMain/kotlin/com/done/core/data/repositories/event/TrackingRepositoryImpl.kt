package com.done.core.data.repositories.event

//import com.done.core.BuildConfig
import com.done.core.data.dto.event.toTrackingEventDto
import com.done.core.data.services.api.KtorApiService
import com.done.core.data.util.ApiRoutes
import com.done.core.domain.models.event.TrackingEvent
import com.done.core.domain.repositories.event.TrackingRepository
import com.done.core.domain.repositories.remote_config.RemoteConfigRepository
import com.done.core.domain.services.auth_response.AuthResponseService
import com.done.core.domain.services.device_info.DeviceInfoService
import com.done.core.domain.services.language.LanguageService
import kotlinx.coroutines.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TrackingRepositoryImpl(
    private val apiService: KtorApiService,
    private val authResponseService: AuthResponseService,
    private val languageService: LanguageService,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val deviceInfoService: DeviceInfoService
) : TrackingRepository {

    private suspend fun shouldSendEvents(): Boolean = remoteConfigRepository.getConfig()?.partnerSendAppEvents == true

    override suspend fun trackOpenAppEvent(
        appStartDuration: String,
        launchSuccess: String,
        errorCount: String,
        errorType: String
    ) {
        if (!shouldSendEvents()) return
        trackEvent(
            eventName = "OpenApp",
            eventParams = mapOf(
                "app_start_duration" to appStartDuration,
                "launch_success" to launchSuccess,
                "error_count" to errorCount,
                "error_type" to errorType
            )
        )
    }

    override suspend fun trackLoginEvent(
        emailAddress: String,
        loginMethod: String
    ) {
        if (!shouldSendEvents()) return
        trackEvent(
            eventName = "Login",
            eventParams = mapOf(
                "login_method" to loginMethod
            )
        )
    }

    override suspend fun trackLogoutEvent(
        isFromCTA: Boolean
    ) {
        if (!shouldSendEvents()) return
        trackEvent(
            eventName = "Logout",
            eventParams = mapOf(
                "type" to if (isFromCTA) "Logout button click" else "Unauthenticated error"
            )
        )
    }

    override suspend fun trackAcceptOrderEvent(
        orderCreated: String,
        acceptanceTimestamp: String,
        orderId: String,
        customerId: String,
        orderItems: Int,
        orderValue: String,
        estimatedPrepTime: String,
        specialInstructions: String,
    ) {
        if (!shouldSendEvents()) return
        trackEvent(
            eventName = "AcceptOrder",
            eventParams = mapOf(
                "order_created" to orderCreated,
                "acceptance_timestamp" to acceptanceTimestamp,
                "order_id" to orderId,
                "customer_id" to customerId,
                "order_items" to orderItems.toString(),
                "order_value" to orderValue,
                "estimated_prep_time" to estimatedPrepTime,
                "special_instructions" to specialInstructions
            )
        )
    }

    override suspend fun trackOrderIsReadyEvent(
        readyTimestamp: String,
        originalEstimatedReadyTime: String,
        restaurantId: String,
        orderId: String,
        driverId: String,
    ) {
        if (!shouldSendEvents()) return
        trackEvent(
            eventName = "OrderIsReady",
            eventParams = mapOf(
                "ready_timestamp" to readyTimestamp,
                "original_estimated_ready_time" to originalEstimatedReadyTime,
                "restaurant_id" to restaurantId,
                "order_id" to orderId,
                "driver_id" to driverId
            )
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun trackEvent(
        eventName: String,
        eventParams: Map<String, String?> = emptyMap()
    ) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val ipInfo = async { deviceInfoService.getIpInfo() }
            val location = authResponseService.getStoreLocation()

            val extraParams = mapOf(
                "deviceType" to deviceInfoService.getDeviceType(),
                "operationSystem" to deviceInfoService.getReleaseVersion(),
                "appVersion" to BuildConfig.VERSION_NAME,
                "partnerId" to authResponseService.getStoreId(),
                "sessionId" to deviceInfoService.getSessionId(),
                "ipAddress" to ipInfo.await()?.ipAddress,
                "networkType" to deviceInfoService.getNetworkType(),
                "internetProvider" to ipInfo.await()?.internetProvider,
                "deviceFingerprint" to deviceInfoService.getAndroidDeviceId(),
                "language" to languageService.getCurrentLanguage(),
                "country" to ipInfo.await()?.country,
                "timezone" to ipInfo.await()?.timezone,
                "latitude" to location?.lat,
                "longitude" to location?.long,
                "appInstallSource" to deviceInfoService.getAppInstallSource(),
                "batteryLevel" to deviceInfoService.getBatteryPercentage().toString(),
                "pushNotificationsEnabled" to deviceInfoService.hasNotificationPermission().toString()
            )

            val trackingEvent = TrackingEvent(
                eventName = eventName,
                timeStamp = Clock.System.now().toEpochMilliseconds(),
                payload = eventParams + extraParams
            )

            println("TrackingEvent: $trackingEvent")
            apiService.post<Unit>(
                route = "${BuildConfig.EVENTS_BASE_URL}/${ApiRoutes.TRACK_EVENT}",
                body = trackingEvent.toTrackingEventDto(),
                useDoneBaseUrl = false
            )
        }
    }

}