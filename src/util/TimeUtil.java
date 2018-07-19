package util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public static Date getDate(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute - 1);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }
}
