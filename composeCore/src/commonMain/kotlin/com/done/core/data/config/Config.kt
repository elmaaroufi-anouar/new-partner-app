package com.done.core.data.config

sealed class Environment(val baseUrl: String) {
    data object Debug : Environment("https://partner.beta.done.ma")
    data object Production : Environment("https://partner.done.ma")
    data object EventsDebug : Environment("https://events.beta.done.ma")
    data object EventsProduction : Environment("https://events.done.ma")
}

object Config {
    private val currentEnvironment: Environment = if (Platform.isDebugBinary) {
        Environment.Debug
    } else {
        Environment.Production
    }

    private val currentEventsEnvironment: Environment = if (Platform.isDebugBinary) {
        Environment.EventsDebug
    } else {
        Environment.EventsProduction
    }

    val baseUrl = currentEnvironment.baseUrl
    val eventsUrl = currentEventsEnvironment.baseUrl

}

expect object Platform {
    val isDebugBinary: Boolean
}
