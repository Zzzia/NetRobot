package util.netUtil;

public class ParametersBuilder {
    private StringBuilder parameters = new StringBuilder();
    private boolean isJson = true;

    public ParametersBuilder putJson(String key, String value) {
        isJson = true;
        parameters.append(key);
        parameters.append("=");
        parameters.append(value);
        parameters.append("&");
        return this;
    }

    public ParametersBuilder put(String str) {
        isJson = false;
        parameters.append(str);
        return this;
    }

    public String getParameters() {
        if (isJson) {
            int lastPosition = parameters.length();
            parameters.delete(lastPosition - 1, lastPosition);
            return parameters.toString();
        } else return parameters.toString();
    }

    @Override
    public String toString() {
        return getParameters();
    }
}