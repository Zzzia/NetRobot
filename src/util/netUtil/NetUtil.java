package util.netUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NetUtil {

    private String url = "";
    private String encodeType = "utf-8";
    private boolean doGet = true;
    private boolean openLog = true;
    private boolean reLocation = false;
    private int maxThreadCount = 10;
    private ParametersBuilder parametersBuilder = null;
    private PropertyBuilder propertyBuilder = new PropertyBuilder();
    private int connectTimeout = 5000;
    private int readTimeout = 5000;
    private ConnectFinishListener connectFinishListener = null;
    private ExecutorService executor;
    private ScheduledThreadPoolExecutor scheduled;

    private NetUtil(Builder builder) {
        url = builder.url;
        encodeType = builder.encodeType;
        doGet = builder.doGet;
        openLog = builder.openLog;
        parametersBuilder = builder.parametersBuilder;
        propertyBuilder = builder.propertyBuilder;
        connectTimeout = builder.connectTimeout;
        readTimeout = builder.readTimeout;
        reLocation = builder.reLocation;
        maxThreadCount = builder.maxThreadCount;
        connectFinishListener = builder.connectFinishListener;
    }

    public NetUtil(String url) {
        this.url = url;
    }

    public void stop() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        if (scheduled != null) {
            scheduled.shutdownNow();
            scheduled = null;
        }
    }

    /**
     * 连接N次
     *
     * @param count 连接的次数
     */
    public void connect(int count) {
        executor = Executors.newFixedThreadPool(maxThreadCount);
        for (int i = 0; i < count; i++) {
            executor.execute(getRunnable());
        }
    }

    /**
     * 无限连接
     *
     * @param period 间隔时间
     */
    public void fuckingConnect(long period) {
        scheduled = new ScheduledThreadPoolExecutor(2);
        scheduled.scheduleAtFixedRate(getRunnable(), 0, period, TimeUnit.MILLISECONDS);
    }

    public Runnable getRunnable() {
        return () -> {
            try {
                connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    /**
     * 普通连接
     *
     * @return String
     * @throws IOException
     */
    public String connect() throws IOException {
        if (reLocation) {
            url = ReLocateUtil.getLocate(url);
        }
        if (openLog) System.out.println("connect : " + url);
        if (openLog && !doGet) System.out.println("parameters : " + parametersBuilder);
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        URL realUrl = new URL(url);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        // 设置请求头
        for (Map.Entry<String, String> entry : propertyBuilder.getProperty().entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        // 处理post请求
        if (!doGet && parametersBuilder != null) {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(parametersBuilder.getParameters().getBytes());
            outputStream.flush();
            outputStream.close();
        }
        // 建立实际的连接
        connection.connect();
        // 定义 BufferedReader输入流来读取URL的响应
        in = new BufferedReader(new InputStreamReader(connection.getInputStream(), encodeType));
        String line;
        while ((line = in.readLine()) != null) {
            result.append(line);
        }
        in.close();
        connection.disconnect();
        String rs = result.toString();
        if (openLog) System.out.println(rs);
        if (connectFinishListener != null) connectFinishListener.onFinish(rs);
        return rs;
    }

//    private static String get(String url, String encodeType, ParametersBuilder parametersBuilder) throws IOException {
//        StringBuilder result = new StringBuilder();
//        BufferedReader in = null;
//        // post请求的参数
//        URL realUrl = new URL(url);
//        // 打开和URL之间的连接
//        HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
//        connection.setDoInput(true);
//        connection.setRequestMethod("GET");
//        connection.setConnectTimeout(5000);
//        connection.setReadTimeout(5000);
//        connection.setRequestProperty("accept", "*/*");
//        connection.setRequestProperty("connection", "Keep-Alive");
//        connection.setRequestProperty("Charsert", "UTF-8");
//        connection.setRequestProperty("user-agent",
//                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        if (parametersBuilder != null) {
//            connection.setRequestMethod("POST");
//            connection.setDoOutput(true);
//            OutputStream outputStream = connection.getOutputStream();
//            outputStream.write(parametersBuilder.getParameters().getBytes());
//            outputStream.flush();
//            outputStream.close();
//        }
//        // 建立实际的连接
//        connection.connect();
//        // 定义 BufferedReader输入流来读取URL的响应
//        in = new BufferedReader(new InputStreamReader(connection.getInputStream(), encodeType));
//        String line;
//        while ((line = in.readLine()) != null) {
//            result.append(line);
//        }
//        in.close();
//        connection.disconnect();
//        if (openLog) System.out.println(result);
//        return result.toString();
//    }

    public static class Builder {
        private String url = "";
        private String encodeType = "utf-8";
        private int maxThreadCount = 10;
        private boolean reLocation = false;
        private boolean doGet = true;
        private boolean openLog = true;
        private ParametersBuilder parametersBuilder = null;
        private PropertyBuilder propertyBuilder = new PropertyBuilder();
        private int connectTimeout = 5000;
        private int readTimeout = 5000;
        private ConnectFinishListener connectFinishListener = null;

        public Builder(String url) {
            this.url = url;
        }

        public Builder connectFinishListener(ConnectFinishListener connectFinishListener) {
            this.connectFinishListener = connectFinishListener;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder encodeType(String encodeType) {
            this.encodeType = encodeType;
            return this;
        }

        public Builder doGet() {
            doGet = true;
            return this;
        }

        public Builder doPost(ParametersBuilder parametersBuilder) {
            this.doGet = false;
            this.parametersBuilder = parametersBuilder;
            return this;
        }

        public Builder property(PropertyBuilder property) {
            this.propertyBuilder = property;
            return this;
        }

        public Builder maxThreadCount(int maxThreadCount) {
            this.maxThreadCount = maxThreadCount;
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder reLocation(boolean reLocation) {
            this.reLocation = reLocation;
            return this;
        }

        public Builder openLog(boolean openLog) {
            this.openLog = openLog;
            return this;
        }

        public NetUtil build() {
            return new NetUtil(this);
        }
    }

    public interface ConnectFinishListener {
        void onFinish(String result);
    }
}
