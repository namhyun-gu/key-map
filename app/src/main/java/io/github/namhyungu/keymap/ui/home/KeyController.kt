package io.github.namhyungu.keymap.ui.home

import android.location.Location
import android.view.View
import com.airbnb.epoxy.Typed2EpoxyController
import com.airbnb.epoxy.group
import io.github.namhyungu.keymap.R
import io.github.namhyungu.keymap.data.Key
import io.github.namhyungu.keymap.util.Util
import io.github.namhyungu.keymap.util.Util.groupKeyList
import io.github.namhyungu.keymap.util.Util.sortKeyList

sealed class KeyItem {
    abstract val location: Location

    data class Default(
        override val location: Location,
        val key: Key,
    ) : KeyItem()

    data class Group(
        override val location: Location,
        val roadName: String,
        val roadNumber: Long,
        val buildingName: String,
        val keyList: List<Key>,
    ) : KeyItem()
}

// TODO (feat) Geohash를 사용하여 정렬
// TODO https://firebase.google.com/docs/firestore/solutions/geoqueries#install_helper_library

// TODO (feat) 같은 도로명, 도로번호, 건물명으로 묶기 (도로명 주소일때) / 일반주소는 묶지 않음
// TODO https://github.com/airbnb/epoxy/wiki/Grouping-Models
class KeyController(
    private val onKeyClickListener: (Key) -> Unit,
) : Typed2EpoxyController<List<Key>, Location?>() {
    override fun buildModels(data: List<Key>, location: Location?) {
        sortKeyList(groupKeyList(data), location).forEach { item ->
            when (item) {
                is KeyItem.Group -> {
                    val pivotKey = item.keyList.first()
                    val city = pivotKey.place!!.address.city
                    val address = "$city ${item.roadName} ${item.roadNumber}"
                    val distance = location?.distanceTo(item.location) ?: 0f

                    group {
                        id("group_${pivotKey.id}")
                        layout(R.layout.item_key_group)

                        keyGroupHeader {
                            address(address)
                            buildingName(item.buildingName)
                            distance(distance)
                        }

                        item.keyList.sortedBy { it.place!!.detail }.forEach { key ->
                            keyGroupChild {
                                id(key.id)
                                addressDetail(key.place!!.detail)
                                content(key.content)
                                description(key.description)
                                clickListener { _: View ->
                                    onKeyClickListener(key)
                                }
                            }
                        }
                    }
                }
                is KeyItem.Default -> {
                    val key = item.key
                    val place = key.place!!
                    val address = Util.getAddressString(place.address)
                    val buildingName = Util.getBuildingName(place.address)
                    val distance = location?.distanceTo(place.location) ?: 0f

                    key {
                        id(key.id)
                        content(key.content)
                        description(key.description)
                        address(address)
                        addressDetail(place.detail)
                        buildingName(buildingName)
                        distance(distance)
                        clickListener { _: View ->
                            onKeyClickListener(key)
                        }
                    }
                }
            }

        }
    }
}