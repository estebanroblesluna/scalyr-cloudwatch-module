package com.scalyr.modules.cloudwatch;

import org.junit.Test;

/**
 * Test the ingestion of cloudwatch logs into Scalyr
 * 
 * @author Esteban Robles Luna
 */
public class CloudwatchServiceTest {

  @Test
  public void testCloudwatch() {
    CloudwatchService service = new CloudwatchService();
    
    String scalyrWriteKey = "";
    String awsAccessKey = "";
    String awsAccessSecret = "";
    String awsRegion = "us-east-1";
    String cloudwatchLogGroup = "syslog";
    long lastChecked = 0;
    
    CloudwatchIngestionResult result = null;
    
    do {
      result = service.ingestLogsFrom(scalyrWriteKey, awsAccessKey, awsAccessSecret, awsRegion, cloudwatchLogGroup, lastChecked);
      lastChecked = result.getLastCheckedTime();
    } while (result != null && result.hasMoreRecords());
    
    
    lastChecked = -1;
  }
}
