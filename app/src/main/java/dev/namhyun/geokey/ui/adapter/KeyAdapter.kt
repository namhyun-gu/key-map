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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.namhyun.geokey.databinding.ItemKeyBinding
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationModel
import dev.namhyun.geokey.util.distanceTo
import dev.namhyun.geokey.util.meter

class KeyAdapter(
  private val onItemSelected: ((Document<Key>) -> Unit)? = null
) : RecyclerView.Adapter<KeyAdapter.KeyViewHolder>() {

    private val items = mutableListOf<Document<Key>>()
    private var currentLocation: LocationModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyViewHolder {
        return KeyViewHolder(ItemKeyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: KeyViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            val value = item.value
            tvName.text = value.name
            tvKey.text = value.key
            tvLocation.text = value.address
            if (currentLocation != null) {
                tvNearBy.text = value.distanceTo(currentLocation!!).meter()
            }
            root.setOnClickListener {
                onItemSelected?.invoke(item)
            }
        }
    }

    fun addKeys(keys: List<Document<Key>>) {
        items.clear()
        items.addAll(keys)
        if (currentLocation != null) {
            sortByLocation()
        }
        notifyDataSetChanged()
    }

    private fun sortByLocation() {
        items.sortWith { o1, o2 ->
            val dis1 = o1.value.distanceTo(currentLocation!!)
            val dis2 = o2.value.distanceTo(currentLocation!!)
            dis1.compareTo(dis2)
        }
    }

    fun setLocation(location: LocationModel) {
        this.currentLocation = location
        sortByLocation()
        notifyDataSetChanged()
    }

    class KeyViewHolder(val binding: ItemKeyBinding) : RecyclerView.ViewHolder(binding.root)
}
