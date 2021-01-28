package io.github.namhyungu.keymap.ui.home

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.paris.Paris
import com.airbnb.paris.extensions.alpha
import com.airbnb.paris.extensions.textColor
import com.airbnb.paris.extensions.textStyle
import com.airbnb.paris.extensions.textViewStyle
import io.github.namhyungu.keymap.R
import io.github.namhyungu.keymap.util.Util
import io.github.namhyungu.keymap.util.epoxy.KotlinEpoxyHolder
import io.github.namhyungu.keymap.util.getColorFromAttr

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.item_key)
abstract class KeyModel : EpoxyModelWithHolder<KeyModel.KeyHolder>() {
    @EpoxyAttribute
    var content: String = ""

    @EpoxyAttribute
    var description: String = ""

    @EpoxyAttribute
    var address: String = ""

    @EpoxyAttribute
    var addressDetail: String = ""

    @EpoxyAttribute
    var buildingName: String = ""

    @EpoxyAttribute
    var distance: Float = 0f

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: View.OnClickListener? = null

    override fun bind(holder: KeyHolder) {
        super.bind(holder)
        val context = holder.rootView.context

        holder.rootView.setOnClickListener(clickListener)
        holder.contentView.text = content
        if (description.isNotEmpty()) {
            holder.descriptionView.text = description
        } else {
            holder.descriptionView.isVisible = false
        }

        val builder = Paris.spannableBuilder()
            .append(Util.getDistanceString(distance), textViewStyle {
                textColor(context.getColorFromAttr(R.attr.colorSecondary))
            })
            .append("  |  ", textViewStyle {
                alpha(0.24f)
            })
            .append(address, textViewStyle {
                textColor(context.getColorFromAttr(R.attr.colorOnBackground))
            })

        if (addressDetail.isNotEmpty()) {
            builder.append(", $addressDetail", textViewStyle {
                textColor(context.getColorFromAttr(R.attr.colorOnBackground))
                textStyle(Typeface.BOLD)
            })
        }

        if (buildingName.isNotEmpty()) {
            builder.append(" ($buildingName)", textViewStyle {
                textColor(context.getColorFromAttr(R.attr.colorOnBackground))
            })
        }

        builder.applyTo(holder.placeView)
    }

    inner class KeyHolder : KotlinEpoxyHolder() {
        val rootView by bind<View>(R.id.root)
        val contentView by bind<TextView>(R.id.content)
        val descriptionView by bind<TextView>(R.id.description)
        val placeView by bind<TextView>(R.id.place)
    }
}