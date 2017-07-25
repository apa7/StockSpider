package top.apa7.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Downloader {

    public class ErrorCodeException extends RuntimeException {

        public ErrorCodeException(int code, String msg) {
            super(msg);
            this.responseCode = code;
        }

        public int getResponseCode() {
            return responseCode;
        }

        private int responseCode = 0;
    }

    private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpClient;
    private RequestConfig requestConfig;
    private int retryTimes;
    private Charset defaultCharSet = null;

    public Downloader() {
        cm.setMaxTotal(50);
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        requestConfig = RequestConfig.custom().setConnectionRequestTimeout(60000).
                setSocketTimeout(60000).setConnectTimeout(60000).setMaxRedirects(1000).
                setCircularRedirectsAllowed(true).setRelativeRedirectsAllowed(true).build();
        retryTimes = 3;
    }

    public void setDefaultCharset(Charset charset) {
        this.defaultCharSet = charset;
    }

    public void setCookie(String cookie) {
        mCookie = cookie;
    }

    public String getHtmlByUrl(String url) {
        HttpGet httpget = null;
        CloseableHttpResponse response = null;
        for (int i = 0; i < retryTimes; i++) {
            try {
                String host = url;
                host = url.substring(url.indexOf("//") + 2);
                host = host.substring(0, host.indexOf("/"));

                url = url.replace(" ", "");
                httpget = new HttpGet(url);//以get方式请求该URL
                httpget.setHeader("Host", host);

                httpget.setHeader("User-Agent", "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.125 Safari/537.36");
                httpget.setConfig(requestConfig);
                if (!mCookie.isEmpty())
                    httpget.setHeader("Cookie", mCookie);
                httpget.setHeader("Referer", url);
                for (String key : mHeaders.keySet()) {
                    httpget.setHeader(key, mHeaders.get(key));
                }

                response = httpClient.execute(httpget);//得到responce对象
                int resStatu = response.getStatusLine().getStatusCode();//返回码
                if (resStatu== HttpStatus.SC_OK) {//200正常
                    HttpEntity entity = response.getEntity();
                    if (entity!=null) {
                        return extractEntityString(entity);
                    }
                }
            } catch (IOException e) {

            } catch (Exception e) {

            } finally {
                try {
                    httpget.abort();
                    if (response != null) {
                        EntityUtils.consumeQuietly(response.getEntity());
                        response.close();
                    }
                } catch (Exception e) {

                }
                if (httpget != null)
                    httpget.releaseConnection();
            }
        }
        return "";
    }

    public String putHtmlByUrl(String url, String bodyData) {
        return fetchHtmlByUrl(url, bodyData, false);
    }

    private String fetchHtmlByUrl(
            String url, String bodyData, boolean isPost) throws ErrorCodeException {
        HttpEntityEnclosingRequestBase httpRequest = null;
        CloseableHttpResponse response = null;
        Charset charset = Charset.defaultCharset();
        for (int i = 0; i < retryTimes; i++) {
            try {
                if (isPost)
                    httpRequest = new HttpPost(url);
                else
                    httpRequest = new HttpPut(url);
                httpRequest.setConfig(requestConfig);
                StringEntity strEntity = new StringEntity(bodyData, charset);
                strEntity.setContentType("application/x-www-form-urlencoded");
                if (bodyData.startsWith("[") || bodyData.startsWith("{"))
                    strEntity.setContentType("application/json; charset=UTF-8");

                httpRequest.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux i686; rv:17.0) Gecko/20131029 Firefox/17.0");
                httpRequest.setEntity(strEntity);
                for (String key : mHeaders.keySet())
                    httpRequest.setHeader(key, mHeaders.get(key));

                if (!mCookie.isEmpty())
                    httpRequest.setHeader("Cookie", mCookie);
                response = httpClient.execute(httpRequest);//得到responce对象
                int resStatu = response.getStatusLine().getStatusCode();//返回码
                response.containsHeader("Set-Cookie");
                if (resStatu == HttpStatus.SC_OK) {//200正常
                    HttpEntity entity = response.getEntity();
                    //获得相应实体
                    if (entity!=null) {
                        String html = EntityUtils.toString(entity, charset);//获得html源代码
                        return html;
                    }
                }
                if (resStatu == HttpStatus.SC_MOVED_TEMPORARILY)
                    return "";
                String msg = EntityUtils.toString(response.getEntity(), charset);
                throw  new ErrorCodeException(resStatu, msg);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        return "";
    }

    public String postHtmlByUrl(String url, String bodyData) {
        return fetchHtmlByUrl(url, bodyData, true);
    }

    public String deleteHtmlByUrl(String url) throws Exception {
        HttpDelete httpRequest = new HttpDelete(url);
        CloseableHttpResponse response = null;
        for (int i = 0; i < retryTimes; i++) {
            try {
                httpRequest.setConfig(requestConfig);
                httpRequest.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux i686; rv:17.0) Gecko/20131029 Firefox/17.0");
                for (String key : mHeaders.keySet())
                    httpRequest.setHeader(key, mHeaders.get(key));

                if (!mCookie.isEmpty())
                    httpRequest.setHeader("Cookie", mCookie);
                response = httpClient.execute(httpRequest);
                int resStatu = response.getStatusLine().getStatusCode();
                response.containsHeader("Set-Cookie");
                if (resStatu == HttpStatus.SC_OK) {//200正常
                    HttpEntity entity = response.getEntity();
                    //获得相应实体
                    if (entity!=null) {
                        String html = EntityUtils.toString(entity);
                        return html;
                    }
                }
                if (resStatu == HttpStatus.SC_MOVED_TEMPORARILY)
                    return "";
                String msg = EntityUtils.toString(response.getEntity());
                throw  new ErrorCodeException(resStatu, msg);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        return "";
    }

    public void addHeader(String key, String name) {
        mHeaders.put(key, name);
    }

    private String extractEntityString(HttpEntity entity) {
        InputStream instream = null;
        try {
            instream = entity.getContent();
        } catch (Exception e) {
            return "";
        }

        CharArrayBuffer buffer = null;
        try {
            int i = (int)entity.getContentLength();
            if (i <= 0) {
                i = 1024 * 1024 * 32;
            }
            Charset charset = null;
            try {
                final ContentType contentType = ContentType.get(entity);
                if (contentType != null) {
                    charset = contentType.getCharset();
                }
            } catch (final UnsupportedCharsetException ex) {

            }
            if (charset == null) {
                if (defaultCharSet == null)
                    charset = Charset.defaultCharset();
                else
                    charset = defaultCharSet;
            }
            if (charset == null) {
                charset = HTTP.DEF_CONTENT_CHARSET;
            }
            final Reader reader = new InputStreamReader(instream, charset);
            buffer = new CharArrayBuffer(i);
            final char[] tmp = new char[1];
            int l;
            while((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toString();
        } catch (Exception e) {
            return buffer.toString();
        } finally {
            try {
                instream.close();
            } catch (Exception e) {

            }
        }
    }

    public boolean downloadFile(String url, String destPath) {
        HttpGet httpget = null;
        CloseableHttpResponse response = null;
        try {
            httpget = new HttpGet(url); // 以get方式请求该URL
            httpget.setConfig(requestConfig);
            response = httpClient.execute(httpget);
            InputStream imageStream = new BufferedInputStream(response.getEntity().getContent());

            File outFile = new File(destPath);
            if (outFile.exists())
                return true;

            outFile.createNewFile();
            OutputStream ofs = new FileOutputStream(outFile);
            int size = 0;
            byte[] buffer = new byte[1024 * 32];
            while ((size = imageStream.read(buffer)) >= 0) {
                ofs.write(buffer, 0, size);
            }
            imageStream.close();
            ofs.close();

        } catch (Exception e) {
            return false;
        } finally {
            try {
                response.close();
            } catch (Exception e) {

            }
            httpget.releaseConnection();
        }
        return true;
    }

    private String mCookie = "";
    private Map<String, String> mHeaders = new HashMap<String, String>();
    private static Logger logger = LogManager.getLogger();
}