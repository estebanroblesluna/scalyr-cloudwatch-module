package com.scalyr.modules.cloudwatch;

import java.util.HashMap;
import java.util.Map;

/**
 * An in-memory implementation of {@link AWSAccountToScalyrMapper} for testing purposes
 * 
 * @author Esteban Robles Lunax
 */
public class InMemoryAWSAccountToScalyrMapper implements AWSAccountToScalyrMapper {

  private Map<String, String> scalyrKeyMapping;
  private Map<String, String> parserMapping;
  
  public InMemoryAWSAccountToScalyrMapper() {
    this.scalyrKeyMapping = new HashMap<String, String>();
    this.parserMapping = new HashMap<String, String>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getScalyrKeyForAWSAccount(String awsAccount) {
    return this.scalyrKeyMapping.get(awsAccount);
  }

  public void setScalyrKeyMapping(String awsAccount, String scalyrKey) {
    this.scalyrKeyMapping.put(awsAccount, scalyrKey);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getParserForAWSAccount(String awsAccount, String logfile) {
    return this.parserMapping.get(awsAccount + "-" + logfile);
  }

  public void setParserMapping(String awsAccount, String logfile, String parser) {
    this.parserMapping.put(awsAccount + "-" + logfile, parser);
  }
}
