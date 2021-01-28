package io.github.namhyungu.keymap.util

import android.location.Location
import io.github.namhyungu.keymap.data.Address
import io.github.namhyungu.keymap.data.BaseAddress
import io.github.namhyungu.keymap.data.Key
import io.github.namhyungu.keymap.data.RoadAddress
import io.github.namhyungu.keymap.ui.home.KeyItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlin.coroutines.coroutineContext

private data class GroupingKey(
    val roadName: String,
    val roadNumber: Long,
    val buildingName: String,
) {
    constructor() : this("", 0L, "")
}

object Util {
    fun getDistanceString(distance: Float): String {
        return if (distance >= 1000) { // Over 1km
            "${String.format("%.1f", distance / 1000)}km"
        } else {
            "${String.format("%.1f", distance)}m"
        }
    }

    fun sortKeyList(keyList: List<KeyItem>, location: Location?): List<KeyItem> {
        return if (location != null) {
            keyList.sortedWith { key1, key2 ->
                val dist1 = key1.location.distanceTo(location)
                val dist2 = key2.location.distanceTo(location)
                dist1.compareTo(dist2)
            }
        } else {
            keyList
        }
    }

    fun groupKeyList(keyList: List<Key>): List<KeyItem> {
        val result = mutableListOf<KeyItem>()
        val (group, remain) = keyList.groupBy {
            val place = checkNotNull(it.place)
            when (val address = place.address) {
                is RoadAddress -> GroupingKey(address.roadName,
                                              address.roadNumber,
                                              address.buildingName)
                else -> GroupingKey()
            }
        }.toList().partition { it.first != GroupingKey() && it.second.size > 1 }

        result.addAll(
            group.map { (groupingKey, list) ->
                val (roadName, roadNumber, buildingName) = groupingKey
                val location = list.first().place!!.location
                KeyItem.Group(
                    location = location,
                    roadName = roadName,
                    roadNumber = roadNumber,
                    buildingName = buildingName,
                    keyList = list
                )
            }
        )

        remain.forEach { (_, list) ->
            result.addAll(
                list.map {
                    KeyItem.Default(it.place!!.location, it)
                }
            )
        }

        return result
    }

    fun getAddressString(address: BaseAddress): String {
        return address.run {
            when (this) {
                is Address -> {
                    "$city $address1"
                }
                is RoadAddress -> {
                    "$city $roadName $roadNumber"
                }
            }
        }
    }

    fun getBuildingName(address: BaseAddress): String {
        return if (address is RoadAddress) {
            address.buildingName
        } else {
            ""
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
suspend fun checkInMainCoroutineDispatcher(): Boolean {
    return coroutineContext[CoroutineDispatcher] is MainCoroutineDispatcher
}

