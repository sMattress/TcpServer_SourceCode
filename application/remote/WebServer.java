package application.remote;

import application.model.ApplicationMsg;
import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by zfly on 2017/4/26.
 */
public enum  WebServer {

    INSTANCE("http://smartmattress.lesmarthome.com");

    WebServer(String serverURL) {
        SERVER_URL = serverURL;
    }

    private final String SERVER_URL;
    private final OkHttpClient client = new OkHttpClient() {{
        setConnectTimeout(3, TimeUnit.SECONDS);
    }};

    public void hardwareOnline(String name) {
        final String SERVICE_FORMAT = SERVER_URL + "/v1/hardware/connect?name=%s";
        final String url = String.format(SERVICE_FORMAT, name);

        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("hardwareOnline => " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("hardwareOnline => " + response.body().string());
            }
        });
    }

    public void hardwareOffline(String name) {
        final String SERVICE_FORMAT = SERVER_URL + "/v1/hardware/disconnect?name=%s";
        final String url = String.format(SERVICE_FORMAT, name);

        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("hardwareOffline => " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("hardwareOffline => " + response.body().string());
            }
        });
    }

    public boolean hasSendPermission(String from, String to) {
        final String SERVICE_FORMAT = SERVER_URL + "/v1/sys/work?from=%s&to=%S";
        final String url = String.format(SERVICE_FORMAT, from, to);
        try {
            final Request request = new Request.Builder().url(url).build();
            final Response response = client.newCall(request).execute();
            final String data = response.body().string();
            final ApplicationMsg body = JSON.parseObject(data, ApplicationMsg.class);
            System.out.println("hasSendPermission => " + data);
            return body.getFlag() == 1;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
