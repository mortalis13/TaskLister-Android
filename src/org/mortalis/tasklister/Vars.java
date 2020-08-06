package org.mortalis.tasklister;

import android.os.Environment;

public class Vars {
  
  public static final String PACKAGE_NAME = "org.mortalis.tasklister";
  
  public enum LogLevel {VERBOSE, DEBUG, INFO, WARN, ERROR};
  public static final LogLevel APP_LOG_LEVEL = LogLevel.DEBUG;
  
  public static final String APP_LOG_TAG = "tasklister";
  
  // ---
  
}
