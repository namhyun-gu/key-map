package io.github.namhyungu.keymap.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberFusedLocationSource
import io.github.namhyungu.keymap.data.Key
import io.github.namhyungu.keymap.ui.KeyMapTheme

// https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#overview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeUiState: HomeUiState,
    userState: UserUiState,
    onLocationChange: (Location) -> Unit,
    onSignInClick: () -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 256.dp,
        sheetContent = {
            HomeSheetContent(
                homeUiState = homeUiState,
                userState = userState,
                onSignInClick = onSignInClick
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            Surface(
                modifier = Modifier
                    .padding(4.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    ),
                shape = MaterialTheme.shapes.medium
            ) {
                MapComponent(
                    modifier = Modifier.fillMaxSize(),
                    onLocationChange = onLocationChange,
                    keyList = homeUiState.keyList
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HomeSheetContent(
    homeUiState: HomeUiState,
    userState: UserUiState,
    onSignInClick: () -> Unit,
) {
    when (userState) {
        UserUiState.NotSignIn -> {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sheet content")
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = onSignInClick
                ) {
                    Text("Sign-in")
                }
            }
        }
        UserUiState.Process -> {
            CircularProgressIndicator()
        }
        is UserUiState.SignIn -> {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(256.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Swipe up to expand sheet")
            }
        }
    }
}

//@Composable
//@OptIn(ExperimentalPermissionsApi::class)
//private fun permission() {
//    val permissionsState = rememberMultiplePermissionsState(
//        listOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//        )
//    )
//    val allGranted = permissionsState.allPermissionsGranted
//}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapComponent(
    modifier: Modifier = Modifier,
    onLocationChange: (Location) -> Unit,
    keyList: List<Key>
) {
    // https://github.com/fornewid/naver-map-compose
    if (!LocalInspectionMode.current) {
        NaverMap(
            modifier = modifier,
            locationSource = rememberFusedLocationSource(),
            properties = MapProperties(
                locationTrackingMode = LocationTrackingMode.Follow
            ),
            uiSettings = MapUiSettings(
                isLocationButtonEnabled = true
            ),
            onLocationChange = {
                onLocationChange(Location(it.latitude, it.longitude))
            }
        ) {
            for (key in keyList) {
                val location = key.place?.location ?: continue

                Marker(
                    state = MarkerState(LatLng(location.latitude, location.longitude))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyDefaultItem(
) {
}

@Preview
@Composable
fun KeyDefaultItemPreview() {
    KeyMapTheme {
        KeyDefaultItem()
    }
}