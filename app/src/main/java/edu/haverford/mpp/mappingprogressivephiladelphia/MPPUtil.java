package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by BrianG on 3/31/2015.
 */
public class MPPUtil {

    public static String saveToInternalSorage(Bitmap bitmapImage, Context context, int ID, String directoryName) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(directoryName, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_data/imageDir
        File path = new File(directory,"mpp_profile_pic_" + Integer.toString(ID)); // Create imageDir

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(path);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos); // Use the compress method on the BitMap object to write image to the OutputStream
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return directory.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(String path) {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}