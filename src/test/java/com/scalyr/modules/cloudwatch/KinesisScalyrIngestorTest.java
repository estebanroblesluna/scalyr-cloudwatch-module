package com.scalyr.modules.cloudwatch;

import software.amazon.awssdk.regions.Region;

/**
 * Test for {@link KinesisScalyrIngestor}
 * It is just a runner class as the fetching is async using Kinesis streams
 * 
 * @author Esteban Robles Luna
 */
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
}
