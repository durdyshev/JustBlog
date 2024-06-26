package com.example.justblog.cropimage

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.GestureDetectorCompat

/**
 * According to the gesture detected on [trackPad], this will notify [actionListener].
 */
internal class GestureAnimation(
  private val trackPad: View,
  private val actionListener: ActionListener
) {

  private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
      return true
    }

    override fun onShowPress(e: MotionEvent) = Unit

    override fun onSingleTapUp(e: MotionEvent): Boolean {
      return true
    }

    override fun onScroll(
      e1: MotionEvent?,
      e2: MotionEvent,
      distanceX: Float,
      distanceY: Float
    ): Boolean {
      actionListener.onMoved(-distanceX, -distanceY)
      return true
    }


    override fun onLongPress(e: MotionEvent) = Unit
    override fun onFling(
      e1: MotionEvent?,
      e2: MotionEvent,
      velocityX: Float,
      velocityY: Float
    ): Boolean {
      actionListener.onFlinged(velocityX, velocityY)
      return true
    }
  }

  private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
      return super.onScaleBegin(detector)
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
      super.onScaleEnd(detector)
      actionListener.onScaleEnded()
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
      actionListener.onScaled(detector.scaleFactor)
      return true
    }
  }

  private val gestureDetector = GestureDetectorCompat(trackPad.context, gestureListener)
  private val scaleDetector = ScaleGestureDetector(trackPad.context, scaleListener)

  @SuppressLint("ClickableViewAccessibility")
  fun start() {
    trackPad.setOnTouchListener { _, event ->
      gestureDetector.onTouchEvent(event)
      scaleDetector.onTouchEvent(event)
      when (event.action) {
        MotionEvent.ACTION_UP -> actionListener.onMoveEnded()
      }
      true
    }
  }

  fun stop() {
    trackPad.setOnTouchListener(null)
  }
}