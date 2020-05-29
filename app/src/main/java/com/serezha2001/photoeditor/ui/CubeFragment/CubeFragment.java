package com.serezha2001.photoeditor.ui.CubeFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;


import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;


public class CubeFragment extends Fragment {

    static class dotVector {
        double x, y, z;
        dotVector(){

        }
        dotVector (double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        Color color;
    }

    static dotVector[] projected;
    private dotVector[] points;
    private double[][] finalMatrix;
    private double angleX = 180, angleY = 180, angleZ = 180;
    private double[][] projection = {
            {1, 0, 0},
            {0, 1, 0}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cube, container, false);
        MainActivity.mainImage.setVisibility(View.INVISIBLE);
        final DrawView drawView = root.findViewById(R.id.drawViewCube);
        SeekBar seekBarX = root.findViewById(R.id.seekBarX);
        SeekBar seekBarY = root.findViewById(R.id.seekBarY);
        SeekBar seekBarZ = root.findViewById(R.id.seekBarZ);
        seekBarX.setMax(360);
        seekBarX.setProgress(180);
        seekBarY.setMax(360);
        seekBarY.setProgress(180);
        seekBarZ.setMax(360);
        seekBarZ.setProgress(180);
        Toast.makeText(getContext(), "Change one of the axes with the slider to draw a cube!", Toast.LENGTH_LONG).show();

        points = new dotVector[8];
        points[0] = new dotVector(-0.5, -0.5, -0.5);
        points[1] = new dotVector(0.5, -0.5, -0.5);
        points[2] = new dotVector(0.5, 0.5, -0.5);
        points[3] = new dotVector(-0.5, 0.5, -0.5);
        points[4] = new dotVector(-0.5, -0.5, 0.5);
        points[5] = new dotVector(0.5, -0.5, 0.5);
        points[6] = new dotVector(0.5, 0.5, 0.5);
        points[7] = new dotVector(-0.5, 0.5, 0.5);

        seekBarX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angleX = (double)(seekBar.getProgress() * Math.PI / 180);
                drawCube(drawView);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        seekBarY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angleY = (double)(seekBar.getProgress() * Math.PI / 180);
                drawCube(drawView);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        seekBarZ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angleZ = (double)(seekBar.getProgress() * Math.PI / 180);
                drawCube(drawView);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        
        return root;
    }

    void drawCube(DrawView drawView) {
        drawView.mCanvas.drawColor(Color.WHITE);
        projected = new dotVector[8];
        calculateMatrix(angleX, angleY, angleZ);
        calculateProjection();
        drawView.scaleCoords();
        drawView.drawNums();
        for (int i = 0; i < 4; i++) {
            drawView.connect(i, (i+1) % 4, projected);
            drawView.connect(i+4, ((i+1) % 4)+4, projected);
            drawView.connect(i, i+4, projected);
        }
    }

    void calculateMatrix(double x, double y, double z) {
        double[][] rotationZ = {
                { Math.cos(z), -Math.sin(z), 0},
                { Math.sin(z), Math.cos(z), 0},
                { 0, 0, 1}
        };

        double[][] rotationX = {
                { 1, 0, 0},
                { 0, Math.cos(x), -Math.sin(x)},
                { 0, Math.sin(x), Math.cos(x)}
        };

        double[][] rotationY = {
                { Math.cos(y), 0, Math.sin(y)},
                { 0, 1, 0},
                { -Math.sin(y), 0, Math.cos(y)}
        };
        finalMatrix = matrixMultiply(rotationX, rotationY);
        finalMatrix = matrixMultiply(finalMatrix, rotationZ);
    }

    void calculateProjection() {
        int index = 0;
        for (dotVector v : points) {
            projected[index] = matrixMultiply(projection, matrixMultiply(finalMatrix, v));
            index++;
        }
    }

