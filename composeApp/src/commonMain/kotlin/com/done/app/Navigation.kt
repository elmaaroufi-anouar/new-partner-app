package com.done.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.done.app.components.NoInternetView
import com.done.core.presentation.core.design_system.BottomNavigationItem
import com.done.core.presentation.core.design_system.DoneBottomBar
import com.done.core.presentation.core.design_system.DoneScaffold
import com.done.partner.domain.DEEPLINK_BASE_PATH
import com.done.partner.presentation.login.LoginScreenCore
import com.done.partner.presentation.order_details.OrderDetailsScreenCore
import com.done.partner.presentation.order_list.OrderListScreenCore
import com.done.partner.presentation.permissions.PermissionsScreenCore
import com.done.partner.presentation.product.product_options.ProductOptionsScreenCore
import com.done.partner.presentation.product.products.ProductsScreenCore
import com.done.partner.presentation.settings.SettingsScreenCore
import com.done.partner.presentation.store.store_detail.StoreDetailScreenRoot
import com.plusmobileapps.konnectivity.Konnectivity
import com.plusmobileapps.konnectivity.NetworkConnection
import kotlinx.serialization.Serializable

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean?,
    orderId: String?,
    orderStatus: String?,
) {
    val navController = rememberNavController()
    val bottomBarNavController = rememberNavController()
    var startDestination by remember { mutableStateOf<Screen?>(null) }

    LaunchedEffect(isLoggedIn) {
        startDestination = when (isLoggedIn) {
            true -> Screen.Main
            false -> Screen.Login
            else -> null
        }
    }

    var isAlreadyOpenedDetailsFromNotification by remember { mutableStateOf(false) }

    if (startDestination != null) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination!!,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            composable<Screen.Permissions> {
                PermissionsScreenCore(
                    onGranted = {
                        navController.navigateUp()
                        if (isLoggedIn == true) {
                            navController.navigate(Screen.Main)
                        } else {
                            navController.navigate(Screen.Login)
                        }
                    },
                )
            }

            composable<Screen.Login> {
                LoginScreenCore(
                    onLoginSuccess = {
                        navController.navigateUp()
                        navController.navigate(Screen.Main)
                    },
                    onRestartApp = {
                        // OnRestart app
                    }
                )
            }

            composable<Screen.Main> {
                LaunchedEffect(orderId, orderStatus) {
                    if (orderId != null && orderStatus != null && !isAlreadyOpenedDetailsFromNotification) {
                        isAlreadyOpenedDetailsFromNotification = true
                        navController.navigate(Screen.OrderDetails(orderId, orderStatus))
                    }
                }

                MainBottomBar(
                    navController = navController,
                    bottomBarNavController = bottomBarNavController,
                    onRestartApp = { _ -> }
                )
            }

            composable<Screen.StoreDetailRoute> { navBackStackEntry ->
                val storeDetails: Screen.StoreDetailRoute = navBackStackEntry.toRoute()
                StoreDetailScreenRoot(
                    orderId = storeDetails.orderId,
                    onBackClick = {
                        navController.navigateUp()
                    }
                )
            }

            composable<Screen.ProductOptionsRoute> { navBackStackEntry ->
                val storeDetails: Screen.ProductOptionsRoute = navBackStackEntry.toRoute()
                ProductOptionsScreenCore(
                    productId = storeDetails.productId,
                    onBackClick = {
                        navController.navigateUp()
                    }
                )
            }

            composable<Screen.OrderDetails>(
                deepLinks = listOf(
                    navDeepLink<Screen.OrderDetails>(
                        basePath = DEEPLINK_BASE_PATH
                    )
                )
            ) { navBackStackEntry ->
                val orderDetails: Screen.OrderDetails = navBackStackEntry.toRoute()

                OrderDetailsScreenCore(
                    orderId = orderDetails.orderId,
                    orderStatus = orderDetails.orderStatus,
                    onGoBack = {
                        navController.navigateUp()
                    },
                    onEditOrder = { orderId ->
                        navController.navigate(
                            Screen.StoreDetailRoute(orderId)
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainBottomBar(
    navController: NavHostController,
    bottomBarNavController: NavHostController,
    onRestartApp: (kill: Boolean) -> Unit
) {
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }

    val bottomBarItems = remember {
        mutableListOf(
            BottomNavigationItem(
                label = Res.string.orders,
                selectedIcon = Res.drawable.order,
                unselectedIcon = Res.drawable.order
            ),
            BottomNavigationItem(
                label = Res.string.products,
                selectedIcon = Res.drawable.product,
                unselectedIcon = Res.drawable.product
            ),
            BottomNavigationItem(
                label = Res.string.settings,
                selectedIcon = Res.drawable.settings,
                unselectedIcon = Res.drawable.settings
            )
        )
    }

    fun onItemClick(index: Int) {
        selectedItem = index

        val navigateToScreen = when (index) {
            0 -> BottomBarScreen.Orders
            1 -> BottomBarScreen.Products
            else -> BottomBarScreen.Settings
        }

        bottomBarNavController.navigate(navigateToScreen) {
            popUpTo(bottomBarNavController.graph.findStartDestination().id) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }

    val konnectivity by remember { mutableStateOf(Konnectivity()) }
    var hasInternet by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        konnectivity.currentNetworkConnectionState.collect { connection ->
            hasInternet = when (connection) {
                NetworkConnection.NONE -> false
                NetworkConnection.WIFI -> true
                NetworkConnection.CELLULAR -> true
            }
        }
    }

    DoneScaffold(
        contentWindowInsets = WindowInsets(top = 0.dp),
        withScrollBehavior = false,
        bottomBar = {
            DoneBottomBar(
                items = bottomBarItems,
                selectedItem = selectedItem,
                onItemClick = { index ->
                    onItemClick(index)
                }
            )
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = !hasInternet && selectedItem != 2,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            NoInternetView()
        }
        AnimatedVisibility(
            visible = hasInternet || selectedItem == 2,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            NavHost(
                modifier = Modifier.padding(paddingValues),
                navController = bottomBarNavController,
                startDestination = BottomBarScreen.Orders,
            ) {
                composable<BottomBarScreen.Orders> {
                    OrderListScreenCore(
                        onOrderDetails = { orderIdToDetails, statusToDetails ->
                            navController.navigate(
                                Screen.OrderDetails(
                                    orderId = orderIdToDetails,
                                    orderStatus = statusToDetails
                                )
                            )
                        },
                        onRestartApp = { kill ->
                            onRestartApp(kill)
                        }
                    )
                }

                composable<BottomBarScreen.Products> {
                    ProductsScreenCore(
                        onProductClick = {
                            navController.navigate(
                                Screen.ProductOptionsRoute(it)
                            )
                        }
                    )
                }

                composable<BottomBarScreen.Settings> {
                    SettingsScreenCore(
                        onRestartApp = {
                            onRestartApp(false)
                        }
                    )
                }
            }
        }
    }
}

sealed interface BottomBarScreen {
    @Serializable
    data object Orders : Screen

    @Serializable
    data object Products : Screen

    @Serializable
    data object Settings : Screen
}

sealed interface Screen {
    @Serializable
    data object Permissions : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object Main : Screen

    @Serializable
    data class OrderDetails(
        val orderId: String,
        val orderStatus: String
    ) : Screen

    @Serializable
    data class StoreDetailRoute(val orderId: String) : Screen

    @Serializable
    data class ProductOptionsRoute(val productId: String) : Screen
}