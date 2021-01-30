package io.github.namhyungu.keymap.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun CronetEngine.startRequest(
    url: String,
    builder: (UrlRequest.Builder) -> UrlRequest.Builder,
): String {
    return suspendCoroutine { cont ->
        val callback = object : UrlRequest.Callback() {
            override fun onRedirectReceived(
                request: UrlRequest?,
                info: UrlResponseInfo?,
                newLocationUrl: String?,
            ) {
                request?.followRedirect()
            }

            override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
                val httpStatusCode = info?.httpStatusCode
                val buffer: ByteBuffer = ByteBuffer.allocateDirect(32 * 1024)
                if (httpStatusCode == 200) {
                    request?.read(buffer)
                }
            }

            override fun onReadCompleted(
                request: UrlRequest?,
                info: UrlResponseInfo?,
                byteBuffer: ByteBuffer?,
            ) {
                byteBuffer?.flip()
                byteBuffer?.let {
                    val bytes = ByteArray(it.remaining())
                    it.get(bytes)
                    String(bytes, Charset.forName("UTF-8"))
                }.apply {
                    cont.resume(this!!)
                }
                byteBuffer?.clear()
                request?.read(byteBuffer)
            }

            override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
            }

            override fun onFailed(
                request: UrlRequest?,
                info: UrlResponseInfo?,
                error: CronetException?,
            ) {
                cont.resumeWithException(error!!)
            }
        }

        val executor = Executors.newSingleThreadExecutor()
        val request = builder(newUrlRequestBuilder(url, callback, executor)).build()
        request.start()
    }
}