package com.github.ymegane.android.taptaptap.presentation.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.github.ymegane.android.taptaptap.R;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TapView extends FrameLayout {

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

    private void init() {
        RxView.touches(this)
                .filter(new Func1<MotionEvent, Boolean>() {
                    @Override
                    public Boolean call(MotionEvent motionEvent) {
                        return motionEvent.getAction() == MotionEvent.ACTION_DOWN;
                    }
                })
                .flatMap(new Func1<MotionEvent, Observable<Point>>() {
                    @Override
                    public Observable<Point> call(MotionEvent motionEvent) {
                        int n = motionEvent.getPointerCount();
                        List<Point> points = new ArrayList<>(n);
                        for (int i=0; i<n; i++) {
                            points.add(new Point((int)motionEvent.getX(i), (int)motionEvent.getY(i)));
                        }
                        return Observable.from(points);
                    }
                })
                .filter(new Func1<Point, Boolean>() {
                    @Override
                    public Boolean call(Point point) {
                        return getChildCount() < 30;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Point>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Point point) {
                        addCircleView(point);
                    }
                });
    }

    private synchronized void addCircleView(Point point) {
        CircleView view = new CircleView(getContext());
        view.setElevation(getResources().getDimension(R.dimen.circle_elevation));
        int width = getResources().getDimensionPixelSize(R.dimen.circle_width);
        int height = getResources().getDimensionPixelSize(R.dimen.circle_width);
        LayoutParams params = new LayoutParams(width, height);
        params.setMargins(point.x - width/2, point.y - height/2, 0, 0);
        view.setLayoutParams(params);
        addView(view);

        view.startAnimation();
    }
}
