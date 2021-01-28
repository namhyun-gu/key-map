package io.github.namhyungu.keymap.ui.home

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.github.namhyungu.keymap.R
import io.github.namhyungu.keymap.util.epoxy.KotlinEpoxyHolder

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.item_key_group_child)
abstract class KeyGroupChildModel : EpoxyModelWithHolder<KeyGroupChildModel.KeyGroupChildHolder>() {
    @EpoxyAttribute
    var content: String = ""

    @EpoxyAttribute
    var description: String = ""

    @EpoxyAttribute
    var addressDetail: String = ""

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: View.OnClickListener? = null

    override fun bind(holder: KeyGroupChildHolder) {
        super.bind(holder)
        holder.rootView.setOnClickListener(clickListener)
        holder.contentView.text = content
        if (description.isNotEmpty()) {
            holder.descriptionView.text = description
        } else {
            holder.descriptionView.isVisible = false
        }
        if (addressDetail.isNotEmpty()) {
            holder.detailView.text = addressDetail
        } else {
            holder.detailView.isVisible = false
        }
    }

    inner class KeyGroupChildHolder : KotlinEpoxyHolder() {
        val rootView by bind<View>(R.id.root)
        val detailView by bind<TextView>(R.id.detail)
        val contentView by bind<TextView>(R.id.content)
        val descriptionView by bind<TextView>(R.id.description)
    }
}