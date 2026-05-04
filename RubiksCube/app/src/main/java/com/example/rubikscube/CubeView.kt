package com.example.rubikscube

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.min

/**
 * Renders the cube as a cross/net layout and handles swipe gestures on each face.
 *
 *          [UP]
 * [LEFT][FRONT][RIGHT][BACK]
 *          [DOWN]
 */
class CubeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    var cubeState: CubeState = CubeState()
        set(value) { field = value; invalidate() }

    var onMoveListener: ((face: Int, clockwise: Boolean) -> Unit)? = null

    private val tilePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 3f
    }
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xAAFFFFFF.toInt()
        style = Paint.Style.FILL
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    // Face origins in "grid units" (each face = 3 tiles)
    // Layout:        col  row
    //   UP           1    0
    //   LEFT         0    1
    //   FRONT        1    1
    //   RIGHT        2    1
    //   BACK         3    1
    //   DOWN         1    2
    private val faceGridPos = arrayOf(
        intArrayOf(1, 0), // UP
        intArrayOf(1, 2), // DOWN
        intArrayOf(1, 1), // FRONT
        intArrayOf(3, 1), // BACK
        intArrayOf(0, 1), // LEFT
        intArrayOf(2, 1)  // RIGHT
    )

    private val faceLabels = arrayOf("U", "D", "F", "B", "L", "R")

    private var tileSize = 0f
    private var offsetX = 0f
    private var offsetY = 0f

    // Touch handling
    private var touchStartX = 0f
    private var touchStartY = 0f
    private var touchFace = -1

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // 4 faces wide, 3 faces tall -> 12 tiles wide, 9 tiles tall
        val maxTileW = w / 12f
        val maxTileH = h / 9f
        tileSize = min(maxTileW, maxTileH)
        val totalW = tileSize * 12
        val totalH = tileSize * 9
        offsetX = (w - totalW) / 2f
        offsetY = (h - totalH) / 2f
        labelPaint.textSize = tileSize * 0.35f
        strokePaint.strokeWidth = tileSize * 0.05f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (face in 0..5) {
            drawFace(canvas, face)
        }
    }

    private fun drawFace(canvas: Canvas, face: Int) {
        val (gx, gy) = faceGridPos[face]
        val baseX = offsetX + gx * 3 * tileSize
        val baseY = offsetY + gy * 3 * tileSize
        for (r in 0..2) {
            for (c in 0..2) {
                val x = baseX + c * tileSize
                val y = baseY + r * tileSize
                val rect = RectF(x + 2, y + 2, x + tileSize - 2, y + tileSize - 2)
                tilePaint.color = cubeState.faces[face][r][c]
                canvas.drawRoundRect(rect, 6f, 6f, tilePaint)
                canvas.drawRoundRect(rect, 6f, 6f, strokePaint)
            }
        }
        // Face label in center
        val cx = baseX + 1.5f * tileSize
        val cy = baseY + 1.5f * tileSize - (labelPaint.ascent() + labelPaint.descent()) / 2f
        canvas.drawText(faceLabels[face], cx, cy, labelPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.x
                touchStartY = event.y
                touchFace = getFaceAt(event.x, event.y)
                return touchFace >= 0
            }
            MotionEvent.ACTION_UP -> {
                if (touchFace < 0) return false
                val dx = event.x - touchStartX
                val dy = event.y - touchStartY
                val threshold = tileSize * 0.5f
                if (abs(dx) < threshold && abs(dy) < threshold) return false
                val clockwise = interpretSwipe(touchFace, dx, dy)
                if (clockwise != null) {
                    onMoveListener?.invoke(touchFace, clockwise)
                }
                touchFace = -1
                return true
            }
        }
        return false
    }

    private fun getFaceAt(x: Float, y: Float): Int {
        for (face in 0..5) {
            val (gx, gy) = faceGridPos[face]
            val left = offsetX + gx * 3 * tileSize
            val top = offsetY + gy * 3 * tileSize
            if (x in left..(left + 3 * tileSize) && y in top..(top + 3 * tileSize)) return face
        }
        return -1
    }

    // Map swipe direction to CW/CCW for each face
    private fun interpretSwipe(face: Int, dx: Float, dy: Float): Boolean? {
        val horizontal = abs(dx) > abs(dy)
        return when (face) {
            FACE_UP -> if (horizontal) dx > 0 else null  // swipe right=CW, left=CCW; up/down ignored
            FACE_DOWN -> if (horizontal) dx < 0 else null
            FACE_FRONT -> if (horizontal) dx > 0 else dy < 0  // right=CW, up=CW
            FACE_BACK -> if (horizontal) dx < 0 else dy > 0
            FACE_LEFT -> if (!horizontal) dy < 0 else dx > 0
            FACE_RIGHT -> if (!horizontal) dy > 0 else dx < 0
            else -> null
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        // maintain 4:3 aspect of the net
        val tileW = w / 12f
        val tileH = h / 9f
        val tile = min(tileW, tileH)
        setMeasuredDimension((tile * 12).toInt().coerceAtLeast(w), (tile * 9).toInt().coerceAtLeast(h))
    }
}
