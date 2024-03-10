package com.cochranelibrary.domain;

import com.cochranelibrary.domain.port.HttpClientPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
@Service
@Slf4j
public class HttpClientService implements HttpClientPort {

    private final CloseableHttpClient httpClient;

    public HttpClientService() {
        httpClient = this.getCloseableHttpClient();
    }

    public String executeHttpGet(String url) {
        HttpGet request = new HttpGet(url);
        this.setHeaders(request);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                log.error("HTTP connection not OK: " + statusCode);
                return null;
            }

            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            log.error("Failed to execute HTTP GET request", e);
            return null;
        }
    }

    public void close() throws IOException {
        this.httpClient.close();
    }

    private CloseableHttpClient getCloseableHttpClient() {
        /*RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(55 * 1000)
                .setConnectionRequestTimeout( 55 * 1000)
                .setSocketTimeout(55 * 1000).build();*/
        PoolingHttpClientConnectionManager poolingConnManager
                = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(50);
        poolingConnManager.setDefaultMaxPerRoute(40);
        return HttpClientBuilder.create()
                .setConnectionManager(poolingConnManager)
                .setDefaultRequestConfig(RequestConfig.DEFAULT).build();
    }

    private void setHeaders(HttpGet request) {
        request.addHeader("authority", "www.cochranelibrary.com");
        request.addHeader("accept", "application/json, text/javascript, */*; q=0.01");
        request.addHeader("accept-encoding", "gzip, deflate, utf-8");
        request.addHeader("accept-language", "en-US,en;q=0.9,ak-GH;q=0.8,ak;q=0.7,bn-BD;q=0.6,bn;q=0.5,agq-CM;q=0.4,agq;q=0.3,ar-XB;q=0.2,ar;q=0.1,bm-ML;q=0.1,bm;q=0.1");
        request.addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36");
    }


}
