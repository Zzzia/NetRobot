import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import netUtil.NetUtil;
import netUtil.ParametersBuilder;
import netUtil.PropertyBuilder;
import regexUtil.RegexUtil;

public class Main extends Application implements NetUtil.ConnectFinishListener {

    //控件
    private Button test, start, stop;
    private TextField input_target, input_interval, match_left, match_right;
    private TextArea tv, input_formData, input_cookies;

    //配置
    private String targetUrl = "", formData = "", cookies = "";
    private String match_left_string = "", match_right_string = "";
    private int interval = 50;

    //临时变量
    private StringBuilder tvContent = new StringBuilder();
    private NetUtil netUtil = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("NetRobot");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        findId(root);
        test.setOnAction(event -> {
            appendContent("开始测试");
            prepareInput();
            test();
        });
        start.setOnAction(event -> {
            appendContent("已开始");
            prepareInput();
            fucking();
        });
        stop.setOnAction(event -> {
            appendContent("已结束");
            netUtil.stop();
        });
    }

    private void fucking() {
        netUtil = getBuilder().build();
        netUtil.fuckingConnect(interval);
    }

    private void test() {
        netUtil = getBuilder().build();
        netUtil.connect(1);
    }

    private NetUtil.Builder getBuilder() {
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
        tv = (TextArea) root.lookup("#tv");
        input_formData = (TextArea) root.lookup("#input_formData");
        input_cookies = (TextArea) root.lookup("#input_cookies");
        match_left = (TextField) root.lookup("#match_left");
        match_right = (TextField) root.lookup("#match_right");
    }

    private void prepareInput() {
        targetUrl = input_target.getText();
        cookies = input_cookies.getText();
        formData = input_formData.getText();
        interval = Integer.parseInt(input_interval.getText());
        match_left_string = match_left.getText();
        match_right_string = match_right.getText();
    }

    private void appendContent(String content) {
        tvContent.append(content).append("\n");
        tv.setText(tvContent.toString());
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
