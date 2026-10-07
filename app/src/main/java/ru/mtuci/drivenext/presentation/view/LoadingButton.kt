package ru.mtuci.drivenext.presentation.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import ru.mtuci.drivenext.R

/**
 * Кнопка с индикатором выполнения запроса: при isLoading текст заменяется крутилкой,
 * а нажатия игнорируются. Нужна, чтобы пользователь видел, что запрос к серверу идёт.
 */
class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle,
) : MaterialButton(context, attrs, defStyleAttr) {

    private var savedText: CharSequence? = null
    private var savedIcon: Drawable? = null
    private var savedIconTint: ColorStateList? = null

    /** Цвет крутилки: белый на тёмной кнопке, фиолетовый на светлой. */
    var progressColorRes: Int = R.color.white

    var isLoading: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            if (value) startLoading() else stopLoading()
        }

    private fun startLoading() {
        savedText = text
        savedIcon = icon
        savedIconTint = iconTint
        val spec = CircularProgressIndicatorSpec(
            context, null, 0, com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        ).apply {
            indicatorSize = (22 * resources.displayMetrics.density).toInt()
            trackThickness = (2.5f * resources.displayMetrics.density).toInt()
            indicatorColors = intArrayOf(ContextCompat.getColor(context, progressColorRes))
        }
        text = ""
        iconPadding = 0
        iconGravity = ICON_GRAVITY_TEXT_START
        iconTint = null
        icon = IndeterminateDrawable.createCircularDrawable(context, spec)
        isClickable = false
    }

    private fun stopLoading() {
        icon = savedIcon
        iconTint = savedIconTint
        text = savedText
        isClickable = true
    }
}
