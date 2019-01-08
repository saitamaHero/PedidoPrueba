package Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class FileUtils {
    public static String PNG_EXT = ".png";
    public static String JPG_EXT = ".jpg";
    public static String PDF_EXT = ".pdf";
    public static final  int DEFAULT_QUALITY = 30;
    public static final  int GOOD_QUALITY = 80;

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

    public static Uri compressPhoto(File dest, Uri uri, int quality) throws NullPointerException, IOException {
        FileOutputStream fos = null;

        if(!dest.exists())
            dest.mkdirs();

        File archivo = new File(dest,uri.getLastPathSegment());
        Bitmap bm = BitmapFactory.decodeFile(uri.getPath(),new BitmapFactory.Options());

        try {
            fos = new FileOutputStream(archivo);

            bm.compress(Bitmap.CompressFormat.JPEG, quality, fos);


            uri = Uri.fromFile(archivo);

            fos.flush();
            fos.close();
            fos = null;

        } catch (IOException e) {
            throw  e;
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }

        return uri;
    }

    public static File createFileRoute(String mainRoute, String subFolder){
        File file;

        file = new File(mainRoute, subFolder);

        return file;
    }

    public static InputStream getFileInputStream(File file) throws FileNotFoundException {
        if(file == null) throw new NullPointerException();
        return  new FileInputStream(file);
    }

    public static Bitmap getBitmapFrom(InputStream inputStream) throws IOException {
        if(inputStream != null && inputStream.available() > 0){
            Bitmap bm = BitmapFactory.decodeStream(inputStream);
            return bm;
        }

        return null;
    }


    public static String encodeBase64(File file){
        try {
            InputStream inputStream = new FileInputStream(file);

            //inputStream.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] fileArray = new byte[(int) file.length()];
        String encodedFile = "";
        try {
            encodedFile = android.util.Base64.encodeToString(fileArray, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            // Manejar Error
        }

        return encodedFile;
    }

    public static Bitmap decodeBase64(String strToDecode){
        byte[] bytes = android.util.Base64.decode(strToDecode, android.util.Base64.DEFAULT);

        Bitmap bm = null;

        try{
            bm = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }


        return bm;
    }
}
