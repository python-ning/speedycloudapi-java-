package cn.speedycloud;

import java.util.HashMap;
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



    public static void main(String[] args) throws Exception{
        String SECRET_KEY = "54c5119b762a029531fbbd316dd247941d3f69e8ac776d8438b53f9107e48ee1";
        String ACCESS_KEY = "28491DE7825141F1C5C3ADCFC9A6B3B9";
        Map<String, String> dict = new HashMap<String, String>();
        APIConnection speedyapi = new APIConnection(ACCESS_KEY, SECRET_KEY);
        String response = speedyapi.MakeRequest("GET", "/api/v1/products/cloud_servers", dict);
        System.out.println(response);
    }

}