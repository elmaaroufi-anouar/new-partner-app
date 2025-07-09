package com.done.partner.domain.models.cart

import com.done.partner.domain.models.store.Store

data class Cart(
    val store: Store,
    val cartProducts: List<CartProduct>,
)
