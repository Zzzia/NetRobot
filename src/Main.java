import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import util.TimeUtil;
import util.netUtil.NetUtil;
import util.netUtil.ParametersBuilder;
import util.netUtil.PropertyBuilder;
import util.regexUtil.RegexUtil;

import java.util.Date;

public class Main extends Application implements NetUtil.ConnectFinishListener {

    //控件
    private Button test, start, stop;
    private TextField input_target, input_interval, match_left, match_right;
    private TextField time_hour, time_minute;
    private TextArea tv, input_formData, input_cookies;
    private Hyperlink hyperlink;

    //配置
    private String targetUrl = "", formData = "", cookies = "";
    private String match_left_string = "", match_right_string = "";
    private int interval = 50;
    private int hour, minute;
    private boolean delay = false;//定时

    //临时变量
    private StringBuilder tvContent = new StringBuilder();
    private NetUtil netUtil = null;

    //应用密码
    private static final String KEY = "ziaiscool";
    private static final String URL = "http://zzzia.net:8080/key";
    private static final String APPNAME = "netrobot";
    private boolean hasKey = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("NetRobot");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();


        //检查是否有应用使用权
        checkPassword(primaryStage);


        //寻找控件
        findId(root);


        //设置点击事件
        //测试按钮
        test.setOnAction(event -> {
            appendContent("开始测试");
            prepareInput();
            test();
        });

        //开始按钮
        start.setOnAction(event -> {
            start.setDisable(true);
            prepareInput();
            if (targetUrl.isEmpty()) return;
            if (delay) {
                Date targetDate = TimeUtil.getDate(hour, minute);
                new Thread(() -> {
                    while (true) {
                        try {
                            long restTime = targetDate.getTime() - new Date().getTime();//剩余时间
                            System.out.println(restTime);
                            if (restTime < 1000) {//不到1s，直接开始运行
                                appendContent("已开始");
                                fucking();
                                break;
                            } else {
                                tv.setText("剩余时间：" + restTime / 1000 + "秒");
                            }
                            Thread.sleep(500);//由于定时引起的误差，每0.5s检查一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                appendContent("已开始");
                fucking();
            }
        });

        //暂停按钮
        stop.setOnAction(event -> {
            netUtil.stop();
            start.setDisable(false);
            appendContent("已结束");
        });


        //超链接
        hyperlink.setOnAction(event -> {
            new WebView().getEngine().load("https://github.com/Zzzia/NetUtil");
        });
    }

    private void checkPassword(Stage primaryStage) {
        try {
            String result = new NetUtil
                    .Builder(URL)
                    .doPostAppend("/?key=netrobot")
                    .build()
                    .connect();
            if (!result.contains(KEY)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setContentText("服务器已关闭对该应用的支持");
                alert.show();
                primaryStage.close();
            } else {
                hasKey = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fucking() {
        //预留个空指针bug，看谁敢破解密码
        netUtil = getBuilder().build();
        netUtil.fuckingConnect(interval);
    }

    private void test() {
        netUtil = getBuilder().build();
        netUtil.connect(1);
    }

    private NetUtil.Builder getBuilder() {
        if (!hasKey) return null;
        var builder = new NetUtil
                .Builder(targetUrl)
                .openLog(true)
                .connectFinishListener(this);
        if (!formData.isEmpty()) {
            builder = builder.doPost(new ParametersBuilder().put(formData));
        } else {
            builder = builder.doGet();
        }
        if (!cookies.isEmpty()) {
            PropertyBuilder propertyBuilder = new PropertyBuilder()
                    .addProperty("Referer", targetUrl)
                    .addProperty("Cookie", cookies);
            builder = builder.property(propertyBuilder);
        }
        return builder;
    }

    private void findId(Parent root) {
        test = (Button) root.lookup("#button_test");
        start = (Button) root.lookup("#button_start");
        stop = (Button) root.lookup("#button_stop");
        input_target = (TextField) root.lookup("#input_target");
        input_interval = (TextField) root.lookup("#input_interval");
        time_hour = (TextField) root.lookup("#time_hour");
        time_minute = (TextField) root.lookup("#time_minute");
        tv = (TextArea) root.lookup("#tv");
        input_formData = (TextArea) root.lookup("#input_formData");
        input_cookies = (TextArea) root.lookup("#input_cookies");
        match_left = (TextField) root.lookup("#match_left");
        match_right = (TextField) root.lookup("#match_right");
        hyperlink = (Hyperlink) root.lookup("#hyperlink");
    }

    private void prepareInput() {
        targetUrl = input_target.getText();
        cookies = input_cookies.getText();
        formData = input_formData.getText();
        interval = Integer.parseInt(input_interval.getText());
        match_left_string = match_left.getText();
        match_right_string = match_right.getText();
        if (!time_hour.getText().isEmpty() && !time_minute.getText().isEmpty()) {
            hour = Integer.parseInt(time_hour.getText());
            minute = Integer.parseInt(time_minute.getText());
            delay = true;
        }
    }

    private void appendContent(String content) {
        tvContent.append(content).append("\n");
        tv.setText(tvContent.toString());
        tv.setScrollTop(Double.MAX_VALUE);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onFinish(String result) {
        if (!match_left_string.isEmpty() && !match_right_string.isEmpty()) {
            result = RegexUtil.regexInclude(match_left_string, match_right_string, result).get(0);
        }
        appendContent(result);
    }
}
