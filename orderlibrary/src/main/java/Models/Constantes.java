package Models;

import android.os.Environment;

import java.io.File;

public class Constantes {
    public static final String USER_DATA = "user_data";
    public static final String USER = "field_user";
    public static final String VENDOR_CODE = "vendor_code";
    public static final String VENDOR_NAME = "vendor_name";


    public static final String MAIN_DIR = Environment.getExternalStorageDirectory().toString()
            .concat(File.separator).concat(".proisa").concat(File.separator);
    public static final String ITEMS_PHOTOS = File.separator.concat("articulos")
            .concat(File.separator);


    public static final String REGEX_PHONE_CHARATERS = "\\(|\\)|-";
}
