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

import android.os.Parcel
import android.os.Parcelable

data class Key(
    val name: String,
    val key: String,
    val lat: Double,
    val lon: Double,
    val address: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!
    )

    constructor() : this("", "", 0.0, 0.0, "")

    constructor(name: String, key: String, locationModel: LocationModel) :
            this(name, key, locationModel.lat, locationModel.lon, locationModel.address)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(key)
        parcel.writeDouble(lat)
        parcel.writeDouble(lon)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Key> {
        override fun createFromParcel(parcel: Parcel): Key {
            return Key(parcel)
        }

        override fun newArray(size: Int): Array<Key?> {
            return arrayOfNulls(size)
        }
    }
}
