package com.scalyr.modules.cloudwatch;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.KinesisClientLibDependencyException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.exceptions.ThrottlingException;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

public class KinesisCloudwatchLogProcessor implements ShardRecordProcessor {

  private static Log log = LogFactory.getLog(KinesisCloudwatchLogProcessor.class);

  private String shardId;
  private final ScalyrService scalyrService;
  private final ObjectMapper mapper;
  private final AWSAccountToScalyrMapper awsAccountToScalyrMapper;
  
  public KinesisCloudwatchLogProcessor(AWSAccountToScalyrMapper awsAccountToScalyrMapper) {
    this.scalyrService = new ScalyrService();
    this.mapper = new ObjectMapper();
    this.awsAccountToScalyrMapper = awsAccountToScalyrMapper;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize(InitializationInput initializationInput) {
    this.shardId = initializationInput.shardId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processRecords(ProcessRecordsInput processRecordsInput) {
    log.info("Start processing logs...");
    
    String lastAccount = "-1";
    String lastHost = "-1";
    String lastLogfile = "-1";
    StringBuilder buffer = new StringBuilder();
    
    for (KinesisClientRecord record : processRecordsInput.records()) {
      try {
        //TODO clean up error
        String data = new String(record.data().array(), "UTF-8");
        JsonNode jsonObject = mapper.readTree(data);
        String awsAccount = jsonObject.get("owner").asText();
        String host = jsonObject.get("logStream").asText();
        String logFile = jsonObject.get("logGroup").asText();
        
        if (!StringUtils.equals(lastAccount, awsAccount)) {
          this.flushLogs(lastAccount, lastHost, lastLogfile, buffer);
        }
        
        if (jsonObject.get("logEvents").isArray()) {
          ArrayNode arrayNode = (ArrayNode) jsonObject.get("logEvents");
          
          for (JsonNode node : arrayNode) {
            String message = node.get("message").asText();
            
            buffer.append(message);
            buffer.append("\n");
          }
        }
        
        lastAccount = awsAccount;
        lastHost = host;
        lastLogfile = logFile;

      } catch (IOException e) {
        log.error("Error parsing json", e);
      }
      
    }
    
    this.flushLogs(lastAccount, lastHost, lastLogfile, buffer);
  }

  private void flushLogs(String awsAccount, String host, String logfile, StringBuilder buffer) {
    String scalyrWriteKey = this.awsAccountToScalyrMapper.getScalyrKeyForAWSAccount(awsAccount);
    String parser = this.awsAccountToScalyrMapper.getParserForAWSAccount(awsAccount, logfile);
    
    try {
      this.scalyrService.flushLog(scalyrWriteKey, host, logfile, parser, buffer);
    } catch (ScalyrUploadException e) {
      log.error("Error uploading logs", e);
    }
    
    buffer.setLength(0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void leaseLost(LeaseLostInput leaseLostInput) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shardEnded(ShardEndedInput shardEndedInput) {
    try {
      shardEndedInput.checkpointer().checkpoint();
    } catch (KinesisClientLibDependencyException e) {
      log.error("Error with lib dependency", e);
    } catch (InvalidStateException e) {
      log.error("Invalid state", e);
    } catch (ThrottlingException e) {
      log.error("Throttling error", e);
    } catch (ShutdownException e) {
      log.error("Shutdown error", e);
    }
  }

  @Override
  public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
    try {
      shutdownRequestedInput.checkpointer().checkpoint();
    } catch (KinesisClientLibDependencyException e) {
      log.error("Error with lib dependency", e);
    } catch (InvalidStateException e) {
      log.error("Invalid state", e);
    } catch (ThrottlingException e) {
      log.error("Throttling error", e);
    } catch (ShutdownException e) {
      log.error("Shutdown error", e);
    }
  }
}
