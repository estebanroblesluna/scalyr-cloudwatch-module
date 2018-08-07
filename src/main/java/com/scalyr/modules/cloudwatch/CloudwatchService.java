package com.scalyr.modules.cloudwatch;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import com.amazonaws.services.logs.model.FilteredLogEvent;
import com.scalyr.api.logs.EventAttributes;
import com.scalyr.api.logs.Events;

/**
 * A cloudwatch service to ingest logs into Scalyr
 * 
 * @author Esteban Robles Luna
 */
public class CloudwatchService {

  private static Log log = LogFactory.getLog(CloudwatchService.class);
  
  /**
   * Ingests into the Scalyr account identified by scalyrWriteKey write key
   * from AWS account with credentials (awsAccessKey, awsAccessSecret)
   * starting in time lastChecked
   * 
   * @param scalyrWriteKey the scalyr write key
   * @param awsAccessKey the aws access key
   * @param awsAccessSecret the aws access secret
   * @param awsRegion the aws region
   * @param cloudwatchLogGroup the log group
   * @param lastChecked the start point for checking the logs
   * @return the ingestion result
   */
  public CloudwatchIngestionResult ingestLogsFrom(String scalyrWriteKey, String awsAccessKey, String awsAccessSecret, String awsRegion, String cloudwatchLogGroup, long lastChecked) {
    log.info("Initializing Scalyr");
    int maxBufferRam = 4 * 1024 * 1024;
    Events.init(scalyrWriteKey, maxBufferRam);
    
    log.info("Connecting to Cloudwatch service");
    BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAccessKey, awsAccessSecret);

    AWSLogs logs = AWSLogsClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        .withRegion(awsRegion)
        .build();
    
    
    log.info("Fetching logs");
    FilterLogEventsRequest filterLogEventsRequest = new FilterLogEventsRequest();
    filterLogEventsRequest.setLogGroupName(cloudwatchLogGroup);
    filterLogEventsRequest.setStartTime(lastChecked);
    
    FilterLogEventsResult result = logs.filterLogEvents(filterLogEventsRequest);
    
    long ingestedCount = 0;
    long lastTime = 0;
    
    log.info("Start processing logs...");
    for (FilteredLogEvent event : result.getEvents()) {
      String message = event.getMessage();
      String streamName = event.getLogStreamName();
      long timestamp = event.getTimestamp();
      
      ingestedCount++;
      lastTime = Math.max(lastTime, timestamp);
      this.logToScalyr(timestamp, streamName, message);
    }
    
    log.info("Finished processing logs. Processed: " + ingestedCount);
    
    log.info("Flushing events to Scalyr");
    Events.flush();

    return new CloudwatchIngestionResult(ingestedCount, lastTime, StringUtils.isNotBlank(result.getNextToken()));
  }
  
  private void logToScalyr(long timestamp, String streamName, String message) {
    Events.info(new EventAttributes(
        "timestamp", timestamp,
        "streamName", streamName,
        "message", message
    ));
  }
}
