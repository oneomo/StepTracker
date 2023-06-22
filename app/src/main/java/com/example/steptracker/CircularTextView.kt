package com.example.steptracker
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CircularTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val progressPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var steps: Int = 0
    private var dailyGoalStep: Int = 0
    private var currentProgress: Float = 0f

    private val textBounds: Rect = Rect()

    init {
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = 10f // Adjust the stroke width as desired
        progressPaint.color = Color.GRAY

        textPaint.color = currentTextColor
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()

        val x = width / 2f
        val y = height / 2f + textBounds.height() / 2f

        val progressRatio = steps.toFloat() / dailyGoalStep.toFloat()
        val sweepAngle = progressRatio * 360f
        val startAngle = -90f

        val progressBounds = RectF(
            progressPaint.strokeWidth / 2f,
            progressPaint.strokeWidth / 2f,
            width - progressPaint.strokeWidth / 2f,
            height - progressPaint.strokeWidth / 2f
        )

        val animatedSweepAngle = currentProgress * sweepAngle
        canvas.drawArc(
            progressBounds,
            startAngle,
            animatedSweepAngle,
            false,
            progressPaint
        )

        val text = "$steps / $dailyGoalStep"
        canvas.drawText(text, x, y, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        textPaint.getTextBounds(text.toString(), 0, text.length, textBounds)
    }

    fun setSteps(steps: Int) {
        this.steps = steps
        invalidate()
    }

    fun setStrokeWidth(strokeWidth: Float) {
        progressPaint.strokeWidth = strokeWidth
        invalidate()
    }
    fun setDailyGoal(dailyGoal: Int) {
        this.dailyGoalStep = dailyGoal
        startProgressAnimation(1000)
        invalidate()
    }
    fun startProgressAnimation(duration: Long) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = duration
        animator.addUpdateListener { valueAnimator ->
            currentProgress = valueAnimator.animatedValue as Float
            val currentStep = (currentProgress * dailyGoalStep).toInt()
            val text = "$currentStep / $dailyGoalStep"
            setText(text)
            invalidate()
        }
        animator.start()
    }

}
