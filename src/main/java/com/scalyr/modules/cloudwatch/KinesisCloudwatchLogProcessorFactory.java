package com.scalyr.modules.cloudwatch;

import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

public class KinesisCloudwatchLogProcessorFactory implements ShardRecordProcessorFactory {
  
  private final AWSAccountToScalyrMapper awsAccountToScalyrMapper;
  
  public KinesisCloudwatchLogProcessorFactory(AWSAccountToScalyrMapper awsAccountToScalyrMapper) {
    this.awsAccountToScalyrMapper = awsAccountToScalyrMapper;
  }
  
  public ShardRecordProcessor shardRecordProcessor() {
    return new KinesisCloudwatchLogProcessor(this.awsAccountToScalyrMapper);
  }
}
