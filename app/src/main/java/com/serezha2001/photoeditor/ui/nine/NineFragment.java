package com.serezha2001.photoeditor.ui.nine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;


public class NineFragment extends Fragment {
    public Button interpBtn, clearBtn;
    public static Bitmap mBitmap;
    public static Canvas mCanvas;
    public static Path mPath;
    public static Paint mBitmapPaint;
    public static Paint dotPaint;
    public static Paint mPaint;
    public static int k;
    public static double[] xs, ys;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final DrawView kek = new DrawView(getActivity());
        View root = inflater.inflate(R.layout.fragment_nine, container, false);

        interpBtn = (Button)root.findViewById(R.id.interpBtn);
        clearBtn = (Button)root.findViewById(R.id.clearBtn);

        dotPaint = new Paint();
        dotPaint.setAntiAlias(true);
        dotPaint.setColor(Color.RED);
        dotPaint.setStyle(Paint.Style.STROKE);
        dotPaint.setStrokeCap(Paint.Cap.ROUND);
        dotPaint.setStrokeWidth(15);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);

        MainActivity.mainImage.setVisibility(View.INVISIBLE);
        interpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                drawSplines();
                kek.validateDots();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                k = 0;
                xs = new double[1000];
                ys = new double[1000];
            }
        });
        return root;
    }

    public static class DrawView extends View {
        Context context;

        public DrawView(Context canvas) {
            super(canvas);
            context=canvas;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        public DrawView(Context canvas, AttributeSet attrs) {
            super(canvas, attrs);
        }

        public DrawView(Context canvas, AttributeSet attrs, int defStyle) {
            super(canvas, attrs, defStyle);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            k = 0;
            xs = new double[1000];
            ys = new double[1000];
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
        }

        protected void validateDots(){
            for (int i = 0; i < xs.length; i++){
                mCanvas.drawCircle((int)xs[i], (int)ys[i], 10, dotPaint);
            }
            invalidate();
        }

        private float mX, mY;
        private void touch_start(float x, float y) {
            mPath.reset();
            xs[k] = x;
            ys[k] = y;
            if (k == 0){
                mX = x;
                mY = y;
                mCanvas.drawCircle(x, y, 10, dotPaint);
            }
            else {
                mPath.moveTo(x, y);
                mCanvas.drawCircle(x, y, 10, dotPaint);
                mPath.lineTo(mX, mY);
                mCanvas.drawPath(mPath, mPaint);
                mX = x;
                mY = y;
                }
            k++;
        }

        private void touch_up() {
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }

    static class cubicSpline {
        public class spline {
            public double a, b, c, d, x;
        };
        spline[] splines;

        public void getSpline(int n)
        {
            splines = new spline[n];
            for (int i = 0; i < n; ++i){
                splines[i] = new spline();
                splines[i].x = xs[i];
                splines[i].a = ys[i];
            }
            splines[0].c = splines[n - 1].c = 0.0;

            double[] alpha = new double[n - 1];
            double[] beta = new double[n - 1];
            alpha[0] = beta[0] = 0.0;
            for (int i = 1; i < n - 1; ++i) {
                double hi = xs[i] - xs[i - 1];
                double hi1 = xs[i + 1] - xs[i];
                double A = hi;
                double C = 2.0 * (hi + hi1);
                double B = hi1;
                double F = 6.0 * ((ys[i + 1] - ys[i]) / hi1 - (ys[i] - ys[i - 1]) / hi);
                double z = (A * alpha[i - 1] + C);
                alpha[i] = -B / z;
                beta[i] = (F - A * beta[i - 1]) / z;
            }

            for (int i = n - 2; i > 0; --i) {
                splines[i].c = alpha[i] * splines[i + 1].c + beta[i];
            }

            for (int i = n - 1; i > 0; --i) {
                double hi = xs[i] - xs[i - 1];
                splines[i].d = (splines[i].c - splines[i - 1].c) / hi;
                splines[i].b = hi * (2.0 * splines[i].c + splines[i - 1].c) / 6.0 + (ys[i] - ys[i - 1]) / hi;
            }
        }

        public double interpolate(double x) {
            int n = splines.length;
            spline s;
            if (x >= splines[n - 1].x) {
                s = splines[n - 1];
            }
            else {
                int i = 0;
                int j = n - 1;
                while (i + 1 < j) {
                    int k = i + (j - i) / 2;
                    if (x <= splines[k].x) {
                        j = k;
                    }
                    else {
                        i = k;
                    }
                }
                s = splines[j];
            }

            double dx = x - s.x;
            return s.a + (s.b + (s.c / 2.0 + s.d * dx / 6.0) * dx) * dx;
        }
    }
    public static void drawSplines() {
        cubicSpline sp = new cubicSpline();
        sp.getSpline(k);
        int n = 1000;
        double h = (xs[k-1] - xs[0])/(n-1);
        double[] spline = new double[n];
        for (int i = 0; i < spline.length; i++) {
            spline[i] = sp.interpolate(xs[0]+i * h);
            mCanvas.drawCircle((int)(xs[0]+i * h), (int)spline[i], 7, mPaint);
        }

    }

}


