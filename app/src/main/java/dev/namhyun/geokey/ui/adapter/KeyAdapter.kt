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
package dev.namhyun.geokey.ui.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationData
import kotlinx.android.synthetic.main.item_key.view.*

class KeyAdapter(val onItemDelete: (String, Key) -> Unit) :
    RecyclerView.Adapter<KeyAdapter.KeyViewHolder>() {
    val items = mutableListOf<Pair<String, Key>>()
    var currentLocation: LocationData? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_key, parent, false)
        return KeyViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: KeyViewHolder, position: Int) {
        val (id, item) = items[position]
        holder.itemView.apply {
            tv_name.text = item.name
            tv_key.text = item.key
            tv_location.text = item.address
            btn_delete.setOnClickListener {
                onItemDelete(id, item)
            }
        }
    }

    fun addKeys(keys: List<Pair<String, Key>>) {
        items.clear()
        items.addAll(keys)
        if (currentLocation != null) {
            sort()
        }
        notifyDataSetChanged()
    }

    private fun sort() {
        items.sortWith(Comparator { o1, o2 ->
            val current = Location("").apply {
                latitude = currentLocation!!.lat
                longitude = currentLocation!!.lon
            }
            val loc1 = Location("").apply {
                val key = o1.second
                latitude = key.lat
                longitude = key.lon
            }
            val dis1 = current.distanceTo(loc1)
            val loc2 = Location("").apply {
                val key = o2.second
                latitude = key.lat
                longitude = key.lon
            }
            val dis2 = current.distanceTo(loc2)
            dis1.compareTo(dis2)
        })
    }

    fun sort(location: LocationData) {
        this.currentLocation = location
        sort()
        notifyDataSetChanged()
    }

    class KeyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
