package com.github.ymegane.android.taptaptap.presentation.view;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import com.github.ymegane.android.taptaptap.presentation.view.CircleView.Circle;

public class TapView extends FrameLayout {

    private OnTapListener tapListener;

    public TapView(Context context) {
        super(context);
        init();
    }

    public TapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setOnTapListener(OnTapListener listener) {
        this.tapListener = listener;
    }

    private void init() {
        prepareForTouchEvent();
    }

    private long lastVibratedTime;
    private Subscription subscription;
    private void prepareForTouchEvent() {
        subscription = RxView.touches(this).flatMap(new Func1<MotionEvent, Observable<Circle>>() {
            @Override
            public Observable<Circle> call(MotionEvent event) {
                int n = event.getPointerCount();

                List<Circle> circles = new ArrayList<>(n);
                for (int i=0; i<n; i++) {
                    Circle circle = new Circle(i, event);
                    circles.add(circle);
                }
                return Observable.from(circles);
            }
        }).doOnNext(new Action1<Circle>() {
            @Override
            public void call(Circle circle) {
                MotionEvent event = circle.event;
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (event.getEventTime() - lastVibratedTime > 600) {
                        lastVibratedTime = event.getEventTime();
                        vibrate();
                    }
                    CircleView view = findCircleView(circle);
                    if (view != null) {
                        view.continueCancelTimer();
                    }
                }
            }
        }).doOnNext(new Action1<Circle>() {
            @Override
            public void call(Circle circle) {
                MotionEvent event = circle.event;
                if (event.getActionMasked() == MotionEvent.ACTION_UP
                        || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP
                        || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                    CircleView view = findCircleView(circle);
                    if (view != null) {
                        view.startAnimation();
                    }
                }
            }
        }).filter(new Func1<Circle, Boolean>() {
            @Override
            public Boolean call(Circle circle) {
                MotionEvent event = circle.event;
                return event.getActionMasked() == MotionEvent.ACTION_DOWN ||
                        event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN;
            }
        }).filter(new Func1<Circle, Boolean>() {
            @Override
            public Boolean call(Circle circle) {
                return findCircleView(circle) == null;
            }
        })
        .filter(new Func1<Circle, Boolean>() {
            @Override
            public Boolean call(Circle circle) {
                return getChildCount() < 100;
            }
        })
        .subscribe(new Observer<Circle>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Circle circle) {
                addCircleView(circle);
                vibratePattern();
                if (tapListener != null) {
                    tapListener.onTap();
                }
            }
        });
    }

    public void release() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    @Nullable
    private CircleView findCircleView(Circle circle) {
        int n = getChildCount();
        for (int i=0; i < n; i++) {
            CircleView child = (CircleView) getChildAt(i);
            if (child.equals(circle)) {
                return child;
            }
        }
        return null;
    }

    private void addCircleView(Circle circle) {
        CircleView view = new CircleView(getContext(), circle);
        addView(view);
        view.invalidate();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (!vibrator.hasVibrator()) {
            return;
        }

        vibrator.vibrate(500);
    }

    private static final long[] PATTERN = new long[]{100, 500};

    private void vibratePattern() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (!vibrator.hasVibrator()) {
            return;
        }

        vibrator.vibrate(PATTERN, 1);
    }

    public interface OnTapListener {
        void onTap();
    }
}
