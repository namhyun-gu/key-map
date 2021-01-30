package io.github.namhyungu.keymap.data.source.remote

import android.os.NetworkOnMainThreadException
import io.github.namhyungu.keymap.BuildConfig
import io.github.namhyungu.keymap.data.Address
import io.github.namhyungu.keymap.data.BaseAddress
import io.github.namhyungu.keymap.data.RoadAddress
import io.github.namhyungu.keymap.data.source.ReverseGeocodingService
import io.github.namhyungu.keymap.util.`object`
import io.github.namhyungu.keymap.util.array
import io.github.namhyungu.keymap.util.checkInMainCoroutineDispatcher
import io.github.namhyungu.keymap.util.startRequest
import io.github.namhyungu.keymap.util.string
import io.github.namhyungu.keymap.util.toJsonObject
import org.chromium.net.CronetEngine
import org.json.JSONObject

class ReverseGeocodingServiceImpl(
    private val cronetEngine: CronetEngine,
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

        val response = if (checkInMainCoroutineDispatcher()) {
            throw NetworkOnMainThreadException()
        } else {
            cronetEngine.startRequest(url.toString()) {
                it.setHttpMethod("GET")
                    .addHeader("X-NCP-APIGW-API-KEY-ID", BuildConfig.NCP_CLIENT_ID)
                    .addHeader("X-NCP-APIGW-API-KEY", BuildConfig.NCP_CLIENT_SECRET)
            }
        }
        return toObject(response)
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