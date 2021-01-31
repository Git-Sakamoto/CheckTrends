package com.example.checktrends.bookmark;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;

class GetHtmlTitle implements Callable<String> {
    String url;
    String title = null;

    protected GetHtmlTitle(String url){
        this.url = url;
    }

    @Override
    public String call() throws Exception {
        InputStream response;
        try {
            response = new URL(url).openStream();

            Scanner scanner = new Scanner(response,"UTF-8");
            String responseBody = scanner.useDelimiter("\\A").next();
            title = responseBody.substring(responseBody.indexOf(">",responseBody.indexOf("<title")) + 1, responseBody.indexOf("</title>"))
                        .replace("&nbsp;", "");
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return title;
    }
}