    double[][] matrixMultiply(double[][] a, double[][] b) {
        int colsA = a[0].length;
        int rowsA = a.length;
        int colsB = b[0].length;
        int rowsB = b.length;
        if (colsA != rowsB) {
            return null;
        }

        double result[][] = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                float sum = 0;
                for (int k = 0; k < colsA; k++) {
                    sum += a[i][k] * b[k][j];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    double[][] vecToMatrix(dotVector v) {
        double[][] m = new double[3][1];
        m[0][0] = v.x;
        m[1][0] = v.y;
        m[2][0] = v.z;
        return m;
    }

    dotVector matrixToVec(double[][] m) {
        dotVector v = new dotVector();
        v.x = m[0][0];
        v.y = m[1][0];
        if (m.length > 2) {
            v.z = m[2][0];
        }
        return v;
    }

    dotVector matrixMultiply(double[][] a, dotVector b) {
        double[][] m = vecToMatrix(b);
        return matrixToVec(matrixMultiply(a,m));
    }
}

class DrawView extends View {
    Context context;
    private Bitmap mBitmap;
    public Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint, dotPaint, mPaint, numPaint;

    public DrawView(Context canvas) {
        super(canvas);
        context=canvas;
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

    protected void initVals() {
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
        numPaint = new Paint();
        numPaint.setAntiAlias(true);
        numPaint.setColor(Color.RED);
        numPaint.setTextSize(35.0f);
        numPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    void connect(int i, int j, CubeFragment.dotVector[] points) {
        CubeFragment.dotVector a = points[i];
        CubeFragment.dotVector b = points[j];
        mCanvas.drawLine((float)a.x + getWidth()/2, (float)a.y + getHeight()/2, (float)b.x + getWidth()/2, (float)b.y + getHeight()/2, mPaint);
        mCanvas.drawCircle((float)a.x + getWidth()/2, (float)a.y + getHeight()/2, 5, dotPaint);
        mCanvas.drawCircle((float)b.x + getWidth()/2, (float)b.y + getHeight()/2, 5, dotPaint);
        invalidate();
    }

    void drawNums() {
        mCanvas.drawText("6", (float)(CubeFragment.projected[0].x + getWidth()/2 + ((CubeFragment.projected[2].x - CubeFragment.projected[0].x) / 2)), (float)(CubeFragment.projected[0].y + getHeight()/2 + ((CubeFragment.projected[2].y - CubeFragment.projected[0].y) / 2)), numPaint);
        mCanvas.drawText("5", (float)(CubeFragment.projected[3].x + getWidth()/2 + ((CubeFragment.projected[6].x - CubeFragment.projected[3].x) / 2)), (float)(CubeFragment.projected[3].y + getHeight()/2 + ((CubeFragment.projected[6].y - CubeFragment.projected[3].y) / 2)), numPaint);
        mCanvas.drawText("4", (float)(CubeFragment.projected[0].x + getWidth()/2 + ((CubeFragment.projected[5].x - CubeFragment.projected[0].x) / 2)), (float)(CubeFragment.projected[0].y + getHeight()/2 + ((CubeFragment.projected[5].y - CubeFragment.projected[0].y) / 2)), numPaint);
        mCanvas.drawText("3", (float)(CubeFragment.projected[1].x + getWidth()/2 + ((CubeFragment.projected[6].x - CubeFragment.projected[1].x) / 2)), (float)(CubeFragment.projected[1].y + getHeight()/2 + ((CubeFragment.projected[6].y - CubeFragment.projected[1].y) / 2)), numPaint);
        mCanvas.drawText("2", (float)(CubeFragment.projected[4].x + getWidth()/2 + ((CubeFragment.projected[6].x - CubeFragment.projected[4].x) / 2)), (float)(CubeFragment.projected[4].y + getHeight()/2 + ((CubeFragment.projected[6].y - CubeFragment.projected[4].y) / 2)), numPaint);
        mCanvas.drawText("1", (float)(CubeFragment.projected[0].x + getWidth()/2 + ((CubeFragment.projected[7].x - CubeFragment.projected[0].x) / 2)), (float)(CubeFragment.projected[0].y + getHeight()/2 + ((CubeFragment.projected[7].y - CubeFragment.projected[0].y) / 2)), numPaint);
    }

    void scaleCoords() {
        for (CubeFragment.dotVector v : CubeFragment.projected) {
            v.x *= Math.min(getWidth(), getHeight())/3;
            v.y *= Math.min(getWidth(), getHeight())/3;
        }
    }
}

