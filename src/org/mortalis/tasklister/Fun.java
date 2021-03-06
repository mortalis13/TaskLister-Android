package org.mortalis.tasklister;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.content.res.ResourcesCompat;


public class Fun {
  
  private static Context context;
  
  public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
  
  public static void setContext(Context context) {
    if (Fun.context == null) Fun.context = context;
  }
  
  public static boolean fileExists(String filePath) {
    return new File(filePath).exists();
  }
  
  public static String getParentFolder(String filePath) {
    return new File(filePath).getParentFile().getAbsolutePath();
  }
  
  public static boolean createFolder(String path) {
    boolean result = true;
    
    try {
      File f = new File(path);
      if (!f.exists()) {
        f.mkdir();
      }
    }
    catch (Exception e) {
      loge("createFolder() exception" + e);
      e.printStackTrace();
      result = false;
    }
    
    return result;
  }
  
  public static int getRandomInt(int from, int to) {
    return from + new Random().nextInt(to - from + 1);
  }
  
  
  //---------------------------------------------- Resources ----------------------------------------------
  
  public static byte[] getRawResource(int resourceId) {
    if (context == null) return null;
    InputStream is = context.getResources().openRawResource(resourceId);
    
    byte[] buf = null;
    
    try {
      buf = new byte[is.available()];
      is.read(buf);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    return buf;
  }
  
  public static int getColor(int resourceId) {
    if (context == null) return 0;
    return context.getResources().getColor(resourceId);
  }
  
  public static String getString(int resourceId) {
    if (context == null) return null;
    return context.getResources().getString(resourceId);
  }
  
  public static int getInteger(int resourceId) {
    if (context == null) return 0;
    return context.getResources().getInteger(resourceId);
  }
  
  public static int getDimension(int resourceId) {
    if (context == null) return 0;
    return (int) context.getResources().getDimension(resourceId);
  }
  
  
  //---------------------------------------------- Log ----------------------------------------------
  
  private static void log(Object value, Vars.LogLevel level) {
    String msg = null;
    if (value != null) {
      msg = value.toString();
      if (Vars.APP_LOG_LEVEL == Vars.LogLevel.VERBOSE) {
        msg += " " + getCallerLogInfo();
      }
    }
    
    try {
      if (Vars.APP_LOG_LEVEL.compareTo(level) <= 0) {
        switch (level) {
        case INFO:
          Log.i(Vars.APP_LOG_TAG, msg);
          break;
        case DEBUG:
          Log.d(Vars.APP_LOG_TAG, msg);
          break;
        case WARN:
          Log.w(Vars.APP_LOG_TAG, msg);
          break;
        case ERROR:
          Log.e(Vars.APP_LOG_TAG, msg);
          break;
        }
      }
    }
    catch (Exception e) {
      System.out.println(Vars.APP_LOG_TAG + " :: " + msg);
    }
  }
  
  
  public static void log(String format, Object... values) {
    try {
      log(String.format(format, values));
    }
    catch (Exception e) {
      loge("Fun.log(format, values) Exception, " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  public static void logd(String format, Object... values) {
    try {
      logd(String.format(format, values));
    }
    catch (Exception e) {
      loge("Fun.logd(format, values) Exception, " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  public static void loge(String format, Object... values) {
    try {
      loge(String.format(format, values));
    }
    catch (Exception e) {
      loge("Fun.loge(format, values) Exception, " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  public static void logw(String format, Object... values) {
    try {
      logw(String.format(format, values));
    }
    catch (Exception e) {
      loge("Fun.loge(format, values) Exception, " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  public static void log(Object value) {
    log(value, Vars.LogLevel.INFO);
  }
  
  public static void logd(Object value) {
    log(value, Vars.LogLevel.DEBUG);
  }
  
  public static void logw(Object value) {
    log(value, Vars.LogLevel.WARN);
  }
  
  public static void loge(Object value) {
    log(value, Vars.LogLevel.ERROR);
  }
  
  
  //---------------------------------------------- Utils ----------------------------------------------
  
  public static void printBytes(byte[] bytes) {
    log("----- Bytes:");
    for (int i = 0; i < bytes.length; i++) {
      System.out.print(Integer.toHexString((int) (bytes[i] & 0xff)) + " ");
    }
    log("\n----- End Bytes:\n");
  }
  
  public static int getInt(byte[] bytes) {
    int res;
    ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
    buffer.put(bytes);
    buffer.flip();
    res = buffer.getInt();
    
    return res;
  }
  
  private static String getCallerLogInfo() {
    String result = "";
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    
    if (stackTrace != null && stackTrace.length > 1) {
      boolean currentFound = false;
      
      int len = stackTrace.length;
      for (int i = 0; i < len; i++) {
        StackTraceElement stackElement = stackTrace[i];
        String className = stackElement.getClassName();
        
        if (className != null && className.equals(Fun.class.getName())) {
          currentFound = true;
        }
        
        if (currentFound && className != null && !className.equals(Fun.class.getName())) {
          String resultClass = stackElement.getClassName();
          String method = stackElement.getMethodName();
          int line = stackElement.getLineNumber();
          result = "[" + resultClass + ":" + method + "():" + line + "]";
          break;
        }
      }
    }
    
    return result;
  }
  
  public static String formatDate(Date date) {
    try {
      return new SimpleDateFormat(DATE_TIME_FORMAT).format(date);
    }
    catch(Exception e) {
      Fun.loge("formatDate() Exception, " + e);
      e.printStackTrace();
    }
    
    return null;
  }
  
  public static Date unformatDate(String dateStr) {
    try {
      return new SimpleDateFormat(DATE_TIME_FORMAT).parse(dateStr);
    }
    catch(Exception e) {
      Fun.loge("unformatDate() Exception, " + e);
      e.printStackTrace();
    }
    
    return null;
  }
  
  
  //---------------------------------------------- Read/Write ----------------------------------------------
  
  public static byte[] readBinFile(String name) {
    logd("Fun.readBinFile()");
    
    byte[] result = null;
    
    try {
      FileInputStream f = new FileInputStream(name);
      
      int fileSize = f.available();
      result = new byte[fileSize];
      f.read(result);
      f.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }
    
    logd("// Fun.readBinFile()");
    
    return result;
  }
  
  public static String readFile(File file) {
    try {
      FileInputStream f = new FileInputStream(file.getPath());
      
      int fileSize = f.available();
      byte[] buf = new byte[fileSize];
      f.read(buf);
      
      f.close();
      
      return new String(buf);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  //---------------------------------------------- Android Utils ----------------------------------------------
  
  public static void toast(Context context, String msg) {
    if (context == null) return;
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
  }
  
  public static void toast(String msg) {
    toast(context, msg);
  }
  
  public static void toastOnMain(String msg) {
    new Handler(Looper.getMainLooper()).post(() -> {
      toast(msg);
    });
  }
  
  public static float dpToPx(float dp) {
    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    return px;
  }
  
  public static float pxToDp(float px) {
    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    return dp;
  }
  
  public static int getScreenWidth() {
    if (context == null) return 0;
    int result = 0;
    
    try {
      DisplayMetrics displayMetrics = new DisplayMetrics();
      Activity activity = (Activity) context;
      activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
      result = displayMetrics.widthPixels;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return result;
  }
  
  public static int getScreenHeight() {
    if (context == null) return 0;
    int result = 0;
    
    try {
      DisplayMetrics displayMetrics = new DisplayMetrics();
      Activity activity = (Activity) context;
      activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
      result = displayMetrics.heightPixels;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return result;
  }
  
  public static float getTextWidth(TextView textView, String text) {
    if (context == null) return 0;
    
    Paint paint = textView.getPaint();
    float textWidth = paint.measureText(text);
    
    return textWidth;
  }
  
  public static int measureListItemHeight(Adapter adapter) {
    if (context == null) return 0;
    int result = 0;
    
    if (adapter.getCount() > 0) {
      int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
      int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
      
      ViewGroup measureParent = new FrameLayout(context);
      View itemView = adapter.getView(0, null, measureParent);
      itemView.measure(widthMeasureSpec, heightMeasureSpec);
      
      result = itemView.getMeasuredHeight();
    }
    
    return result;
  }
  
  public static int measureViewHeight(View view) {
    if (context == null) return 0;
    int result = 0;
    
    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    view.measure(widthMeasureSpec, heightMeasureSpec);
    result = view.getMeasuredHeight();
    
    return result;
  }
  
  public static int measureViewWidth(View view) {
    if (context == null) return 0;
    int result = 0;
    
    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    view.measure(widthMeasureSpec, heightMeasureSpec);
    result = view.getMeasuredWidth();
    
    return result;
  }
  
  public static int measureContentWidth(Adapter adapter) {
    if (context == null) return 0;
    int maxWidth = 0;
    
    ViewGroup measureParent = null;
    View itemView = null;
    int itemType = 0;
    
    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    
    int count = adapter.getCount();
    for (int i = 0; i < count; i++) {
      int positionType = adapter.getItemViewType(i);
      if (positionType != itemType) {
        itemType = positionType;
        itemView = null;
      }
      
      if (measureParent == null) measureParent = new FrameLayout(context);
      itemView = adapter.getView(i, itemView, measureParent);
      itemView.measure(widthMeasureSpec, heightMeasureSpec);
      
      int itemWidth = itemView.getMeasuredWidth();
      if (itemWidth > maxWidth) maxWidth = itemWidth;
    }
    
    return maxWidth;
  }
  
  public static void showKeyboard(View view) {
    log("..showKeyboard()");
    // if (context == null || view == null) return;
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
  }
  
  public static void showKeyboard(View view, int delay) {
    new Handler().postDelayed(() -> showKeyboard(view), delay);
  }
  
  public static void showKeyboard() {
    // if (context == null) return;
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
  }
  
  public static void hideKeyboard(Activity activity) {
    if (activity == null) return;
    View view = activity.findViewById(android.R.id.content);
    hideKeyboard(view);
  }

  public static void hideKeyboard(View view) {
    if (view == null) return;
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }
  
  public static void rotateScreen(Activity activity) {
    int currentOrientation = activity.getResources().getConfiguration().orientation;
    
    int newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
      newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }
    
    activity.setRequestedOrientation(newOrientation);
    
    int rotationState = Settings.System.getInt(activity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
    if (rotationState == 1) {
      activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
  }
  
  public static void changeOverScrollGlowColor(Resources res, int colorID) {
    try {
      final int glowDrawableId = res.getIdentifier("overscroll_glow", "drawable", "android");
      final Drawable overscrollGlow = ResourcesCompat.getDrawable(res, glowDrawableId, null);
      overscrollGlow.setColorFilter(res.getColor(colorID), PorterDuff.Mode.SRC_ATOP);

      final int edgeDrawableId = res.getIdentifier("overscroll_edge", "drawable", "android");
      final Drawable overscrollEdge = ResourcesCompat.getDrawable(res, edgeDrawableId, null);
      overscrollEdge.setColorFilter(res.getColor(colorID), PorterDuff.Mode.SRC_ATOP);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void dimPopupBackground(PopupWindow popup) {
    View container = popup.getContentView().getRootView();
    Context context = popup.getContentView().getContext();
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
    p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
    p.dimAmount = 0.3f;
    wm.updateViewLayout(container, p);
  }
  
  public static void toggleFullScreen(Window window) {
    WindowManager.LayoutParams attrs = window.getAttributes();
    int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
    boolean inFullScreenMode = (attrs.flags & flag) != 0;
    
    if (inFullScreenMode) {
      attrs.flags &= ~flag;
    }
    else {
      attrs.flags |= flag;
    }
    window.setAttributes(attrs);
  }
  
 
  //---------------------------------------------- === App Utils === ----------------------------------------------
  
}
