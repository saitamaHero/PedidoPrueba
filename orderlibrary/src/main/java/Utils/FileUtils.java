package Utils;

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

    public static String addExtension(String fileName, String extension){
        if(fileName.contains(extension)) return fileName;
        return fileName.concat(extension);
    }

    public static String createFileNameDate(String prefix, String datePattern, String suffix){
        DateFormat dateFormat = new SimpleDateFormat(datePattern);

        String dateStr = dateFormat.format(Calendar.getInstance().getTime());

        return prefix.concat(dateStr).concat(suffix);
    }
}
