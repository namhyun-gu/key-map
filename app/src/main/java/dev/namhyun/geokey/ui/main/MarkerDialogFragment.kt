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
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.ui.adapter.KeyAdapter
import kotlinx.android.synthetic.main.dialog_marker_sheet.view.*

class MarkerDialogFragment(
  private val keys: List<Document<Key>>,
  private val onItemSelected: (Document<Key>) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_marker_sheet, container, false)
        val keyAdapter = KeyAdapter(onItemSelected)

        keyAdapter.addKeys(keys)
        view.list_keys.apply {
            adapter = keyAdapter
            layoutManager = LinearLayoutManager(context)
            val dividerDecoration =
                DividerItemDecoration(
                    ContextThemeWrapper(context, R.style.Theme_MyApp),
                    DividerItemDecoration.VERTICAL
                )
            addItemDecoration(dividerDecoration)
        }
        return view
    }
}
