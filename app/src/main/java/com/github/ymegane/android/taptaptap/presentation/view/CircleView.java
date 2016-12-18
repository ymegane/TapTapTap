package com.github.ymegane.android.taptaptap.presentation.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.ymegane.android.taptaptap.R;

public class CircleView extends View {
    private Circle circle;
    private final Paint paint = new Paint();
    private ValueAnimator animator;
    private float defaultRadius;
    private static final int DURATION = 3000;

    private final Handler cancelHandler = new Handler(Looper.getMainLooper());
    private final Runnable cancelTimer = new Runnable() {
        @Override
        public void run() {
            startAnimation();
        }
    };

    public CircleView(Context context, Circle circle) {
        super(context);
        this.circle = circle;
        initLayout();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public Circle getCircle() {
        return circle;
    }

    private void initLayout() {
        if (this.circle == null) {
            return;
        }
        defaultRadius = getResources().getDimensionPixelSize(R.dimen.circle_radius);
        setElevation(getResources().getDimension(R.dimen.circle_elevation));

        int width = getResources().getDimensionPixelSize(R.dimen.circle_width);
        int height = getResources().getDimensionPixelSize(R.dimen.circle_width);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.setMargins(circle.point.x - width/2, circle.point.y - height/2, 0, 0);
        setLayoutParams(params);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        cancelHandler.postDelayed(cancelTimer, DURATION);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelHandler.removeCallbacks(cancelTimer);
    }

    public void continueCancelTimer() {
        cancelHandler.removeCallbacks(cancelTimer);
        cancelHandler.postDelayed(cancelTimer, DURATION);
    }

    public void startAnimation() {
        cancelHandler.removeCallbacks(cancelTimer);

        animator = ValueAnimator.ofFloat(defaultRadius, 0.f);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.setDuration(DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isAttachedToWindow()) {
                    return;
                }
                ((ViewGroup)getParent()).removeView(CircleView.this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float radius = defaultRadius;
        if (animator != null) {
            radius = (Float) (animator.getAnimatedValue());
        }
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        paint.setAlpha(Math.round(200 * (radius/defaultRadius)));
        canvas.drawCircle(getWidth()/2, getHeight()/2, radius, paint);
    }

    public boolean equals(Circle circle) {
        return this.circle.equals(circle);
    }

    public static class Circle {
        MotionEvent event;
        int pointerId;
        Point point;

        public Circle(int index, MotionEvent event) {
            this.event = event;
            this.pointerId = event.getPointerId(index);
            this.point = new Point(Math.round(event.getX(index)), Math.round(event.getY(index)));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Circle circle = (Circle) o;

            if (pointerId != circle.pointerId) return false;
            return Math.abs(point.x - circle.point.x) <= 50
                && Math.abs(point.y - circle.point.y) <= 50;
        }

        @Override
        public int hashCode() {
            int result = pointerId;
            result = 31 * result + point.hashCode();
            return result;
        }
    }
}
