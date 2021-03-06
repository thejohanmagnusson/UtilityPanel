package android.johanmagnusson.se.utilitypanel.Util;

public class FormatUtils {

    public static String formatCallDuration(int callDuration) {
        int sec = callDuration % 60;
        int min = (callDuration / 60) % 60;

        return String.format("%02d:%02d", min, sec);
    }
}
