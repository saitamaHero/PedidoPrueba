package com.mobile.proisa.pedidoprueba.Utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtils {
    public final static String FORMAT_NUMER_DOUBLE = "###,###,##0.00";

    public static String formatNumber(double number, DecimalFormat decimalFormat){
        return decimalFormat.format(number);
    }

    public static String formatNumber(double number, String pattern){
        DecimalFormat format = new DecimalFormat(pattern);

        return formatNumber(number, format);
    }
}
