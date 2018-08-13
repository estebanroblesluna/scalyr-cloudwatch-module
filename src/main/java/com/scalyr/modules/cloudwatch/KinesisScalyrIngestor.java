package com.scalyr.modules.cloudwatch;

import java.util.UUID;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.coordinator.Scheduler;

/**
 * An ingestor of CloudWatch logs produced inside Kinesis into Scalyr
 * 
 * @author Esteban Robles Luna
 */
public class KinesisScalyrIngestor {

  private final KinesisAsyncClient kinesisClient;
  private final String streamName;
  private final Region region;
  private final AWSAccountToScalyrMapper mapper;
  
  public KinesisScalyrIngestor(Region region, String streamName, String awsAccessKey, String awsAccessSecret, AWSAccountToScalyrMapper mapper) {
    this.streamName = streamName;
    this.region = region;
    this.mapper = mapper;
    
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsAccessKey, awsAccessSecret);

    this.kinesisClient = KinesisAsyncClient
        .builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
        .build();
  }
  
  /**
   * Starts the ingestion process in the background
   */
  public void start() {
    DynamoDbAsyncClient dynamoClient = DynamoDbAsyncClient
        .builder()
        .region(this.region)
        .build();
    
    CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient
        .builder()
        .region(this.region)
        .build();
    
    ConfigsBuilder configsBuilder = new ConfigsBuilder(
        this.streamName, 
        "scalyr-consumer", 
        this.kinesisClient, 
        dynamoClient, 
        cloudWatchClient, 
        UUID.randomUUID().toString(), 
        new KinesisCloudwatchLogProcessorFactory(this.mapper));

    Scheduler scheduler = new Scheduler(
            configsBuilder.checkpointConfig(),
            configsBuilder.coordinatorConfig(),
            configsBuilder.leaseManagementConfig(),
            configsBuilder.lifecycleConfig(),
            configsBuilder.metricsConfig(),
            configsBuilder.processorConfig(),
            configsBuilder.retrievalConfig()
    );

    Thread schedulerThread = new Thread(scheduler);
    schedulerThread.setDaemon(true);
    schedulerThread.start();
  }
}
