package com.github.florent37.arclayout

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import com.github.florent37.arclayout.manager.ClipManager
import com.github.florent37.arclayout.manager.ClipPathManager
import com.github.florent37.arclayout.manager.ClipPathManager.ClipPathCreator

open class ShapeOfView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val clipPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val clipPath = Path()
    private var pdMode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

    var drawable: Drawable? = null
        set(value) {
            field = value
            requiresShapeUpdate()
        }

    private val clipManager: ClipManager = ClipPathManager()
    private var requiersShapeUpdate = true
    private var clipBitmap: Bitmap? = null
    private val rectView = Path()

    override fun setBackground(background: Drawable) {
        // Disabled here, please set a background to this view child
        // super.setBackground(background);
    }

    override fun setBackgroundResource(resid: Int) {
        // Disabled here, please set a background to this view child
        // super.setBackgroundResource(resid);
    }

    override fun setBackgroundColor(color: Int) {
        // Disabled here, please set a background to this view child
        // super.setBackgroundColor(color);
    }

    init {
        clipPaint.isAntiAlias = true
        isDrawingCacheEnabled = true
        setWillNotDraw(false)
        clipPaint.color = Color.BLUE
        clipPaint.style = Paint.Style.FILL
        clipPaint.strokeWidth = 1f
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            clipPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            setLayerType(LAYER_TYPE_SOFTWARE, clipPaint) // Only works for software layers
        } else {
            clipPaint.xfermode = pdMode
            setLayerType(LAYER_TYPE_SOFTWARE, null) // Only works for software layers
        }
    }

    protected fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics).toInt()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            requiresShapeUpdate()
        }
    }

    private fun requiresBitmap(): Boolean {
        return isInEditMode || clipManager.requiresBitmap() || drawable != null
    }

    fun setDrawable(redId: Int) {
        drawable = AppCompatResources.getDrawable(context, redId)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (requiersShapeUpdate) {
            calculateLayout(canvas.width, canvas.height)
            requiersShapeUpdate = false
        }
        if (requiresBitmap()) {
            clipPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            canvas.drawBitmap(clipBitmap!!, 0f, 0f, clipPaint)
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                canvas.drawPath(clipPath, clipPaint)
            } else {
                canvas.drawPath(rectView, clipPaint)
            }
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            setLayerType(LAYER_TYPE_HARDWARE, null)
        }
    }

    private fun calculateLayout(width: Int, height: Int) {
        rectView.reset()
        rectView.addRect(0f, 0f, 1f * getWidth(), 1f * getHeight(), Path.Direction.CW)
        if (width > 0 && height > 0) {
            clipManager.setupClipLayout(width, height)
            clipPath.reset()
            clipPath.set(clipManager.createMask(width, height))
            if (requiresBitmap()) {
                clipBitmap?.recycle()
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                clipBitmap = bitmap
                val canvas = Canvas(bitmap)
                if (drawable != null) {
                    drawable!!.setBounds(0, 0, width, height)
                    drawable!!.draw(canvas)
                } else {
                    canvas.drawPath(clipPath, clipManager.paint)
                }
            }

            // Invert the path for android P
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                rectView.op(clipPath, Path.Op.DIFFERENCE)
            }

            // This needs to be fixed for 25.4.0
            if (ViewCompat.getElevation(this) > 0f) {
                try {
                    setOutlineProvider(outlineProvider)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        postInvalidate()
    }

    override fun getOutlineProvider(): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val shadowConvexPath = clipManager.shadowConvexPath
                if (shadowConvexPath != null) {
                    try {
                        outline.setConvexPath(shadowConvexPath)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun setClipPathCreator(createClipPath: ClipPathCreator?) {
        (clipManager as ClipPathManager?)!!.setClipPathCreator(createClipPath)
        requiresShapeUpdate()
    }

    fun requiresShapeUpdate() {
        requiersShapeUpdate = true
        postInvalidate()
    }
}
