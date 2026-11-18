package za.co.jcms.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String formatDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return "N/A";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
            Date d = input.parse(dateStr.trim());
            return d != null ? output.format(d) : dateStr;
        } catch (Exception e) {
            return dateStr;
        }
    }

    public static String formatTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) return "N/A";
        try {
            SimpleDateFormat input = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("h:mm a", Locale.getDefault());
            if (timeStr.length() > 5) {
                timeStr = timeStr.substring(0, 5);
            }
            Date d = input.parse(timeStr.trim());
            return d != null ? output.format(d) : timeStr;
        } catch (Exception e) {
            return timeStr;
        }
    }

    public static String formatDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return "N/A";
        try {
            String clean = dateTimeStr.replace("T", " ");
            if (clean.length() >= 16) {
                clean = clean.substring(0, 16);
            }
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault());
            Date d = input.parse(clean);
            return d != null ? output.format(d) : dateTimeStr;
        } catch (Exception e) {
            return dateTimeStr;
        }
    }

    public static String formatDisplayDate(String dateStr) {
        return formatDate(dateStr);
    }

    public static String formatDisplayTime(String timeStr) {
        return formatTime(timeStr);
    }

    public static String formatDisplayDateTime(String dateTimeStr) {
        return formatDateTime(dateTimeStr);
    }

    public static String formatTimeRange(String startTime, String endTime) {
        return formatDisplayTime(startTime) + " - " + formatDisplayTime(endTime);
    }
}
