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
package io.github.namhyungu.keymap.util

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.TypedValue
import androidx.activity.ComponentActivity
import androidx.annotation.AttrRes
import androidx.core.app.ActivityCompat
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
fun Query.asFlow(): Flow<QuerySnapshot?> {
    return callbackFlow {
        addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else {
                offer(snapshot)
            }
        }
        awaitClose {
            // No-op
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun DocumentReference.asFlow(): Flow<DocumentSnapshot?> {
    return callbackFlow {
        addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
            } else {
                offer(snapshot)
            }
        }
        awaitClose {
            // No-op
        }
    }
}

suspend fun <R> Task<R>.await(): R? {
    return suspendCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { e -> cont.resumeWithException(e) }
    }
}

suspend inline fun Call.await(): Response {
    return suspendCancellableCoroutine { continuation ->
        val callback = ContinuationCallback(this, continuation)
        enqueue(callback)
        continuation.invokeOnCancellation(callback)
    }
}

class ContinuationCallback(
    private val call: Call,
    private val continuation: CancellableContinuation<Response>,
) : Callback, CompletionHandler {

    override fun onResponse(call: Call, response: Response) {
        continuation.resume(response)
    }

    override fun onFailure(call: Call, e: IOException) {
        if (!call.isCanceled()) {
            continuation.resumeWithException(e)
        }
    }

    override fun invoke(cause: Throwable?) {
        try {
            call.cancel()
        } catch (_: Throwable) {
        }
    }
}

suspend fun MapFragment.awaitMap(): NaverMap {
    return suspendCoroutine { cont ->
        getMapAsync {
            cont.resume(it)
        }
    }
}

fun Context.getColorFromAttr(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

fun ComponentActivity.checkSelfPermissions(permissions: List<String>): Boolean {
    permissions.forEach {
        if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

fun Location.toGeohash(): String {
    return GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))
}