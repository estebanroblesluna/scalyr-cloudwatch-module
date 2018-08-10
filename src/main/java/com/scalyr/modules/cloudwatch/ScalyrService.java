package com.scalyr.modules.cloudwatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

public class ScalyrService {

  private static Log log = LogFactory.getLog(ScalyrService.class);
  
  private final CloseableHttpClient httpClient;

  public ScalyrService() {
    RequestConfig defaultRequestConfig = RequestConfig.custom()
        .setConnectTimeout(2000)
        .setSocketTimeout(2000)
        .setConnectionRequestTimeout(2000)
        .build();

    this.httpClient = HttpClients.custom()
        .setConnectionManager(new PoolingHttpClientConnectionManager())
        .setDefaultRequestConfig(defaultRequestConfig)
        .build();
  }
  
  /**
   * Ingests into the Scalyr account identified by scalyrWriteKey write key
   * from AWS account with credentials (awsAccessKey, awsAccessSecret)
   * starting in time lastChecked
   * 
   * @param scalyrWriteKey the scalyr write key
   * @param host the host that owns the log
   * @param logfile the log file name
   * @param parser the parser to be used
   * @param bodyBuffer the buffer of the log to upload
   * 
   * @throws ScalyrUploadException if the service is unable to upload the logs
   */
  public void flushLog(String scalyrWriteKey, String host, String logfile, String parser, StringBuilder bodyBuffer) throws ScalyrUploadException {
    if (bodyBuffer.length() > 0) {
      log.info("Flushing events to Scalyr");
      List<BasicNameValuePair> queryParams = new ArrayList<BasicNameValuePair>();
      queryParams.add(new BasicNameValuePair("token", scalyrWriteKey));
      queryParams.add(new BasicNameValuePair("host", host));
      queryParams.add(new BasicNameValuePair("logfile", logfile));
      queryParams.add(new BasicNameValuePair("parser", parser));
      
      String queryParamsAsString = URLEncodedUtils.format(queryParams, "UTF-8");

      String logs = bodyBuffer.toString();
      String requestID = DigestUtils.sha256Hex(logs);
      
      HttpPost httpPost = new HttpPost("https://www.scalyr.com/api/uploadLogs?" + queryParamsAsString);
      httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
      httpPost.setHeader("Nonce", requestID);
      
      CloseableHttpResponse response = null;
      try {
        httpPost.setEntity(new StringEntity(logs));
        response = this.httpClient.execute(httpPost);
        
        int statusCode = response.getStatusLine().getStatusCode();
        if (!(statusCode >= 200 && statusCode < 300)) {
          throw new ScalyrUploadException("Status code:" + statusCode);
        }
      } catch (ClientProtocolException e) {
        log.error("Error uploading logs to Scalyr", e);
        throw new ScalyrUploadException(e);
      } catch (IOException e) {
        log.error("Error uploading logs to Scalyr", e);
        throw new ScalyrUploadException(e);
      } finally {
        IOUtils.closeQuietly(response);
      }
    }
  }
  
  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    
    IOUtils.closeQuietly(this.httpClient);
  }
}
