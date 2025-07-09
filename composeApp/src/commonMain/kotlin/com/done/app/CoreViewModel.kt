package com.done.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.core.domain.repositories.remote_config.RemoteConfigRepository
import com.done.partner.domain.repositories.auth.AuthRepository
import com.done.partner.domain.repositories.settings.SettingsRepository
import com.done.partner.domain.repositories.store.StoreRepository
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import com.done.core.domain.repositories.event.TrackingRepository
import com.done.core.domain.services.notifications.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CoreViewModel(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val storeRepository: StoreRepository,
    private val notificationService: NotificationService,
    private val analyticsRepository: AnalyticsRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val trackingRepository: TrackingRepository
) : ViewModel() {

    var state by mutableStateOf(CoreState())
        private set

//    init {
//        viewModelScope.launch {
//            val startTime = System.currentTimeMillis()
//            state = state.copy(isCheckingLogIn = true)
//            state = state.copy(isLoggedIn = authRepository.isLoggedIn())
//            state = state.copy(isCheckingLogIn = false)
//
//            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
//                async {
//                    analyticsRepository.setUser()
//                    notificationService.subscribeToGlobalTopic()
//                }
//
//                async {
//                    if (state.isLoggedIn == true) {
//                        settingsRepository.registerDeviceToken()
//                        val storeId = storeRepository.getStoreId()
//                        if (storeId != null) {
//                            settingsRepository.changeLanguage(
//                                storeId, settingsRepository.getCurrentLanguage().code
//                            )
//                        }
//                    }
//                }
//
//                async {
//                    trackingRepository.trackOpenAppEvent(
//                        appStartDuration = (System.currentTimeMillis() - startTime).toString(),
//                        launchSuccess = state.isLoggedIn.toString(),
//                        errorCount = "0",
//                        errorType = ""
//                    )
//                }
//
//                async {
//                    val activateFCM = remoteConfigRepository.getConfig()?.partnerAndroidActivateFCM
//                    state = state.copy(activateFCM = activateFCM)
//                }
//            }
//        }
//    }
}