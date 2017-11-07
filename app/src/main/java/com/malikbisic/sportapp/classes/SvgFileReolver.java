package com.malikbisic.sportapp.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.caverock.androidsvg.SVGExternalFileResolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nane on 7.11.2017.
 */

public class SvgFileReolver extends SVGExternalFileResolver {

    @Override public Bitmap resolveImage(String filename) {
        return getBitmapFromURL(filename);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
