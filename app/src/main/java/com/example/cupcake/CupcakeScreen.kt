package com.example.cupcake

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cupcake.data.DataSource.flavors
import com.example.cupcake.data.DataSource.quantityOptions
import com.example.cupcake.ui.OrderSummaryScreen
import com.example.cupcake.ui.OrderViewModel
import com.example.cupcake.ui.SelectOptionScreen
import com.example.cupcake.ui.StartOrderScreen

/**
 * 대상 경로(앱 화면에 상응하는 문자열) 정의. 경로는 URL 개념과 비슷하다
 * 다른 URL이 웹사이트의 다른 페이지에 매핑되는 것처럼 경로는 대상(=단일 컴포저블 or 컴포저블 그룹)에 매핑돼 고유한 식별자 역할을 하는 문자열이다
 * kotlin enum class에 속성 이름이 포함된 문자열을 반환하는 [name] 프로퍼티가 있기 때문에 enum class를 사용한다
 */
enum class CupcakeScreen {
    Start,
    Flavor,
    Pickup,
    Summary
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 * 뒤로 탐색이 가능한 경우 topBar를 표시하고 뒤로가기 버튼을 표시하는 컴포저블
 */
@Composable
fun CupcakeAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun CupcakeApp(modifier: Modifier = Modifier, viewModel: OrderViewModel = viewModel()){
    /* NavHost에서 쓰기 위한 NavController 인스턴스 가져옴
     * NavHost와 AppBar에서 네비게이션 컨트롤러를 쓸 거라서 이 컴포저블에서 선언해야 한다 */
    val navController = rememberNavController()

    // TODO: Get current back stack entry

    // TODO: Get the name of the current screen

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = false,
                navigateUp = { /* TODO: implement back navigation */ }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        /**
         * NavHost : 지정된 경로를 기반으로 다른 컴포저블 대상을 표시하는 컴포저블. 경로가 Flavor면 NavHost는 컵케이크 맛을 선택하는 화면을 띄운다
         */
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            /**
             * NavHost에서 경로 처리하기 : composable(route) { content } 형태의 함수 사용
             * - route : 경로 이름 문자열. 위에서 만든 enum 상수를 사용한다
             * - content : 특정 경로에 표시할 컴포저블 호출
             */
            composable(route = CupcakeScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = quantityOptions,
                    onNextButtonClicked = {
                        viewModel.setQuantity(it)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    }
                )
            }

            composable(route = CupcakeScreen.Flavor.name) {
                val context = LocalContext.current
                SelectOptionScreen(
                    subtotal = uiState.price,
                    options = flavors.map { id -> context.resources.getString(id) },
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Pickup.name)
                    },
                    onCancelButtonClicked = {
                        //
                    }
                )
            }

            // 수령일 화면. 맛 화면과 유사하지만 컴포저블에 전달되는 데이터가 다르다
            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Summary.name)
                    },
                    onCancelButtonClicked = {
                        //
                    }
                )
            }

            composable(route = CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onSendButtonClicked = { subject: String, summary: String ->
                        //
                    },
                    onCancelButtonClicked = {
                        //
                    }
                )
            }
        }
    }
}

