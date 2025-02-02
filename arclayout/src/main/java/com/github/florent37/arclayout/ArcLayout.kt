package com.github.florent37.arclayout

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import androidx.annotation.IntDef
import com.github.florent37.arclayout.manager.ClipPathManager.ClipPathCreator

class ArcLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ShapeOfView(context, attrs, defStyleAttr) {
    @ArcPosition
    var arcPosition: Int = POSITION_TOP
        set(value) {
            field = value
            requiresShapeUpdate()
        }

    @CropDirection
    var cropDirection: Int = CROP_INSIDE
        set(value) {
            field = value
            requiresShapeUpdate()
        }

    var arcHeight: Int = 0
        set(value) {
            field = value
            requiresShapeUpdate()
        }

    init {
        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.ArcLayout)
            arcHeight = attributes.getDimensionPixelSize(R.styleable.ArcLayout_arc_height, arcHeight)
            arcPosition = attributes.getInteger(R.styleable.ArcLayout_arc_position, arcPosition)
            cropDirection = attributes.getInteger(R.styleable.ArcLayout_arc_cropDirection, cropDirection)
            attributes.recycle()
        }
        super.setClipPathCreator(
            object : ClipPathCreator {
                override fun createClipPath(width: Int, height: Int): Path {
                    val path = Path()
                    val isCropInside = cropDirection == CROP_INSIDE
                    when (arcPosition) {
                        POSITION_BOTTOM -> {
                            if (isCropInside) {
                                path.moveTo(0f, 0f)
                                path.lineTo(0f, height.toFloat())
                                path.quadTo((width / 2).toFloat(), (height - 2 * arcHeight).toFloat(), width.toFloat(), height.toFloat())
                                path.lineTo(width.toFloat(), 0f)
                                path.close()
                            } else {
                                path.moveTo(0f, 0f)
                                path.lineTo(0f, (height - arcHeight).toFloat())
                                path.quadTo((width / 2).toFloat(), (height + arcHeight).toFloat(), width.toFloat(), (height - arcHeight).toFloat())
                                path.lineTo(width.toFloat(), 0f)
                                path.close()
                            }
                        }

                        POSITION_TOP -> if (isCropInside) {
                            path.moveTo(0f, height.toFloat())
                            path.lineTo(0f, 0f)
                            path.quadTo((width / 2).toFloat(), (2 * arcHeight).toFloat(), width.toFloat(), 0f)
                            path.lineTo(width.toFloat(), height.toFloat())
                            path.close()
                        } else {
                            path.moveTo(0f, arcHeight.toFloat())
                            path.quadTo((width / 2).toFloat(), -arcHeight.toFloat(), width.toFloat(), arcHeight.toFloat())
                            path.lineTo(width.toFloat(), height.toFloat())
                            path.lineTo(0f, height.toFloat())
                            path.close()
                        }

                        POSITION_LEFT -> if (isCropInside) {
                            path.moveTo(width.toFloat(), 0f)
                            path.lineTo(0f, 0f)
                            path.quadTo((arcHeight * 2).toFloat(), (height / 2).toFloat(), 0f, height.toFloat())
                            path.lineTo(width.toFloat(), height.toFloat())
                            path.close()
                        } else {
                            path.moveTo(width.toFloat(), 0f)
                            path.lineTo(arcHeight.toFloat(), 0f)
                            path.quadTo(-arcHeight.toFloat(), (height / 2).toFloat(), arcHeight.toFloat(), height.toFloat())
                            path.lineTo(width.toFloat(), height.toFloat())
                            path.close()
                        }

                        POSITION_RIGHT -> if (isCropInside) {
                            path.moveTo(0f, 0f)
                            path.lineTo(width.toFloat(), 0f)
                            path.quadTo((width - arcHeight * 2).toFloat(), (height / 2).toFloat(), width.toFloat(), height.toFloat())
                            path.lineTo(0f, height.toFloat())
                            path.close()
                        } else {
                            path.moveTo(0f, 0f)
                            path.lineTo((width - arcHeight).toFloat(), 0f)
                            path.quadTo((width + arcHeight).toFloat(), (height / 2).toFloat(), (width - arcHeight).toFloat(), height.toFloat())
                            path.lineTo(0f, height.toFloat())
                            path.close()
                        }
                    }
                    return path
                }

                override fun requiresBitmap(): Boolean {
                    return false
                }
            },
        )
    }

    @IntDef(value = [POSITION_BOTTOM, POSITION_TOP, POSITION_LEFT, POSITION_RIGHT])
    annotation class ArcPosition

    @IntDef(value = [CROP_INSIDE, CROP_OUTSIDE])
    annotation class CropDirection
    companion object {
        const val POSITION_BOTTOM = 1
        const val POSITION_TOP = 2
        const val POSITION_LEFT = 3
        const val POSITION_RIGHT = 4
        const val CROP_INSIDE = 1
        const val CROP_OUTSIDE = 2
    }
}
