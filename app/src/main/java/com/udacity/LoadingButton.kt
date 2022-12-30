package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import timber.log.Timber
import kotlin.properties.Delegates

private const val DEFAULT_ANIME_DURATION = 1000L
private const val TOAST_SHORT_DURATION = 2500L

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    // variable hold color of view items
    private var backgroundColorNormal = 0
    private var loadingColor = 0
    private var loadingIndicatorColor = 0

    // loadingProgress will be use to manipulate the color box and circle
    var loadingProgress = 0.0f
        get() = field
        set(value) {
            field = value
            invalidate()
        }

    // indicator radius
    private var indicatorRadius = 18f

    // variable hold the paint
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
        typeface = Typeface.create("", Typeface.NORMAL)
    }

    // update listener for update loading progress
    private val updateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        loadingProgress = animation.animatedValue as Float
        if (buttonState == ButtonState.Clicked && loadingProgress == 1f) {
            buttonState = ButtonState.Normal
        }
    }

    // the animator control the value of loadingProgress
    private val valueAnimator = ValueAnimator().apply {
        setFloatValues(0f, 1f)
    }

    /**
     * cancel animation will
     * stop animation and reset loading progress
     */
    private fun cancelAnimation() {
        Timber.d("cancelAnimation()")
        loadingProgress = 0.0f
        valueAnimator.removeAllUpdateListeners()
    }

    /**
     * start animation
     * with repeat flag
     */
    private fun startAnimation(repeat: Boolean = false, animeTime: Long = TOAST_SHORT_DURATION) {
        valueAnimator.apply {
            addUpdateListener(updateListener)
            duration = animeTime
            if (repeat) {
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
            }
            start()
        }
    }

    // update anime base on change of button state
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        Timber.d("ButtonState: $new")
        when (new) {
            ButtonState.Normal -> {
                cancelAnimation()
            }
            ButtonState.Clicked -> {
                startAnimation()
            }
            ButtonState.Loading -> {
                startAnimation(true, DEFAULT_ANIME_DURATION)
            }
            ButtonState.Completed -> {
                cancelAnimation()
            }
        }
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backgroundColorNormal = getColor(R.styleable.LoadingButton_backgroundColorNormal, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            loadingIndicatorColor = getColor(R.styleable.LoadingButton_loadingIndicatorColor, 0)
        }
    }

    // textBound for calculate Size of text
    private val textBound = Rect()
    override fun onDraw(canvas: Canvas?) {
        // draw text
        if (canvas != null) {
            canvas.save()
            // draw background
            paint.color = backgroundColorNormal
            canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
            // draw loading color
            paint.color = loadingColor
            canvas.drawRect(0f, 0f, loadingProgress * widthSize, heightSize.toFloat(), paint)
            // draw text
            val label = resources.getString(buttonState.strResId)
            paint.getTextBounds(label, 0, label.length, textBound)
            paint.color = Color.WHITE
            canvas.drawText(
                label,
                widthSize / 2f,
                heightSize / 2f - textBound.exactCenterY(),
                paint
            )
            // draw spinning circle
            canvas.translate(
                widthSize / 2f + textBound.exactCenterX(),
                heightSize.toFloat() / 2f - indicatorRadius
            )
            paint.color = loadingIndicatorColor
            canvas.drawArc(
                0f,
                0f,
                2 * indicatorRadius,
                2 * indicatorRadius,
                0f,
                loadingProgress * 360f,
                true,
                paint
            )
            canvas.restore()
        }
    }

    /**
     * calculate size of view
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}