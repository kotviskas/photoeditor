package com.serezha2001.photoeditor.ui.SplineInterpFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
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


public class SplineInterpFragment extends Fragment {
    static Button interpBtn, clearBtn, linearBtn, deleteBtn;
    static EditText dotPtr;
    static boolean isRedacting = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_spline, container, false);
        final DrawView drawView = (DrawView) root.findViewById(R.id.drawView);
        interpBtn = (Button) root.findViewById(R.id.interpBtn);
        interpBtn.setEnabled(false);
        clearBtn = (Button) root.findViewById(R.id.clearBtn);
        clearBtn.setEnabled(false);
        linearBtn = (Button) root.findViewById(R.id.linearBtn);
        linearBtn.setEnabled(false);
        deleteBtn = (Button) root.findViewById(R.id.deletebtn);
        dotPtr = (EditText) root.findViewById(R.id.dotPtr);
        deleteBtn.setVisibility(View.INVISIBLE);
        dotPtr.setVisibility(View.INVISIBLE);

        Toast.makeText(getContext(), "Tap to add a point!\nYou can move points after interpolation.", Toast.LENGTH_LONG).show();

        MainActivity.mainImage.setVisibility(View.INVISIBLE);
        interpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRedacting = true;
                drawView.mCanvas.drawColor(Color.WHITE);
                drawView.sortDots();
                drawSplines();
                drawView.addDots();
                drawView.invalidate();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRedacting = false;
                drawView.clearScreen();
                drawView.invalidate();
                interpBtn.setEnabled(false);
                linearBtn.setEnabled(false);
                deleteBtn.setVisibility(View.INVISIBLE);
                dotPtr.setVisibility(View.INVISIBLE);
            }
        });
        linearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRedacting = false;
                drawView.sortDots();
                drawView.drawLinear();
                drawView.invalidate();
                interpBtn.setEnabled(true);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int ptr = Integer.valueOf(dotPtr.getText().toString()) - 1;
                    if (ptr < 0 || ptr > drawView.k - 1) {
                        Toast.makeText(getContext(), "There's no dot with your number", Toast.LENGTH_LONG).show();
                    } else {
                        drawView.deleteDot(ptr);
                        drawView.mCanvas.drawColor(Color.WHITE);
                        drawView.addDots();
                        drawView.invalidate();
                    }
                    isRedacting = false;
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Enter number", Toast.LENGTH_LONG).show();
                }
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        return root;
    }

    static class cubicSpline {
        static class spline {
            double a, b, c, d, x;
        }

        ;
        spline[] splines;

        void getSpline(int n) {
            splines = new spline[n];
            for (int i = 0; i < n; ++i) {
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
                double C = 2.0 * (hi + hi1);
                double F = 6.0 * ((DrawView.ys[i + 1] - DrawView.ys[i]) / hi1 - (DrawView.ys[i] - DrawView.ys[i - 1]) / hi);
                double z = (hi * alpha[i - 1] + C);
                alpha[i] = -hi1 / z;
                beta[i] = (F - hi * beta[i - 1]) / z;
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

        double interpolate(double x) {
            int n = splines.length;
            spline s;
            if (x >= splines[n - 1].x) {
                s = splines[n - 1];
            } else {
                int i = 0;
                int j = n - 1;
                while (i + 1 < j) {
                    int k = i + (j - i) / 2;
                    if (x <= splines[k].x) {
                        j = k;
                    } else {
                        i = k;
                    }
                }
                s = splines[j];
            }

            double dx = x - s.x;
            return s.a + (s.b + (s.c / 2.0 + s.d * dx / 6.0) * dx) * dx;
        }
    }

    static void drawSplines() {
        cubicSpline sp = new cubicSpline();
        sp.getSpline(DrawView.k);
        int n = 900;
        double h = (DrawView.xs[DrawView.k - 1] - DrawView.xs[0]) / (n - 1);
        double[] spline = new double[n];
        spline[0] = sp.interpolate(DrawView.xs[0] + 0 * h);
        for (int i = 1; i < spline.length; i++) {
            spline[i] = sp.interpolate(DrawView.xs[0] + i * h);
            DrawView.mCanvas.drawLine((float) (DrawView.xs[0] + (i - 1) * h), (float) spline[i - 1], (float) (DrawView.xs[0] + i * h), (float) spline[i], DrawView.mPaint);
            //DrawView.mCanvas.drawCircle((int)(DrawView.xs[0]+i * h), (int)spline[i], 6, DrawView.mPaint);
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
    public static int k, dotToChange = 0;
    public static double[] xs, ys;

    public DrawView(Context canvas) {
        super(canvas);
        context = canvas;
        initVals();
    }

    public DrawView(Context canvas, AttributeSet attrs) {
        super(canvas, attrs);
        initVals();
    }

    public DrawView(Context canvas, AttributeSet attrs, int defStyle) {
        super(canvas, attrs, defStyle);
        initVals();
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
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }

    protected void initVals() {
        mPath = new Path();
        dotPaint = new Paint();
        dotPaint.setAntiAlias(true);
        dotPaint.setColor(Color.RED);
        dotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        dotPaint.setStrokeCap(Paint.Cap.ROUND);
        dotPaint.setStrokeWidth(20);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(25);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        setWillNotDraw(false);
        SplineInterpFragment.isRedacting = false;
    }

    private void touch_start(float x, float y) {
        if (!SplineInterpFragment.isRedacting) {
            mPath.reset();
            xs[k] = x;
            ys[k] = y;
            if (k == 0) {
                mCanvas.drawCircle(x, y, 5, dotPaint);
                SplineInterpFragment.clearBtn.setEnabled(true);
                SplineInterpFragment.deleteBtn.setVisibility(View.VISIBLE);
                SplineInterpFragment.dotPtr.setVisibility(View.VISIBLE);
            } else {
                SplineInterpFragment.linearBtn.setEnabled(true);
                mPath.moveTo(x, y);
                mCanvas.drawCircle(x, y, 5, dotPaint);
                mCanvas.drawPath(mPath, mPaint);
            }
            k++;
        } else {
            float minDotDist = 10000;
            for (int i = 0; i < k; i++) {
                //if (Math.abs(xs[i] + ys[i] - x - y) < minDotDist) {
                if (Math.sqrt((xs[i] - x) * (xs[i] - x) + (ys[i] - y) * (ys[i] - y)) < minDotDist) {
                    //minDotDist = (float)Math.abs(xs[i] + ys[i] - x - y);
                    minDotDist = (float) Math.sqrt((xs[i] - x) * (xs[i] - x) + (ys[i] - y) * (ys[i] - y));
                    dotToChange = i;
                }
            }
        }
    }

    private void touch_up() {
        mPath.reset();
    }

    private void touch_move(float x, float y) {
        if (SplineInterpFragment.isRedacting) {
            xs[dotToChange] = x;
            ys[dotToChange] = y;
            sortDots();
            mCanvas.drawColor(Color.WHITE);
            SplineInterpFragment.drawSplines();
            addDots();
        }
    }

    public void addDots() {
        for (int i = 0; i < k; i++) {
            mCanvas.drawCircle((int) xs[i], (int) ys[i], 9, dotPaint);
        }
    }

    public void drawLinear() {
        mCanvas.drawColor(Color.WHITE);
        for (int i = 1; i < k; i++) {
            mCanvas.drawLine((int) xs[i - 1], (int) ys[i - 1], (int) xs[i], (int) ys[i], mPaint);
        }
        addDots();
    }

    /*public void drawLinear() {
        for (int i = 0; i < k; i++) {
            float k = (float) ((ys[i + 1] - ys[i]) / (xs[i + 1] - xs[i])), b = (float) (ys[i] - k * xs[i]);
            for (float midx = (float) (xs[i] + 0.05); midx < xs[i + 1] - 0.05; midx += 0.05) {
                mCanvas.drawCircle((int) midx, (int) (k * midx + b), 5, mPaint);
            }
        }
        addDots();
    }*/

    private void swap(double[] array, int ind1, int ind2) {
        double tmp = array[ind1];
        array[ind1] = array[ind2];
        array[ind2] = tmp;
    }

    public void sortDots() {
        for (int i = 1; i < k; i++) {
            if (xs[i] < xs[i - 1]) {
                swap(xs, i, i - 1);
                swap(ys, i, i - 1);
                for (int z = i - 1; (z - 1) >= 0; z--) {
                    if (xs[z] < xs[z - 1]) {
                        swap(xs, z, z - 1);
                        swap(ys, z, z - 1);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public void deleteDot(int ptr) {
        for (int i = ptr; i < k - 1; i++) {
            xs[i] = xs[i + 1];
            ys[i] = ys[i + 1];
        }
        k--;

    }

    public void clearScreen() {
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
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;

        }
        return true;
    }
}
