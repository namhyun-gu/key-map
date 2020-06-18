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
package dev.namhyun.geokey.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Key
import kotlinx.android.synthetic.main.dialog_marker_sheet.view.*
import kotlinx.android.synthetic.main.item_key_menu.view.*

class MarkerSheetDialogFragment(private val keys: List<Key>) : BottomSheetDialogFragment() {

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_marker_sheet, container, false)
        view.list_keys.apply {
            adapter = KeyAdapter(keys)
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }

    private class KeyAdapter(val keys: List<Key>) : RecyclerView.Adapter<KeyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyViewHolder {
            return KeyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_key_menu, parent, false)
            )
        }

        override fun getItemCount(): Int = keys.size

        override fun onBindViewHolder(holder: KeyViewHolder, position: Int) {
            val item = keys[position]
            holder.itemView.apply {
                tv_name.text = item.name
                tv_key.text = item.key
            }
        }
    }

    private class KeyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
