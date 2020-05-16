package com.serezha2001.photoeditor.ui.SplineRefactorFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

import java.io.IOException;
import java.util.Arrays;


public class SplineInterpFragment extends Fragment {
    public static Button interpBtn, clearBtn, linearBtn, deleteBtn;
    public static EditText dotPtr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_spline, container, false);
        final DrawView drawView = (DrawView)root.findViewById(R.id.drawView);
        interpBtn = (Button)root.findViewById(R.id.interpBtn);
        interpBtn.setEnabled(false);
        clearBtn = (Button)root.findViewById(R.id.clearBtn);
        clearBtn.setEnabled(false);
        linearBtn = (Button)root.findViewById(R.id.linearBtn);
        linearBtn.setEnabled(false);
        deleteBtn = (Button)root.findViewById(R.id.deletebtn);
        dotPtr = (EditText)root.findViewById(R.id.dotPtr);
        deleteBtn.setVisibility(View.INVISIBLE);
        dotPtr.setVisibility(View.INVISIBLE);

        MainActivity.mainImage.setVisibility(View.INVISIBLE);
        interpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawView.mCanvas.drawColor(Color.WHITE);
                drawSplines();
                drawView.addDots();
                drawView.invalidate();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawView.clearScreen();
                drawView.invalidate();
                interpBtn.setEnabled(false);
                linearBtn.setEnabled(false);
                deleteBtn.setVisibility(View.INVISIBLE);
                dotPtr.setVisibility(View.INVISIBLE);
            }
        });
        linearBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawView.sortDots();
                drawView.drawLinear();
                drawView.invalidate();
                interpBtn.setEnabled(true);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int ptr = Integer.valueOf(dotPtr.getText().toString()) - 1;
                if (ptr < 1 || ptr > drawView.k - 1){
                    Toast.makeText(getContext(), "There's no dot with your number", Toast.LENGTH_LONG).show();
                }
                else {
                    drawView.deleteDot(ptr);
                    drawView.mCanvas.drawColor(Color.WHITE);
                    drawView.addDots();
                    drawView.invalidate();
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.mainImage.setVisibility(View.VISIBLE);
    }

    class cubicSpline {
        public class spline {
            public double a, b, c, d, x;
        };
        spline[] splines;

        public void getSpline(int n)
        {
            splines = new spline[n];
            for (int i = 0; i < n; ++i){
                splines[i] = new spline();
                splines[i].x = DrawView.xs[i];
                splines[i].a = DrawView.ys[i];
            }
            splines[0].c = splines[n - 1].c = 0.0;

            double[] alpha = new double[n - 1];
            double[] beta = new double[n - 1];
            alpha[0] = beta[0] = 0.0;
            for (int i = 1; i < n - 1; ++i) {
                double hi = DrawView.xs[i] - DrawView.xs[i - 1];
                double hi1 = DrawView.xs[i + 1] - DrawView.xs[i];
                double A = hi;
                double C = 2.0 * (hi + hi1);
                double B = hi1;
                double F = 6.0 * ((DrawView.ys[i + 1] - DrawView.ys[i]) / hi1 - (DrawView.ys[i] - DrawView.ys[i - 1]) / hi);
                double z = (A * alpha[i - 1] + C);
                alpha[i] = -B / z;
                beta[i] = (F - A * beta[i - 1]) / z;
            }

            for (int i = n - 2; i > 0; --i) {
                splines[i].c = alpha[i] * splines[i + 1].c + beta[i];
            }

            for (int i = n - 1; i > 0; --i) {
                double hi = DrawView.xs[i] - DrawView.xs[i - 1];
                splines[i].d = (splines[i].c - splines[i - 1].c) / hi;
                splines[i].b = hi * (2.0 * splines[i].c + splines[i - 1].c) / 6.0 + (DrawView.ys[i] - DrawView.ys[i - 1]) / hi;
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
    public void drawSplines() {
        cubicSpline sp = new cubicSpline();
        sp.getSpline(DrawView.k);
        int n = 10000;
        double h = (DrawView.xs[DrawView.k-1] - DrawView.xs[0])/(n-1);
        double[] spline = new double[n];
        for (int i = 0; i < spline.length; i++) {
            spline[i] = sp.interpolate(DrawView.xs[0]+i * h);
            DrawView.mCanvas.drawCircle((int)(DrawView.xs[0]+i * h), (int)spline[i], 6, DrawView.mPaint);
        }
    }
}

class DrawView extends View {
    Context context;
    public Bitmap mBitmap;
    public static Canvas mCanvas;
    public Path mPath;
    public Paint mBitmapPaint;
    public Paint dotPaint;
    public static Paint mPaint;
    public static int k;
    public static double[] xs, ys;

    public DrawView(Context canvas) {
        super(canvas);
        context=canvas;
        mPath = new Path();
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
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        setWillNotDraw(false);
    }

    public DrawView(Context canvas, AttributeSet attrs) {
        super(canvas, attrs);
        mPath = new Path();
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
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        setWillNotDraw(false);
    }

    public DrawView(Context canvas, AttributeSet attrs, int defStyle) {
        super(canvas, attrs, defStyle);
        mPath = new Path();
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
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        setWillNotDraw(false);
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

    private float mX, mY;
    private void touch_start(float x, float y) {
        mPath.reset();
        xs[k] = x;
        ys[k] = y;
        if (k == 0){
            mX = x;
            mY = y;
            mCanvas.drawCircle(x, y, 5, dotPaint);
            SplineInterpFragment.clearBtn.setEnabled(true);
            SplineInterpFragment.deleteBtn.setVisibility(View.VISIBLE);
            SplineInterpFragment.dotPtr.setVisibility(View.VISIBLE);
        }
        else {
            SplineInterpFragment.linearBtn.setEnabled(true);
            mPath.moveTo(x, y);
            mCanvas.drawCircle(x, y, 5, dotPaint);
           // mPath.lineTo(mX, mY);
            mCanvas.drawPath(mPath, mPaint);
            mX = x;
            mY = y;
        }
        k++;
    }

    private void touch_up() {
        mPath.reset();
    }

    public void addDots(){
        for (int i = 0; i < k; i++){
            mCanvas.drawCircle((int)xs[i], (int)ys[i], 5, dotPaint);
        }
    }

    public void drawLinear() {
        for (int i = 1; i < k; i++){
            mCanvas.drawLine((int)xs[i-1], (int)ys[i-1], (int)xs[i], (int)ys[i], mPaint);
        }
    }

    private void swap(double[] array, int ind1, int ind2) {
        double tmp = array[ind1];
        array[ind1] = array[ind2];
        array[ind2] = tmp;
    }

    public void sortDots() {
        for (int i = 1; i < k; i++) {
            if (xs[i] < xs[i - 1]) {
                swap(xs, i, i-1);
                swap(ys, i, i-1);
                for (int z = i - 1; (z - 1) >= 0; z--) {
                    if (xs[z] < xs[z - 1]) {
                        swap(xs, z, z-1);
                        swap(ys, z, z-1);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public void deleteDot(int ptr) {
        for (int i = ptr; i < k - 1; i++) {
            xs[i] = xs[i+1];
            ys[i] = ys[i+1];
        }
        k--;

    }

    public void clearScreen(){
        mCanvas.drawColor(Color.WHITE);
        k = 0;
        xs = new double[1000];
        ys = new double[1000];
        SplineInterpFragment.interpBtn.setEnabled(false);
        SplineInterpFragment.clearBtn.setEnabled(false);
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
