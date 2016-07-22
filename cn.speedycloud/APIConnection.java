package cn.speedycloud;

import java.util.Map;


public class APIConnection extends AbstractConnection {
    private String domain = "api.speedycloud.cn";
    private String protocol = "https";

    public APIConnection(String accessKey, String secretKey) {
        super(accessKey, secretKey);
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setProtocol(String protocol) throws Exception {
        if ((protocol != "http") || (protocol != "https")) {
            throw new Exception("Unsupport Protocol: " + protocol);
        }

        this.protocol = protocol;
    }

    public String getProtocol() {
        return this.protocol;
    }

    private String generateURL(String path) {
        return this.protocol + "://" + this.domain + path;
    }

    public String MakeRequest(String method, String path,
        Map<String, String> params) throws Exception {
        String targetUrl = this.generateURL(path);

        if (method.toUpperCase() == "GET") {
            if (params != null) {
                String queryString = this.generateQueryString(params);
                targetUrl += ("?" + queryString);
            }

            return this.Get(targetUrl);
        } else if (method.toUpperCase() == "POST") {
            return this.Post(targetUrl, params);
        } else {
            throw new Exception("Method is not support");
        }
    }
}

