package cn.speedycloud;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class AbstractConnection {
    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    private String accessKey;
    private String secretKey;
    private int statusCode;

    public AbstractConnection(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public int GetStatusCode() {
        return this.statusCode;
    }

    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[(j * 2) + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    private String generateAuthorization(String method, String path,
        String dateStr) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac = Mac.getInstance("HmacSHA1");
        SecretKey key = new SecretKeySpec(this.secretKey.getBytes(), "HmacSHA1");
        hmac.init(key);

        StringBuilder sb = new StringBuilder();
        sb.append(method.toUpperCase() + "\n");
        sb.append(path + "\n");
        sb.append(dateStr + "\n");
        hmac.update(sb.toString().getBytes());

        byte[] result = hmac.doFinal();
        String hexStr = this.bytesToHex(result);

        return this.accessKey + "," + hexStr;
    }

    protected Map<String, String> GenerateRequestHeaders(String method,
        String path) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",
                Locale.US);
        String dateStr = format.format(new Date());
        String authStr = this.generateAuthorization(method, path, dateStr);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type",
            "application/x-www-form-urlencoded; charset=utf-8");
        headers.put("Date", dateStr);
        headers.put("Authorization", authStr);

        return headers;
    }

    protected HttpURLConnection GetConnection(String method, String targetUrl)
        throws Exception {
        URL url = new URL(targetUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method.toUpperCase());

        Map<String, String> baseHeaders = this.GenerateRequestHeaders(method,
                url.getPath());

        for (String key : baseHeaders.keySet()) {
            conn.setRequestProperty(key, baseHeaders.get(key));
        }

        return conn;
    }

    public String Get(String targetUrl) throws Exception {
        BufferedReader in = null;

        try {
            HttpURLConnection conn = this.GetConnection("GET", targetUrl);
            this.statusCode = conn.getResponseCode();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    protected String generateQueryString(Map<String, String> params) {
        List<String> items = new ArrayList<String>();

        for (String key : params.keySet()) {
            items.add(key + "=" + params.get(key));
        }

        return String.join("&", items);
    }

    public String Post(String targetUrl, Map<String, String> params)
        throws Exception {
        HttpURLConnection conn = this.GetConnection("POST", targetUrl);
        String postData = "";

        if (params != null) {
            postData = this.generateQueryString(params);
        }

        conn.setDoOutput(true);

        DataOutputStream wr = null;

        try {
            wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
        } finally {
            if (wr != null) {
                wr.close();
            }
        }

        this.statusCode = conn.getResponseCode();

        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
