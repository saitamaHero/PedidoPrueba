package Utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utilidades para la presentacion y conversion de fechas
 */
public class DateUtils {
    /**
     * Cadena de formato dia, mes, año
     */
    public static final String DD_MM_YYYY = "dd-MM-YYYY";

    /**
     * Cadena de formato dia, mes con tres letras, año
     */
    public static final String DD_MMM_YYYY = "dd-MMM-YYYY";

    /**
     * Cadena de formato dia, mes, año, horas(24), minutos
     */
    public static final String DD_MM_YYYY_HH_mm = "dd-MM-YYYY HH:mm";


    /**
     * Cadena de formato dia, mes, año, horas(12), minutos
     */
    public static final String DD_MM_YYYY_hh_mm = "dd-MM-YYYY hh:mm";

    /**
     * Cadena de formato dia, mes, año, horas(24), minutos
     */
    public static final String DD_MM_YYYY_HH_mm_ss = "dd-MM-YYYY HH:mm:ss";

    /**
     * Cadena de formato dia, mes, año, horas(12), minutos
     */
    public static final String DD_MM_YYYY_hh_mm_ss = "dd-MM-YYYY hh:mm:ss";

    /**
     * Formato para almacenaje de fechas en formato ISO
     */
    public static final String YYYY_MM_DD = "YYYY-MM-dd";

    /**
     * Formato para almacenaje de fechas y tiempo en formato ISO
     */
    public static final String YYYY_MM_DD_HH_mm_ss = "YYYY-MM-dd HH:mm:ss";


    /**
     * Convierte una fecha en un string para visualizar con el formato especificado
     * @param date fecha a formatear
     * @param format formato en que es requerida la fecha
     * @return un string formateado
     */
    public static String formatDate(Date date, String format){
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * Convierte un string a una fecha.
     * @param date string con la fecha.
     * @param format formato en el que se encuentra el string date.
     * @return Devuelve un objeto java.util.Date or null si el formato falla
     */
    public static Date convertToDate(String date, String format){
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date d = null;

        try {
            d = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d;
    }

    /**
     * Convierte un {@link java.sql.Timestamp} a un objeto del tipo {@link Date}
     * @param timestamp
     * @return Un objeto {@link Date}
     */
    public static Date convertToDate(Timestamp timestamp){
        Date d = timestamp;
        return d;
    }
}
