package util.regexUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    private final static int TYPE_INCLUDE = 1;
    private final static int TYPE_EXCEPT = 2;
    public static boolean openLog = false;

    //包含正则表达式
    private static String Include(String start, String end) {
        return start + "[\\s\\S]*?" + end;
    }

    //不包含正则表达式
    private static String Except(String start, String end) {
        return "(?<=" + start + ")[\\s\\S]*?(?=" + end + ")";
    }

    //解析逻辑
    private static List<String> regex(String start, String end, String source, int type) {
        List<String> list = new ArrayList<>();
        String regEx;
        //设置解析方式
        if (type == TYPE_EXCEPT) {
            regEx = Except(start, end);
        } else regEx = Include(start, end);
        Pattern pattern = Pattern.compile(regEx);
        //将目标集合里的所有内容解析出来，放到这里的list里
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            if (matcher.group() != null) {
                list.add(matcher.group());
            }
        }
        return list;
    }

    public static List<String> regexInclude(String start, String end, String source) {
        if (source == null || source.length() == 0) return new ArrayList<>();
        return regex(start, end, source, TYPE_INCLUDE);
    }

    public static List<String> regexExcept(String start, String end, String source) {
        if (source == null || source.length() == 0) return new ArrayList<>();
        return regex(start, end, source, TYPE_EXCEPT);
    }

    public static List<Tag> getTags(String tagName, String source) {
        Matcher matcher = Pattern
                .compile("<" + tagName + ".*?>[\\s\\S]*?</" + tagName + ">|<" + tagName + "[\\s\\S]*?>")
                .matcher(source);
        List<Tag> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(new Tag(matcher.group()));
        }
        return list;
    }


    /**
     * 同步获取html文件，默认编码utf-8
     *
     * @param url
     * @return
     */
    public static String getHtml(String url) throws IOException {
        return getHtml(url, "utf-8");
    }

    public static String getHtml(String url, String encodeType) throws IOException {
        if (openLog) System.out.println("connect : " + url);
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        URL realUrl = new URL(url);
        // 打开和URL之间的连接
        URLConnection connection = realUrl.openConnection();
        // 设置通用的请求属性
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("Charsert", "UTF-8");
        connection.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        // 建立实际的连接
        connection.connect();
        // 定义 BufferedReader输入流来读取URL的响应
        in = new BufferedReader(new InputStreamReader(connection.getInputStream(), encodeType));
        String line;
        while ((line = in.readLine()) != null) {
            result.append(line);
        }
        in.close();
        if (openLog) System.out.println(result);
        return result.toString();
    }

    /**
     * 用于解析类似<a...>..</a>的工具
     */
    public static class Tag {
        private String tagName;
        private String text;
        private String html;
        private String prefix;//前缀
        private String suffix;//后缀

        @Override
        public String toString() {
            return "html: " + getHtml() + "\n"
                    + "tagName: " + getTagName() + "\n"
                    + "text: " + getText() + "\n"
                    + "prefix: " + getPrefix() + "\n"
                    + "suffix: " + getSuffix() + "\n";
        }

        public Tag(String html) {
            this.html = html;
        }

        public String getTagName() {
            if (tagName == null)
                setTagName();
            return tagName;
        }

        private void setTagName() {
            Matcher matcher = Pattern.compile("<.*?/(.*?)>").matcher(getSuffix());
            if (matcher.find())
                tagName = matcher.group(1);
            if (tagName == null) tagName = "";
        }

        public String getText() {
            if (text == null)
                setText();
            return text;
        }

        private void setText() {
            Matcher matcher = Pattern.compile("<.*?>([\\s\\S]*)<.*?>").matcher(getHtml());
            if (matcher.find())
                text = matcher.group(1);
            if (text == null) text = "";
        }

        public String getHtml() {
            return html;
        }

        public String getPrefix() {
            if (prefix == null)
                setSPfix();
            return prefix;
        }

        //设置前后缀
        private void setSPfix() {
            Matcher matcher = Pattern.compile("<.*?>").matcher(html);
            if (matcher.find()) {
                prefix = matcher.group();
            }
            while (matcher.find()) {
                suffix = matcher.group();
            }

            if (prefix == null) prefix = "";
            if (suffix == null) suffix = "";
        }

        public String getSuffix() {
            if (suffix == null)
                setSPfix();
            return suffix;
        }

        //通过关键字获取属性
        public String getValue(String key) {
            Matcher matcher = Pattern.compile(key + "='(.*?)'|" + key + "=\"(.*?)\"").matcher(getPrefix());
            while (matcher.find()) {
                if (matcher.group(1) != null)
                    return matcher.group(1);
                if (matcher.group(2) != null)
                    return matcher.group(2);
            }
            return "";
        }
    }

}



