package com.github.ymegane.android.taptaptap.presentation.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.github.ymegane.android.taptaptap.R;

public class CircleView extends View {
    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private final Paint paint = new Paint();
    private ValueAnimator animator;
    private float defaultRadius;
    private final int DURATION = 3000;

    public void startAnimation() {
        defaultRadius = getResources().getDimensionPixelSize(R.dimen.circle_radius);
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
        float radius = (Float) (animator.getAnimatedValue());
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        paint.setAlpha(Math.round(255 * (radius/defaultRadius)));
        canvas.drawCircle(getWidth()/2, getHeight()/2, radius, paint);
    }
}
