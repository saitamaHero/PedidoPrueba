package com.mobile.proisa.pedidoprueba.Utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtils {
    /**
     * Cadena de formato para números decimales, si el número es 0 el formato devolverá 0.00
     */
    public final static String FORMAT_NUMER_DOUBLE = "###,###,##0.00";
    /**
     * Cadena de formato para números decimales pero cuando es 0 retornará una cadena vacía.
     */
    public final static String FORMAT_NUMER_DOUBLE_NO_ZERO = "###,###,###.##";
    /**
     * Cadena de formato para números enteneros, si es 0 el número devolverá 0
     */
    public final static String FORMAT_NUMER_INTEGER = "###,###,##0";

    public static String formatNumber(double number, DecimalFormat decimalFormat){
        return decimalFormat.format(number);
    }

    public static String formatNumber(double number, String pattern){
        DecimalFormat format = new DecimalFormat(pattern);

        return formatNumber(number, format);
    }
}
