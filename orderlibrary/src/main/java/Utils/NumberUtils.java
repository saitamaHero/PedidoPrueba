package Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import Models.ITotal;


/**
 * Clase que provee utilidades para el manejo de numeros y su representacion.
 */
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

    public static final char DECIMAL_SEPARATOR = '.';
    public static final char MILLAR_SEPARATOR = ',';

    public static String formatNumber(double number, DecimalFormat decimalFormat){
        return decimalFormat.format(number);
    }

    public static String formatNumber(double number, String pattern){
        DecimalFormat format = new DecimalFormat(pattern);

        return formatNumber(number, format);
    }


    public static DecimalFormat getDefaultDecimalFormat(){
        DecimalFormat decimalFormat = new DecimalFormat();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(DECIMAL_SEPARATOR);
        symbols.setGroupingSeparator(MILLAR_SEPARATOR);

        decimalFormat.setDecimalFormatSymbols(symbols);

        return decimalFormat;
    }

    public static int getPercent(float value, float sum){
        float p = ( value / sum) * 100.00f;
        return Math.round(p);
    }

    public static float getFloatPercent(float value, float sum){
        float p = ( value / sum) * 100.00f;
        return p;
    }


    public static double getTotal(ArrayList<ITotal> totals) {
        double total = 0;

        for (ITotal t : totals) {
            total += t.getTotal();
        }

        return total;
    }

    public static String generateSequence(int nZeros, int sequence){
        String format = String.format(Locale.getDefault(), "%%0%dd",nZeros);
        return String.format(format,sequence);
    }
}
