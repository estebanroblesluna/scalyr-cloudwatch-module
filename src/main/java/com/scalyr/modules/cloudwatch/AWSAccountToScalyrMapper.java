package com.scalyr.modules.cloudwatch;

/**
 * An AWSAccountToScalyrMapper maps between AWS account information 
 * into Scalyr private information
 * 
 * @author Esteban Robles Luna
 */
public interface AWSAccountToScalyrMapper {

  /**
   * Returns the Scalyr access key for the awsAccount or null if not found or invalid
   * 
   * @param awsAccount the aws account
   * @return the access key if a valid user or null
   */
  String getScalyrKeyForAWSAccount(String awsAccount);
  
  /**
   * Returns the parser to be used for the aws account and log file
   * 
   * @param awsAccount the aws account
   * @param logfile the log file
   * @return the parser to be used
   */
  String getParserForAWSAccount(String awsAccount, String logfile);
}
