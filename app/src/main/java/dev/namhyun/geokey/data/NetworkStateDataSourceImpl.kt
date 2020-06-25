/*
 * Copyright 2020 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.namhyun.geokey.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dev.namhyun.geokey.model.NetworkState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkStateDataSourceImpl(context: Context) : NetworkStateDataSource {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun getState(): NetworkState {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return if (capabilities != null) NetworkState.Available
        else NetworkState.Unavailable
    }

    @ExperimentalCoroutinesApi
    override fun getStateUpdates(): Flow<NetworkState> {
        return callbackFlow {
            if (networkCallback == null) {
                networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        channel.offer(NetworkState.Available)
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        channel.offer(NetworkState.Unavailable)
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        channel.offer(NetworkState.Unavailable)
                    }
                }
            }
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
            awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback!!) }
        }
    }

    companion object {
        private val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }
}
