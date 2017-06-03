package com.github.ymegane.android.taptaptap.domain.model

import android.graphics.Point
import android.view.MotionEvent

data class Circle(val index: Int, internal var event: MotionEvent) {
    val pointerId: Int = event.getPointerId(index)
    val point: Point = toPoint(index, event)

    override fun equals(other: Any?): Boolean {
        if (super.equals(other)) return true
        if (other == null || javaClass != other.javaClass) return false

        val circle = other as Circle
        if (pointerId != circle.pointerId) return false
        return Math.abs(point.x - circle.point.x) <= 50
                && Math.abs(point.y - circle.point.y) <= 50
    }

    override fun hashCode(): Int {
        var result = pointerId
        result = 31 * result + point.hashCode()
        return result
    }
}

fun toPoint(index: Int, event: MotionEvent) = Point(Math.round(event.getX(index)), Math.round(event.getY(index)))
