package io.github.namhyungu.keymap.data.source.remote

import android.os.NetworkOnMainThreadException
import io.github.namhyungu.keymap.BuildConfig
import io.github.namhyungu.keymap.data.Address
import io.github.namhyungu.keymap.data.BaseAddress
import io.github.namhyungu.keymap.data.RoadAddress
import io.github.namhyungu.keymap.data.source.ReverseGeocodingService
import io.github.namhyungu.keymap.util.HttpException
import io.github.namhyungu.keymap.util.`object`
import io.github.namhyungu.keymap.util.array
import io.github.namhyungu.keymap.util.await
import io.github.namhyungu.keymap.util.checkInMainCoroutineDispatcher
import io.github.namhyungu.keymap.util.string
import io.github.namhyungu.keymap.util.toJsonObject
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ReverseGeocodingServiceImpl(
    private val client: OkHttpClient,
) : ReverseGeocodingService {
    override suspend fun reverseGeocoding(lat: Double, lon: Double): BaseAddress {
        val url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc"
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("sourcecrs", "epsg:4326")
            .addQueryParameter("coords", "${lon},${lat}")
            .addQueryParameter("orders", "roadaddr,addr")
            .addQueryParameter("output", "json")
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .header("X-NCP-APIGW-API-KEY-ID", BuildConfig.NCP_CLIENT_ID)
            .header("X-NCP-APIGW-API-KEY", BuildConfig.NCP_CLIENT_SECRET)
            .build()


        val response = if (checkInMainCoroutineDispatcher()) {
            throw NetworkOnMainThreadException()
        } else {
            client.newCall(request).await()
        }

        if (!response.isSuccessful) {
            response.body?.close()
            throw HttpException(response)
        }

        val body = checkNotNull(response.body) { "Null response body" }

        return toObject(body.string())
    }

    private fun toObject(body: String): BaseAddress {
        val obj = body.toJsonObject()
        val results = obj.array("results")
        val result = results[0] as JSONObject

        // TODO Results 값이 비어있는 경우도 있음.
        return when (val type = result.string("name")) {
            "roadaddr" -> {
                RoadAddress.builder {
                    result.apply {
                        `object`("region") {
                            `object`("area1") {
                                province = string("name")
                            }
                            `object`("area2") {
                                city = string("name")
                            }
                            `object`("area3") {
                                address1 = string("name")
                            }
                            `object`("area4") {
                                address2 = string("name")
                            }
                        }
                        `object`("land") {
                            roadName = string("name")
                            roadNumber = string("number1").toLong()
                            `object`("addition0") {
                                buildingName = string("value")
                            }
                        }
                    }
                }
            }
            "addr" -> {
                Address.builder {
                    result.apply {
                        `object`("region") {
                            `object`("area1") {
                                province = string("name")
                            }
                            `object`("area2") {
                                city = string("name")
                            }
                            `object`("area3") {
                                address1 = string("name")
                            }
                            `object`("area4") {
                                address2 = string("name")
                            }
                        }
                    }
                }
            }
            else -> throw NotImplementedError("Not implemented geocoding type : $type")
        }
    }
}