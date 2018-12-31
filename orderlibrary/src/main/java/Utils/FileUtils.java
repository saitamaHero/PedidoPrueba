package Utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class FileUtils {
    public static String PNG_EXT = ".png";
    public static String JPG_EXT = ".jpg";
    public static String PDF_EXT = ".pdf";

    public static String createTmpFileName(){
        return UUID.randomUUID().toString().substring(0,7);
    }

    public static String createTmpFileName(String ext){
        return createTmpFileName().concat(ext);
    }

    public static String addExtension(String fileName, String extension){
        if(fileName.contains(extension)) return fileName;
        return fileName.concat(extension);
    }

    public static String createFileNameDate(String prefix, String datePattern, String suffix){
        DateFormat dateFormat = new SimpleDateFormat(datePattern);

        String dateStr = dateFormat.format(Calendar.getInstance().getTime());

        return prefix.concat(dateStr).concat(suffix);
    }


    public static boolean savePhoto(Bitmap bm, File route, String name, int quality) {

        if(bm == null){
            return false;
        }

        FileOutputStream fos = null;
        boolean saved = false;


        if(!route.exists()) {
            route.mkdirs();
        }

        File archivo = new File(route,name);

        try {
            fos = new FileOutputStream(archivo);

            bm.compress(Bitmap.CompressFormat.JPEG, quality, fos);

            fos.flush();
            fos.close();
            fos = null;

            saved = true;
        } catch (IOException e) {
           e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return saved;
    }

    public static File createFileRoute(String mainRoute, String subFolder){
        File file;

        file = new File(mainRoute, subFolder);

        return file;
    }
}
