package com.zhenxiang.blur

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlin.math.roundToInt

import androidx.core.graphics.ColorUtils
import androidx.annotation.ColorInt

import com.android.internal.graphics.drawable.BackgroundBlurDrawable

class BlurLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
): LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    @ColorInt val backgroundColour: Int
    // Opacity applied to the background colour when blur is available
    val blurBackgroundColourOpacity: Float
    val blurRadius: Int
    val cornerRadiusTopLeft: Float
    val cornerRadiusTopRight: Float
    val cornerRadiusBottomLeft: Float
    val cornerRadiusBottomRight: Float

    init {
        val a = attrs?.let {
            context.obtainStyledAttributes(
                attrs, R.styleable.BlurLinearLayout, defStyleAttr, defStyleRes)
        }

        if (a != null) {
            backgroundColour = a.getColor(R.styleable.BlurLinearLayout_backgroundColour, Color.TRANSPARENT)
            blurBackgroundColourOpacity = a.getFloat(
                R.styleable.BlurLinearLayout_blurBackgroundColourOpacity,
                DEFAULT_BLUR_BACKGROUND_COLOUR_OPACITY
            )
            blurRadius = a.getInteger(R.styleable.BlurLinearLayout_blurRadius, DEFAULT_BLUR_RADIUS)

            val allEdgesCornerRadius = a.getDimensionPixelSize(R.styleable.BlurLinearLayout_cornerRadius, 0)

            cornerRadiusTopLeft = formatEdgeCornerRadius(
                allEdgesCornerRadius,
                a.getDimensionPixelSize(R.styleable.BlurLinearLayout_cornerRadiusTopLeft, -1)
            )
            cornerRadiusTopRight = formatEdgeCornerRadius(
                allEdgesCornerRadius,
                a.getDimensionPixelSize(R.styleable.BlurLinearLayout_cornerRadiusTopRight, -1)
            )
            cornerRadiusBottomLeft = formatEdgeCornerRadius(
                allEdgesCornerRadius,
                a.getDimensionPixelSize(R.styleable.BlurLinearLayout_cornerRadiusBottomLeft, -1)
            )
            cornerRadiusBottomRight = formatEdgeCornerRadius(
                allEdgesCornerRadius,
                a.getDimensionPixelSize(R.styleable.BlurLinearLayout_cornerRadiusBottomRight, -1)
            )

            a.recycle()
        } else {
            backgroundColour = Color.TRANSPARENT
            blurBackgroundColourOpacity = DEFAULT_BLUR_BACKGROUND_COLOUR_OPACITY
            blurRadius = DEFAULT_BLUR_RADIUS

            cornerRadiusTopLeft = 0f
            cornerRadiusTopRight = 0f
            cornerRadiusBottomLeft = 0f
            cornerRadiusBottomRight = 0f
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val blurDrawable: BackgroundBlurDrawable = getViewRootImpl().createBackgroundBlurDrawable()
        blurDrawable.setBlurRadius(blurRadius)
        blurDrawable.setColor(applyOpacityToColour(backgroundColour, blurBackgroundColourOpacity))
        blurDrawable.setCornerRadius(
            cornerRadiusTopLeft,
            cornerRadiusTopRight,
            cornerRadiusBottomLeft,
            cornerRadiusBottomRight
        )

        background = blurDrawable
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    companion object {
        const val DEFAULT_BLUR_BACKGROUND_COLOUR_OPACITY = 0.7f
        const val DEFAULT_BLUR_RADIUS = 25

        @ColorInt
        fun applyOpacityToColour(@ColorInt colour: Int, opacity: Float): Int {
            val targetAlpha = Color.alpha(colour) * opacity
            return ColorUtils.setAlphaComponent(colour, targetAlpha.roundToInt())
        }

        // Consider edgeRadius when more than -1, otherwise use allSidesRadius
        private fun formatEdgeCornerRadius(allSidesRadius: Int, edgeRadius: Int): Float {
            return if (edgeRadius > -1) {
                edgeRadius
            } else {
                allSidesRadius
            }.toFloat()
        }
    }
} 
