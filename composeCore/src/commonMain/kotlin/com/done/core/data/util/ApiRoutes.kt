package com.done.core.data.util

object ApiRoutes {

    const val LOGIN = "api/login"

    const val GET_STORES = "api/stores"

    // Store by ID
    fun storeById(storeId: String): String {
        return "api/stores/$storeId?include=brand,sections.products.assets,sections.products.option_groups,ratings.customer,order_products_again.assets,order_products_again.option_groups"
    }

    fun changeLanguage(
        storeId: String
    ): String {
        return "api/stores/$storeId/settings/default-language"
    }

    // Update order
    fun updateOrder(storeId: String, orderId: String): String {
        return "api/stores/$storeId/orders/$orderId"
    }

    fun acceptedAndPendingAndReadyToPickup(
        storeId: String, page: Int
    ): String {
        return "api/stores/$storeId/orders?status[]=ready_for_pick_up&status[]=being_prepared&status[]=pending&sort_field=updated_at&sort_order=desc&include=customer,driver,order_items&page=$page"
    }

    fun deliveredAndCancelled(
        storeId: String, page: Int
    ): String {
        return "api/stores/$storeId/orders?status[]=delivered&status[]=cancelled&?sort_field=updated_at&sort_order=desc&include=customer,driver,order_items&page=$page"
    }

    fun getOrders(
        storeId: String, page: Int
    ): String {
        return "api/stores/$storeId/orders?sort_field=updated_at&sort_order=desc&include=customer&page=$page"
    }

    fun getOrderDetails(
        storeId: String, orderId: String
    ): String {
        return "api/stores/$storeId/orders/$orderId?include=customer,driver,order_items"
    }

    fun openCloseStore(
        storeId: String, state: String
    ): String {
        return "api/stores/$storeId/$state"
    }

    fun updateOrderStatus(
        storeId: String, orderId: String, markOrderAsStatus: String
    ): String {
        return "api/stores/$storeId/orders/$orderId/$markOrderAsStatus"
    }

    fun getProducts(
        storeId: String, page: Int, query: String
    ): String {
        val route = if (query.isNotEmpty()) {
            "api/stores/$storeId/products?page=$page&q=$query"
        } else {
            "api/stores/$storeId/products?page=$page"
        }

        return route
    }

    fun getProduct(
        storeId: String, productId: String
    ): String {
        return  "api/stores/$storeId/products/$productId"
    }

    fun updateProductPrice(
        storeId: String, productId: String
    ): String {
        return "api/stores/$storeId/products/$productId/update-price"
    }

    fun updateProductOptions(
        storeId: String, productId: String
    ): String {
        return "api/stores/$storeId/products/$productId/option-groups/update"
    }

    fun enableDisableProduct(
        storeId: String, productId: String, state: String
    ): String {
        return "api/stores/$storeId/products/$productId/$state"
    }

    fun registerTokenInServer(storeId: String): String {
        return "api/stores/$storeId/register-device-token"
    }

    fun posHeartbeat(storeId: String): String {
        return "api/stores/$storeId/pos-heartbeat"
    }

    fun orderTracking(orderId: String, action: String): String {
        return "api/orders/$orderId/order-tracking/$action"
    }

    // Track events
    const val TRACK_EVENT = "partner"
}