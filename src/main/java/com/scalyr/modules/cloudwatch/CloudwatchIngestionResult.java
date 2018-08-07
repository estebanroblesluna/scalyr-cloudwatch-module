package com.scalyr.modules.cloudwatch;

/**
 * The result of ingesting the logs into Scalyr
 * 
 * @author Esteban Robles Luna
 */
public class CloudwatchIngestionResult {

  private long ingestedLogsCount;
  private long lastCheckedTime;
  private boolean hasMoreRecords;

  public CloudwatchIngestionResult(long ingestedLogsCount, long lastCheckedTime, boolean hasMoreRecords) {
    this.ingestedLogsCount = ingestedLogsCount;
    this.lastCheckedTime = lastCheckedTime;
    this.hasMoreRecords = hasMoreRecords;
  }

  /**
   * @return whether cloudwatch has more records to provide
   */
  public boolean hasMoreRecords() {
    return hasMoreRecords;
  }

  /**
   * @return the number of ingested log lines
   */
  public long getIngestedLogsCount() {
    return ingestedLogsCount;
  }

  /**
   * @return the last time seen in the logs
   */
  public long getLastCheckedTime() {
    return lastCheckedTime;
  }
}
