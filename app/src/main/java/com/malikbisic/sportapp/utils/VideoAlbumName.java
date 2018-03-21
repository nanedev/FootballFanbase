package com.malikbisic.sportapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.provider.MediaStore;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by malikbisic on 20/03/2018.
 */

public class VideoAlbumName {

    Context context;

    public VideoAlbumName(Context context) {
        this.context = context;
    }

    // Reading file paths from SDCard
    public ArrayList<String> getFilePaths() {
        // The list of columns we're interested in:
        String[] columns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_ADDED};

        final Cursor cursor = context.getContentResolver().
                query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, // Specify the provider
                        columns, // The columns we're interested in
                        null, // A WHERE-filter query
                        null, // The arguments for the filter-query
                        MediaStore.Video.Media.DATE_ADDED + " DESC" // Order the results, newest first
                );

        ArrayList<String> result = new ArrayList<String>(cursor.getCount());

        if (cursor.moveToFirst()) {
            final int image_path_col = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            do {
                result.add(cursor.getString(image_path_col));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    // Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());

        if (ImageConstant.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;

    }

    /*
     * getting screen width
     */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }
}
