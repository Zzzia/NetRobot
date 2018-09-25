package util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public static Date getDate(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute - 1);
        calendar.set(Calendar.SECOND, 59);
        if (calendar.getTime().compareTo(new Date(System.currentTimeMillis())) > 0) {
            System.out.println("calendar.getTime().compareTo(new Date(System.currentTimeMillis())) > 0");
            return calendar.getTime();
        } else {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
            System.out.println(calendar.getTime());
            return calendar.getTime();
        }
    }

}
