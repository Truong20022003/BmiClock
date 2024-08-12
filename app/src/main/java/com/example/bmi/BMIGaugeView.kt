package com.example.bmi

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

class BMIGaugeView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var needleAngle = 270f
    private var needleDrawable: Drawable? = null

    init {
        paint.style = Paint.Style.FILL
        needleDrawable = ContextCompat.getDrawable(context, R.drawable.ic_kim_chi_bmi)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2

        val outerRadius = Math.min(width, height) / 2 * 0.8f
        val innerRadius = outerRadius

        // Vẽ cung "Under Weight"
        paint.color = Color.parseColor("#0077FE")
        val ovalUnderWeight = RectF(
            centerX - outerRadius,
            centerY - outerRadius,
            centerX + outerRadius,
            centerY + outerRadius
        )
        canvas.drawArc(ovalUnderWeight, 180f, 62f, true, paint)
        drawTextInArc(canvas, "UNDER\nWEIGHT", 180f, 62f, outerRadius, centerX, centerY)

        // Vẽ cung "Normal"
        paint.color = Color.parseColor("#00AD50")
        val ovalNormal = RectF(
            centerX - outerRadius,
            centerY - outerRadius,
            centerX + outerRadius,
            centerY + outerRadius
        )
        canvas.drawArc(ovalNormal, 245f, 62f, true, paint)
        drawTextInArc(canvas, "NORMAL", 245f, 62f, outerRadius, centerX, centerY)

        // Vẽ cung "Over Weight"
        paint.color = Color.parseColor("#F36F56")
        val ovalOverWeight = RectF(
            centerX - outerRadius,
            centerY - outerRadius,
            centerX + outerRadius,
            centerY + outerRadius
        )
        canvas.drawArc(ovalOverWeight, 310f, 34f, true, paint)
        drawTextInArc(canvas, "OVER\nWEIGHT", 310f, 34f, outerRadius, centerX, centerY)

        // Vẽ cung "Obese"
        paint.color = Color.parseColor("#FF2600")
        val ovalObese = RectF(
            centerX - outerRadius,
            centerY - outerRadius,
            centerX + outerRadius,
            centerY + outerRadius
        )
        canvas.drawArc(ovalObese, 347f, 13f, true, paint)
        drawTextInArc(canvas, "OBESE", 347f, 13f, outerRadius, centerX, centerY)

        // Vẽ nửa hình tròn nhỏ bên trong
        paint.color = Color.WHITE
        val smallOuterRadius = innerRadius * 0.55f
        val smallOval = RectF(
            centerX - smallOuterRadius,
            centerY - smallOuterRadius,
            centerX + smallOuterRadius,
            centerY + smallOuterRadius
        )
        canvas.drawArc(smallOval, 180f, 180f, true, paint)

        // Vẽ kim chỉ
        drawNeedle(canvas, centerX, centerY, smallOuterRadius)
    }

    private fun drawNeedle(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        paint.color = Color.WHITE
        canvas.drawCircle(centerX, centerY, 20f, paint)

        needleDrawable?.let { drawable ->
            val needleLength = radius * 0.9f
            val pivotX = centerX
            val pivotY = centerY

            val left = pivotX - drawable.intrinsicWidth / 2
            val top = pivotY - needleLength
            val right = pivotX + drawable.intrinsicWidth / 2
            val bottom = pivotY

            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            canvas.save()
            canvas.rotate(needleAngle, pivotX, pivotY) // Điều chỉnh góc kim chỉ
            drawable.draw(canvas)
            canvas.restore()
        }
    }

    private fun drawTextInArc(
        canvas: Canvas,
        text: String,
        startAngle: Float,
        sweepAngle: Float,
        radius: Float,
        centerX: Float,
        centerY: Float
    ) {
        val middleAngle = startAngle + sweepAngle / 2
        val textRadius = radius * 0.75f
        val textX =
            centerX + textRadius * Math.cos(Math.toRadians(middleAngle.toDouble())).toFloat()
        val textY =
            centerY + textRadius * Math.sin(Math.toRadians(middleAngle.toDouble())).toFloat()

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 12f.toDp()
            typeface = ResourcesCompat.getFont(context, R.font.larsseit_bold)
            textAlign = Paint.Align.CENTER
        }
        val textLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, 300)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .build()

        canvas.save()
        canvas.translate(textX, textY - textLayout.height / 2)
        textLayout.draw(canvas)
        canvas.restore()
    }

    fun setBMI(bmi: Float? = null) {
        val newAngle = calculateAngleFromBMI(bmi ?: 10f)
        animateNeedle(needleAngle, newAngle)
    }

    private fun calculateAngleFromBMI(bmi: Float): Float {
        val minBmi = 10f
        val maxBmi = 32f
        val percentage = (bmi - minBmi) / (maxBmi - minBmi)
        val clampedPercentage = percentage.coerceIn(0f, 1f)

        return when {
            bmi < 18.5f -> {
                // Underweight: Từ 180° đến 242°
                val bmiPercentage = (bmi - 10f) / (18.5f - 10f)
                val adjustedPercentage = bmiPercentage.coerceIn(0f, 1f)
                val angle = 180f + adjustedPercentage * 62f
                angle.coerceIn(180f, 242f) + 90f
            }

            bmi < 26.4f -> {
                // Normal: Từ 245° đến 307°
                val bmiPercentage = (bmi - 18.6f) / (26.3f - 18.6f)
                val adjustedPercentage = bmiPercentage.coerceIn(0f, 1f)
                val angle = 245f + adjustedPercentage * 62f
                angle.coerceIn(245f, 307f) + 90f
            }

            bmi < 29.8f -> {
                // Overweight: Từ 310° đến 344°
                val bmiPercentage = (bmi - 26.4f) / (29.7f - 26.4f)
                val adjustedPercentage = bmiPercentage.coerceIn(0f, 1f)
                val angle = 310f + adjustedPercentage * 34f
                angle.coerceIn(310f, 344f) + 90f
            }

            else -> {
                // Obese: Từ 347° đến 360°
                val bmiPercentage = (bmi - 29.8f) / (32f - 29.8f)
                val adjustedPercentage = bmiPercentage.coerceIn(0f, 1f)
                val angle = 347f + adjustedPercentage * 13f
                angle.coerceIn(347f, 360f) + 90f
            }
        }
    }



    fun Float.toDp(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
        )
    }

    private fun animateNeedle(oldAngle: Float, newAngle: Float) {

        val animator = ValueAnimator.ofFloat(oldAngle, newAngle)
        animator.duration = 1500
        animator.interpolator =
            AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            needleAngle = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }
}
