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
package dev.namhyun.geokey.util

import dev.namhyun.geokey.model.AdmCodeResult
import dev.namhyun.geokey.model.GeocodingResponse
import dev.namhyun.geokey.model.LegalCodeResult
import dev.namhyun.geokey.model.RoadAddrResult

fun GeocodingResponse.getAddress(): String? {
    return if (results.isNotEmpty()) {
        when (val result = results[0]) {
            is RoadAddrResult -> {
                val (region, land) = result
                val builder = StringBuilder()
                if (region.area1.name.isNotEmpty()) {
                    builder.append(region.area1.name)
                }
                if (region.area2.name.isNotEmpty()) {
                    builder.append(" " + (region.area2.name))
                }
                if (land.name.isNotEmpty()) {
                    builder.append(" " + land.name)
                }
                if (land.number1.isNotEmpty()) {
                    builder.append(" " + land.number1)
                }
                if (region.area3.name.isNotEmpty()) {
                    val building =
                        if (land.addition0.value.isNotEmpty()) ", ${land.addition0.value}"
                        else ""
                    builder.append(" (${region.area3.name}$building)")
                }
                builder.toString()
            }
            is LegalCodeResult -> {
                val (region) = result
                val builder = StringBuilder()
                if (region.area1.name.isNotEmpty()) {
                    builder.append(region.area1.name)
                }
                if (region.area2.name.isNotEmpty()) {
                    builder.append(" " + (region.area2.name))
                }
                if (region.area3.name.isNotEmpty()) {
                    builder.append(" " + region.area3.name)
                }
                if (region.area4.name.isNotEmpty()) {
                    builder.append(" " + region.area4.name)
                }
                builder.toString()
            }
            is AdmCodeResult -> {
                val (region) = result
                val builder = StringBuilder()
                if (region.area1.name.isNotEmpty()) {
                    builder.append(region.area1.name)
                }
                if (region.area2.name.isNotEmpty()) {
                    builder.append(" " + (region.area2.name))
                }
                if (region.area3.name.isNotEmpty()) {
                    builder.append(" " + region.area3.name)
                }
                if (region.area4.name.isNotEmpty()) {
                    builder.append(" " + region.area4.name)
                }
                builder.toString()
            }
        }
    } else {
        null
    }
}
