package util.netUtil;

import java.util.HashMap;
import java.util.Map;

public class PropertyBuilder {

    private final Map<String, String> properties = getDefaultProperty();

    public PropertyBuilder addProperty(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public PropertyBuilder addProperty(Map<String,String> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public Map<String, String> getProperty() {
        return properties;
    }

    private Map<String, String> getDefaultProperty() {
        Map<String, String> propertys = new HashMap<>();
        propertys.put("accept", "*/*");
        propertys.put("connection", "Keep-Alive");
        propertys.put("Charsert", "UTF-8");
        propertys.put("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        propertys.put("Content-Type", "application/x-www-form-urlencoded");
        return propertys;
    }
}
