package org.mortalis.tasklister;

import android.os.Environment;

public class Vars {
  
  public static final String PACKAGE_NAME = "org.mortalis.tasklister";
  
  public enum LogLevel {VERBOSE, DEBUG, INFO, WARN, ERROR};
  public static final LogLevel APP_LOG_LEVEL = LogLevel.DEBUG;
  
  public static final String APP_LOG_TAG = "tasklister";
  
  public static final int APP_PERMISSION_REQUEST_ACCESS_EXTERNAL_STORAGE = 101;
  
  // ---
  
}
