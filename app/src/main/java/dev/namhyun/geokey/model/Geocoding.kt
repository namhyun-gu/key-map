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
package dev.namhyun.geokey.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Geocoding(val status: Status, val results: List<GeocodingResult>) {
    val legalCode: LegalCode?
        get() {
            val result = results.filterIsInstance<LegalCode>()
            return if (result.isNotEmpty()) result.first()
            else null
        }

    val admCode: AdmCode?
        get() {
            val result = results.filterIsInstance<AdmCode>()
            return if (result.isNotEmpty()) result.first()
            else null
        }

    val roadAddr: RoadAddr?
        get() {
            val result = results.filterIsInstance<RoadAddr>()
            return if (result.isNotEmpty()) result.first()
            else null
        }

    val addr: Addr?
        get() {
            val result = results.filterIsInstance<Addr>()
            return if (result.isNotEmpty()) result.first()
            else null
        }

    val isSuccess: Boolean
        get() = status.code == 0

    val isNoResults: Boolean
        get() = status.code == 3
}

@Serializable
data class Status(val code: Int, val message: String)

@Polymorphic
@Serializable
sealed class GeocodingResult

@Serializable
@SerialName("legalcode")
data class LegalCode(val region: Region) : GeocodingResult()

@Serializable
@SerialName("admcode")
data class AdmCode(val region: Region) : GeocodingResult()

@Serializable
@SerialName("roadaddr")
data class RoadAddr(val region: Region, val land: Land) : GeocodingResult()

@Serializable
@SerialName("addr")
data class Addr(val region: Region) : GeocodingResult()

@Serializable
data class Region(
    @SerialName("area0") val country: Area,
    @SerialName("area1") val province: Area,
    @SerialName("area2") val city: Area,
    @SerialName("area3") val address1: Area,
    @SerialName("area4") val address2: Area
)

@Serializable
data class Area(val name: String)

@Serializable
data class Land(
    val name: String,
    @SerialName("number1") val number: String,
    @SerialName("addition0") val buildingInfo: BuildingInfo
)

@Serializable
data class BuildingInfo(@SerialName("value") val name: String)
