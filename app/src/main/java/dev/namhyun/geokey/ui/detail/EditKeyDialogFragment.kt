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
package dev.namhyun.geokey.ui.detail

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.util.locationData
import kotlinx.android.synthetic.main.fragment_edit_key.view.*

class EditKeyDialogFragment(
  private val keyDocument: Document<Key>,
  private val onDismiss: () -> Unit
) : BottomSheetDialogFragment() {
    private val viewModel by activityViewModels<DetailViewModel>()

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_key, container, false)
        val editName = view.edit_name
        val editKey = view.edit_key

        viewModel.resetForm()

        editName.editText!!.setText(keyDocument.value.name)
        editKey.editText!!.setText(keyDocument.value.key)

        view.btn_edit.setOnClickListener {
            val name = editName.editText!!.text.toString()
            val key = editKey.editText!!.text.toString()
            viewModel.updateKey(keyDocument.id, name, key, keyDocument.value.locationData)
        }

        view.btn_cancel.setOnClickListener { dismiss() }

        viewModel.editKeyFormData.observe(this, Observer {
            editName.error = ""
            editKey.error = ""
            when (it) {
                is InvalidData -> {
                    if (it.invalidItem.contains("name")) {
                        editName.error = getString(R.string.msg_name_required)
                    }
                    if (it.invalidItem.contains("key")) {
                        editKey.error = getString(R.string.msg_key_required)
                    }
                }
                ValidData -> {
                    dismiss()
                }
            }
        })
        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }
}
