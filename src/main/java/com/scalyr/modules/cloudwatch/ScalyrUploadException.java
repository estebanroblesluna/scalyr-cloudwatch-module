package com.scalyr.modules.cloudwatch;

/**
 * Exception thrown when there was a problem uploading logs into Scalyr
 * 
 * @author Esteban Robles Luna
 */
public class ScalyrUploadException extends Exception {

  private static final long serialVersionUID = 1L;

  public ScalyrUploadException(String message) {
    super(message);
  }

  public ScalyrUploadException(Exception e) {
    super(e);
  }
}