package com.example.slider

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.text.DecimalFormat

class Slider : View {

	interface OnSliderReleaseListener {
		fun onSliderReleased(value: Int)
	}

	private var onSliderReleaseListener: OnSliderReleaseListener? = null

	private var value = 0.0f

	private val progressBackgroundPaint = Paint()
	private val progressPaint = Paint()
	private val ringPaint = Paint()
	private val handlerPaint = Paint()
	private val textPaint = Paint()

	private val formatter = DecimalFormat("#.##")

	private var radius = 0.0f

	private var progressHeight = 0.0f

	private var textSize = 0.0f

	private var touchX = 0.0f

	private val step = 1.0f

	private var textHeight = 0.0f

	private var textBottomMargin = 0.0f

	constructor(context: Context) : this(context, null)

	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		val typedArray = context.obtainStyledAttributes(attrs, R.styleable.Slider, defStyleAttr, 0)

		radius = if (typedArray.hasValue(R.styleable.Slider_progressHandleRadius)) {
			typedArray.getDimension(R.styleable.Slider_progressHandleRadius, -1.0f)
		} else {
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, resources.displayMetrics)
		}
		progressHeight = if (typedArray.hasValue(R.styleable.Slider_progressHeight)) {
			typedArray.getDimension(R.styleable.Slider_progressHeight, -1.0f)
		} else {
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, resources.displayMetrics)
		}
		textSize = if (typedArray.hasValue(R.styleable.Slider_progressTextSize)) {
			typedArray.getDimension(R.styleable.Slider_progressTextSize, -1.0f)
		} else {
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12.0f, resources.displayMetrics)
		}
		textBottomMargin = if (typedArray.hasValue(R.styleable.Slider_progressTextBottomMargin)) {
			typedArray.getDimension(R.styleable.Slider_progressTextBottomMargin, -1.0f)
		} else {
			TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, resources.displayMetrics)
		}

		handlerPaint.color = if (typedArray.hasValue(R.styleable.Slider_progressHandleColor)) {
			typedArray.getColor(R.styleable.Slider_progressHandleColor, 0)
		} else {
			Color.parseColor("#FFF18030")
		}
		progressBackgroundPaint.color = if (typedArray.hasValue(R.styleable.Slider_progressBackgroundColor)) {
			typedArray.getColor(R.styleable.Slider_progressBackgroundColor, 0)
		} else {
			Color.parseColor("#FFF1F1F1")
		}
		progressPaint.color = if (typedArray.hasValue(R.styleable.Slider_progressColor)) {
			typedArray.getColor(R.styleable.Slider_progressColor, 0)
		} else {
			Color.parseColor("#FFF18030")
		}
		ringPaint.color = if (typedArray.hasValue(R.styleable.Slider_progressHandleRingColor)) {
			typedArray.getColor(R.styleable.Slider_progressHandleRingColor, 0)
		} else {
			Color.parseColor("#FFFFFFFF")
		}
		textPaint.color = if (typedArray.hasValue(R.styleable.Slider_progressTextColor)) {
			typedArray.getColor(R.styleable.Slider_progressTextColor, 0)
		} else {
			Color.parseColor("#FFF18030")
		}

		textPaint.textSize = textSize

		val rect = Rect()
		textPaint.getTextBounds("0", 0, 1, rect)
		textHeight = rect.height().toFloat()

		typedArray.recycle()
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val height = textHeight + textBottomMargin + 2 * radius
		val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.AT_MOST)
		super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
	}

	override fun onDraw(canvas: Canvas) {
		val width = measuredWidth
		val height = measuredHeight

		val left = 0.0f
		val top = 0.0f
		val right = width.toFloat()
		val bottom = height.toFloat()

		canvas.save()

			canvas.translate(0.0f, textHeight + textBottomMargin)

			var cx = touchX
			if (cx < radius) {
				cx = radius
			}
			if (cx > width - radius) {
				cx = width - radius
			}
			val cy = radius

			val progressMargin = (2 * radius - progressHeight) / 2

			canvas.drawRoundRect(RectF(left, top + progressMargin, right, 2 * radius - progressMargin), radius, radius, progressBackgroundPaint)

			canvas.drawRoundRect(RectF(left, top + progressMargin, cx + radius, 2 * radius - progressMargin), radius, radius, progressPaint)

			canvas.drawCircle(cx, cy, radius + 5, ringPaint)

			canvas.drawCircle(cx, cy, radius, handlerPaint)

		canvas.restore()

		value = cx - radius
		if (value < 0) {
			value = 0.0f
		}
		val max = width - 2 * radius
		if (value > max) {
			value = max
		}
		value = value / max * 100
		value = value.toInt().toFloat()

		val formattedValue = formatter.format(value)

		val text = "$formattedValue%"
		val textWidth = textPaint.measureText(text)
		var textLeft = cx - (textWidth / 2)
		val textTop = textHeight
		if (textLeft < 0) {
			textLeft = 0.0f
		}
		if (textLeft + textWidth > width) {
			textLeft = width - textWidth
		}
		canvas.drawText(text, textLeft, textTop, textPaint)

		log("$value")
	}

	override fun onTouchEvent(event: MotionEvent): Boolean {
		touchX = event.x

		when (event.actionMasked) {
			MotionEvent.ACTION_DOWN -> {
				invalidate()
			}
			MotionEvent.ACTION_MOVE -> {
				invalidate()
			}
			MotionEvent.ACTION_UP -> {
				value = value.toInt().toFloat()

				onSliderReleaseListener?.onSliderReleased(value.toInt())

				val oldTouchX = touchX
				val newTouchX = (width - 2 * radius) * value / 100 + radius
				val valueAnimator = ValueAnimator.ofFloat(oldTouchX, newTouchX)
				valueAnimator.addUpdateListener {
					touchX = it.animatedValue as Float
					invalidate()
				}
				valueAnimator.start()
			}
		}
		return true
	}

	fun setValue(value: Float, animated: Boolean) {
		this.value = value

		val oldTouchX = touchX

		val newTouchX = (width - 2 * radius) * value / 100 + radius

		if (animated) {
			val valueAnimator = ValueAnimator.ofFloat(oldTouchX, newTouchX)
			valueAnimator.addUpdateListener {
				touchX = it.animatedValue as Float
				invalidate()
			}
			valueAnimator.start()
		} else {
			invalidate()
		}

	}

	fun setOnSliderReleaseListener(onSliderReleaseListener: OnSliderReleaseListener) {
		this.onSliderReleaseListener = onSliderReleaseListener
	}

	private fun log(message: String) {
		Log.d("Slider", message)
	}

}