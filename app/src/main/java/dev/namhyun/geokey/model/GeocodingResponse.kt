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

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
  val status: Status,
  val results: List<Result>
)

@JsonClass(generateAdapter = true)
data class Status(
  val code: Int,
  val name: String,
  val message: String
)

enum class OperationName {
    legalcode,
    admcode,
    roadaddr
}

sealed class Result(val name: OperationName)

data class LegalCodeResult(val region: Region) : Result(OperationName.legalcode)

data class AdmCodeResult(val region: Region) : Result(OperationName.admcode)

data class RoadAddrResult(val region: Region, val land: Land) : Result(OperationName.roadaddr)

@JsonClass(generateAdapter = true)
data class Region(
  val area0: Area,
  val area1: Area,
  val area2: Area,
  val area3: Area,
  val area4: Area
)

@JsonClass(generateAdapter = true)
data class Area(val name: String)

@JsonClass(generateAdapter = true)
data class Land(
  val name: String,
  val number1: String,
  val addition0: BuildingInfo
)

@JsonClass(generateAdapter = true)
data class BuildingInfo(val value: String)
