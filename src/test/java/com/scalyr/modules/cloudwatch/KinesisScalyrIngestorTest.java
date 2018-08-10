package com.scalyr.modules.cloudwatch;

import org.junit.Test;

import software.amazon.awssdk.regions.Region;

public class KinesisScalyrIngestorTest {

  public static void main(String[] args) {
    String awsClientAccountId = "";
    String scalyrWriteKey = "";
    String awsAccessKey = "";
    String awsAccessSecret = "";
    Region awsRegion = Region.of("us-east-1");
    String cloudwatchLogGroup = "syslog";
    String streamName = "ScalyrCloudwatchLogsStream";
    String parser = "test-parser";
    
    InMemoryAWSAccountToScalyrMapper mapper = new InMemoryAWSAccountToScalyrMapper();
    mapper.setScalyrKeyMapping(awsClientAccountId, scalyrWriteKey);
    mapper.setParserMapping(awsClientAccountId, cloudwatchLogGroup, parser);

    KinesisScalyrIngestor ingestor = new KinesisScalyrIngestor(awsRegion, streamName, awsAccessKey, awsAccessSecret, mapper);
    ingestor.start();
  }
  
  @Test
  public void testIngestion() {
    String awsClientAccountId = "";
    String scalyrWriteKey = "";
    String awsAccessKey = "";
    String awsAccessSecret = "";
    Region awsRegion = Region.of("us-east-1");
    String cloudwatchLogGroup = "syslog";
    String streamName = "ScalyrCloudwatchLogsStream";
    String parser = "test-parser";
    
    InMemoryAWSAccountToScalyrMapper mapper = new InMemoryAWSAccountToScalyrMapper();
    mapper.setScalyrKeyMapping(awsClientAccountId, scalyrWriteKey);
    mapper.setParserMapping(awsClientAccountId, cloudwatchLogGroup, parser);

    KinesisScalyrIngestor ingestor = new KinesisScalyrIngestor(awsRegion, streamName, awsAccessKey, awsAccessSecret, mapper);
    ingestor.start();
    
    int i = 0;
    
  }
}
