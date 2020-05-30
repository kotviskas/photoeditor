package com.serezha2001.photoeditor.ui.ZoomFragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

public class ZoomFragment extends Fragment {

    private SeekBar Coef;
    private TextView coefView;
    private Bitmap prevBitmap;
    private ProgressBar progressBar;
    private LinearLayout btnsLayout;
    private Asynced task;

    class Asynced extends AsyncTask<Integer, Void, Void> {
        Bitmap redactBitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Coef.setVisibility(View.INVISIBLE);
            coefView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... angle) {
            redactBitmap = bilinearFiltration(cropImage(angle[0]), angle[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            if (redactBitmap != null) {
                MainActivity.mainImage.setImageBitmap(redactBitmap);
            }
            btnsLayout.setVisibility(View.VISIBLE);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_zoom, container, false);
        MainActivity.mainImage.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        Coef = (SeekBar) root.findViewById(R.id.zoomCoef);
        Coef.setMax(19);
        Coef.setProgress(0);
        coefView = (TextView) root.findViewById(R.id.coefView);
        coefView.setText("1x");
        btnsLayout = (LinearLayout) root.findViewById(R.id.processBtnsLayout);
        btnsLayout.setVisibility(View.INVISIBLE);
        Button applyBtn = (Button) root.findViewById(R.id.applyBtn);
        Button cancelBtn = (Button) root.findViewById(R.id.cancelBtn);

        Coef.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                coefView.setText(String.valueOf(Coef.getProgress() + 1) + "x");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (prevBitmap == null) {
                    prevBitmap = ((BitmapDrawable) MainActivity.mainImage.getDrawable()).getBitmap();
                }
                int scale = (int) (Coef.getProgress() + 1);
                coefView.setText(String.valueOf(scale) + "x");
                task = new Asynced();
                task.execute(scale);
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnsLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                Coef.setVisibility(View.VISIBLE);
                coefView.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mainImage.setImageBitmap(prevBitmap);
                Coef.setProgress(0);
                coefView.setText("1x");
                btnsLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                Coef.setVisibility(View.VISIBLE);
                coefView.setVisibility(View.VISIBLE);
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            task.cancel(true);
        } catch (Exception e) {
            //Toast.makeText(getContext(), ""+e, Toast.LENGTH_LONG).show();
        }
    }

/*    private Bitmap imageCrop(int scale) {
        try {
            Bitmap croppedBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            for (int x = 0, prevx = 0; x < croppedBitmap.getWidth(); x += scale, prevx++) {
                for (int y = 0, prevy = 0; y < croppedBitmap.getHeight(); y += scale, prevy++) {
                    int prevBitmapPixel = prevBitmap.getPixel(prevBitmap.getWidth() / 2 - prevBitmap.getWidth() / scale / 2 + prevx, prevBitmap.getHeight() / 2 - prevBitmap.getHeight() / scale / 2 + prevy);
                    int newPixel = Color.argb(Color.alpha(prevBitmapPixel), Color.red(prevBitmapPixel), Color.green(prevBitmapPixel), Color.blue(prevBitmapPixel));
                    for (int i = 0; i < scale && x + i < croppedBitmap.getWidth(); i++) {
                        for (int j = 0; j < scale && y + j < croppedBitmap.getHeight(); j++) {
                            croppedBitmap.setPixel(x + i, y + j, newPixel);
                        }
                    }
                }
            }
            return croppedBitmap;
            //MainActivity.mainImage.setImageBitmap(croppedBitmap);
            //Undo.setEnabled(true);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error! " + e, Toast.LENGTH_SHORT).show();
            return null;
        }
    }*/


    private int[] cropImage(int scale) {
        final int[] pixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        final int[] newPixels = new int[(prevBitmap.getWidth() / scale) * (prevBitmap.getHeight() / scale)];
        prevBitmap.getPixels(pixels, 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());

        int startX = prevBitmap.getWidth() / 2 - prevBitmap.getWidth() / scale / 2;
        int startY = prevBitmap.getHeight() / 2 - prevBitmap.getHeight() / scale / 2;

        for (int x = 0; x < prevBitmap.getWidth() / scale; x++) {
            for (int y = 0; y < prevBitmap.getHeight() / scale; y++) {
                newPixels[prevBitmap.getWidth() / scale * y + x] = pixels[prevBitmap.getWidth() * (startY + y) + startX + x];
            }
        }

        return newPixels;
    }

    private Bitmap bilinearFiltration(int[] pixels, int scale) {
        int w2 = (prevBitmap.getWidth() / scale) * scale;
        int h2 = (prevBitmap.getHeight() / scale) * scale;
        int[] newPixels = new int[w2 * h2];

        int a, b, c, d, x, y, index, offset = 0;
        float x_ratio = (float) (prevBitmap.getWidth() / scale - 1) / w2, y_ratio = (float) (prevBitmap.getHeight() / scale - 1) / h2;
        float x_diff, y_diff, red, green, blue;

        for (int i = 0; i < h2; i++) {
            for (int j = 0; j < w2; j++) {
                x = (int) (x_ratio * j);
                y = (int) (y_ratio * i);
                x_diff = x_ratio * j - x;
                y_diff = y_ratio * i - y;
                index = y * (prevBitmap.getWidth() / scale) + x;
                a = pixels[index];
                b = pixels[index + 1];
                c = pixels[index + (prevBitmap.getWidth() / scale)];
                d = pixels[index + (prevBitmap.getWidth() / scale) + 1];

                blue = (a & 0xff) * (1 - x_diff) * (1 - y_diff) + (b & 0xff) * (x_diff) * (1 - y_diff) +
                        (c & 0xff) * (y_diff) * (1 - x_diff) + (d & 0xff) * (x_diff * y_diff);

                green = ((a >> 8) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 8) & 0xff) * (x_diff) * (1 - y_diff) +
                        ((c >> 8) & 0xff) * (y_diff) * (1 - x_diff) + ((d >> 8) & 0xff) * (x_diff * y_diff);

                red = ((a >> 16) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 16) & 0xff) * (x_diff) * (1 - y_diff) +
                        ((c >> 16) & 0xff) * (y_diff) * (1 - x_diff) + ((d >> 16) & 0xff) * (x_diff * y_diff);

                newPixels[offset++] = 0xff000000 | ((((int) red) << 16) & 0xff0000) | ((((int) green) << 8) & 0xff00) | ((int) blue);
            }
        }

        return Bitmap.createBitmap(newPixels, w2, h2, Bitmap.Config.ARGB_8888);
    }

}
