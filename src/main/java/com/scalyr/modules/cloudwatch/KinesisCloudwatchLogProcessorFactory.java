package com.scalyr.modules.cloudwatch;

import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

/**
 * A {@link ShardRecordProcessorFactory} for {@link KinesisCloudwatchLogProcessor}
 * 
 * @author Esteban Robles Luna
 */
public class KinesisCloudwatchLogProcessorFactory implements ShardRecordProcessorFactory {
  
  private final AWSAccountToScalyrMapper awsAccountToScalyrMapper;
  
  public KinesisCloudwatchLogProcessorFactory(AWSAccountToScalyrMapper awsAccountToScalyrMapper) {
    this.awsAccountToScalyrMapper = awsAccountToScalyrMapper;
  }
  
  /**
   * {@inheritDoc}
   */
  public ShardRecordProcessor shardRecordProcessor() {
    return new KinesisCloudwatchLogProcessor(this.awsAccountToScalyrMapper);
  }
}
