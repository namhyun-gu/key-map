package io.github.namhyungu.keymap.ui.home

import android.annotation.SuppressLint
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.paris.Paris
import com.airbnb.paris.extensions.alpha
import com.airbnb.paris.extensions.textColor
import com.airbnb.paris.extensions.textViewStyle
import io.github.namhyungu.keymap.R
import io.github.namhyungu.keymap.util.Util
import io.github.namhyungu.keymap.util.epoxy.KotlinEpoxyHolder
import io.github.namhyungu.keymap.util.getColorFromAttr

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.item_key_group_header)
abstract class KeyGroupHeaderModel :
    EpoxyModelWithHolder<KeyGroupHeaderModel.KeyGroupHeaderHolder>() {

    @EpoxyAttribute
    var address: String = ""

    @EpoxyAttribute
    var buildingName: String = ""

    @EpoxyAttribute
    var distance: Float = 0f

    override fun bind(holder: KeyGroupHeaderHolder) {
        super.bind(holder)
        val context = holder.placeView.context
        val builder = Paris.spannableBuilder()
            .append(Util.getDistanceString(distance), textViewStyle {
                textColor(context.getColorFromAttr(R.attr.colorSecondary))
            })
            .append("  |  ", textViewStyle {
                alpha(0.24f)
            })
            .append(address, textViewStyle {
//                textColor(context.getColorFromAttr(R.attr.colorOnBackground))
            })

        if (buildingName.isNotEmpty()) {
            builder.append(" ($buildingName)", textViewStyle {
//                textColor(context.getColorFromAttr(R.attr.colorOnBackground))
            })
        }

        builder.applyTo(holder.placeView)
    }

    inner class KeyGroupHeaderHolder : KotlinEpoxyHolder() {
        val placeView by bind<TextView>(R.id.place)
    }
}