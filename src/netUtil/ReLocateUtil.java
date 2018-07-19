package netUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReLocateUtil {

    public static String getLocate(String url) throws IOException {
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        String location = conn.getHeaderField("Location");
        conn.disconnect();
        if (location == null) {
            return url;
        } else {
            return location;
        }
    }
}
