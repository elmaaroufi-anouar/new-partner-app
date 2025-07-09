package com.done.partner.presentation.order_list

import androidx.compose.foundation.text.input.TextFieldState
import com.done.partner.domain.models.orders.Order
import com.done.core.domain.models.pagination.Pagination
import com.done.partner.domain.models.store.Store

data class OrderListState(
    val orderThatIsBeingMarkedAcceptedOrReadyId: String = "",
    val orderThatIsBeingMarkedRefusedId: String = "",

    val printLangCode: String? = null,

    val isLoadingStore: Boolean = false,
    val isFirstTimeLoadingStore: Boolean = false,
    val isChangingStoreAvailability: Boolean = false,
    val isStoreOpen: Boolean = false,
    val isToggleStoreAvailabilityDialogShowing: Boolean = false,
    val stores: List<Store> = emptyList(),
    val selectedStore: Store? = null,
    val selectedStoreId: String? = null,

    val isLoadingFirstTabOrders: Boolean = false,
    val isLoadingSecondTabOrders: Boolean = false,
    val shouldForceUpdate: Boolean = false,
    val updateUrl: String = "https://drive.google.com/uc?export=download&id=1tYJYNfHWwbJbOnJa791oFSm-kz-xKoNH",

    val activateFCM: Boolean? = null,
    val isRequestUpdatePlayServicesDialogShowing: Boolean = false,
    val isUpdatingPlayServices: Boolean = false,
    val playServicesUrl: String = "https://assets.done.ma/GooglePlayServices.xapk",

    val firstTabPagination: Pagination? = null,
    val secondTabPagination: Pagination? = null,

    val firstTabOrders: List<Order> = emptyList(),
    val secondTabOrders: List<Order> = emptyList(),

    val isConfirmingDelivery: Boolean = false,
    val isDeliveryCodeInputDialogShowing: Boolean = false,
    val selectedOrderToConfirmId: String? = null,
    val deliveryCodeTextState: TextFieldState = TextFieldState()
)