package org.mortalis.tasklister;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


@SuppressWarnings("unused")
public class DatabaseManager {
  
  private static SQLiteDatabase db;
  private static DBHelper dbHelper;
  

  public static void init(Context context) {
    if (db == null) {
      dbHelper = new DBHelper(context);
      db = dbHelper.getWritableDatabase();
    }
  }
  
  public static void release() {
    if (dbHelper != null) {
      dbHelper.close();
    }
    db = null;
  }

  
  public static void createTask(TaskItem item) {
    Fun.logd("DatabaseManager.createTask()");
    
    Cursor cursor = null;
    
    try {
      String dateStr = Fun.formatDate(new Date());
      
      ContentValues contentValues = new ContentValues();
      contentValues.put(DBHelper.TASK_COL_TEXT, item.text);
      contentValues.put(DBHelper.TASK_COL_DATE, dateStr);
      contentValues.put(DBHelper.TASK_COL_CHECKED, item.checked);
      contentValues.put(DBHelper.TASK_COL_ARCHIVED, item.archived);
      long res = db.insert(DBHelper.TASK_TABLE_NAME, null, contentValues);
      
      // -- get id
      String sql = "SELECT " + DBHelper.TASK_COL_ID + " FROM " + DBHelper.TASK_TABLE_NAME + 
        " ORDER BY " + DBHelper.TASK_COL_ID + " DESC";
      cursor = db.rawQuery(sql, null);
      
      int count = cursor.getCount();
      if (count > 0) {
        cursor.moveToFirst();
        item.id = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_COL_ID));
      }
    }
    catch (Exception e) {
      Fun.loge("createTask() Exception, " + e);
      e.printStackTrace();
    }
    finally {
      if (cursor != null) cursor.close();
    }
  }
  
  public static TaskItem getTask(int id) {
    Fun.logd("DatabaseManager.getTask()");
    
    TaskItem result = new TaskItem();
    Cursor cursor = null;
    
    try {
      String sql = "SELECT * FROM " + DBHelper.TASK_TABLE_NAME + 
        " WHERE " + DBHelper.TASK_COL_ID + "=?;";
      String[] selectArgs = new String[] {String.valueOf(id)};
      cursor = db.rawQuery(sql, selectArgs);
      
      int count = cursor.getCount();
      if (count > 0) {
        cursor.moveToFirst();
        result.id = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_COL_ID));
        result.text = cursor.getString(cursor.getColumnIndex(DBHelper.TASK_COL_TEXT));
        result.date = Fun.unformatDate(cursor.getString(cursor.getColumnIndex(DBHelper.TASK_COL_DATE)));
        result.checked = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_COL_CHECKED)) == 1;
        result.archived = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_COL_ARCHIVED)) == 1;
      }
    }
    catch (Exception e) {
      Fun.loge("getTask() Exception, " + e);
      e.printStackTrace();
    }
    finally {
      if (cursor != null) cursor.close();
    }
    
    return result;
  }
  
  public static List<TaskItem> getTasks(boolean reverse) {
    Fun.logd("DatabaseManager.getTask()");
    
    List<TaskItem> result = new ArrayList<>();
    Cursor cursor = null;
    
    try {
      String sql = "SELECT * FROM " + DBHelper.TASK_TABLE_NAME + 
        " WHERE " + DBHelper.TASK_COL_ARCHIVED + "=0" +
        " ORDER BY " + DBHelper.TASK_COL_ID;
      if (reverse) sql += " DESC";
      cursor = db.rawQuery(sql, null);
      
      for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
        TaskItem item = new TaskItem();
        item.id = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_COL_ID));
        item.text = cursor.getString(cursor.getColumnIndex(DBHelper.TASK_COL_TEXT));
        item.date = Fun.unformatDate(cursor.getString(cursor.getColumnIndex(DBHelper.TASK_COL_DATE)));
        item.checked = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_COL_CHECKED)) == 1;
        item.archived = cursor.getInt(cursor.getColumnIndex(DBHelper.TASK_COL_ARCHIVED)) == 1;
        result.add(item);
      }
    }
    catch (Exception e) {
      Fun.loge("getTask() Exception, " + e);
      e.printStackTrace();
    }
    finally {
      if (cursor != null) cursor.close();
    }
    
    return result;
  }
  
  public static void updateTask(TaskItem item) {
    Fun.logd("DatabaseManager.updateTask()");
    
    try {
      if (item.id == -1) {
        Fun.loge("updateTask() Error, item.id = -1");
        return;
      }
      
      ContentValues contentValues = new ContentValues();
      contentValues.put(DBHelper.TASK_COL_TEXT, item.text);
      contentValues.put(DBHelper.TASK_COL_CHECKED, item.checked);
      contentValues.put(DBHelper.TASK_COL_ARCHIVED, item.archived);
      
      String where = DBHelper.TASK_COL_ID + "=?";
      String[] args = new String[] {String.valueOf(item.id)};
      int res = db.update(DBHelper.TASK_TABLE_NAME, contentValues, where, args);
    }
    catch (Exception e) {
      Fun.loge("updateTask() Exception, " + e);
      e.printStackTrace();
    }
  }
  
  public static void removeTask(int id) {
    Fun.logd("DatabaseManager.removeTask()");
    
    try {
      if (id == -1) {
        Fun.loge("removeTask() Error, item.id = -1");
        return;
      }
      
      String where = DBHelper.TASK_COL_ID + "=?";
      String[] args = new String[] {String.valueOf(id)};
      int res = db.delete(DBHelper.TASK_TABLE_NAME, where, args);
    }
    catch (Exception e) {
      Fun.loge("removeTask() Exception, " + e);
      e.printStackTrace();
    }
  }
  
  public static void removeAllTasks() {
    Fun.logd("DatabaseManager.removeAllTasks()");
    
    try {
      int res = db.delete(DBHelper.TASK_TABLE_NAME, null, null);
    }
    catch (Exception e) {
      Fun.loge("removeAllTasks() Exception, " + e);
      e.printStackTrace();
    }
  }
  
  
  // ------------------------------------- -------------- -------------------------------------
  // ------------------------------------- DBHelper       -------------------------------------
  // ------------------------------------- -------------- -------------------------------------
  
  public static class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "tasklister.db";
    public static final int DATABASE_VERSION = 1;
    
    public static final String TASK_TABLE_NAME = "task";
    
    public static final String TASK_COL_ID = "_id";
    public static final String TASK_COL_TEXT = "text";
    public static final String TASK_COL_DATE = "date";
    public static final String TASK_COL_CHECKED = "checked";
    public static final String TASK_COL_ARCHIVED = "archived";
    
    
    public DBHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
      Fun.logd("onCreate");
      String sql = "CREATE TABLE " + TASK_TABLE_NAME + " (" +
        TASK_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        TASK_COL_TEXT + " TEXT, " +
        TASK_COL_DATE + " TEXT, " +
        TASK_COL_CHECKED + " INTEGER NOT NULL DEFAULT 0, " +
        TASK_COL_ARCHIVED + " INTEGER NOT NULL DEFAULT 0" +
      ");";
      db.execSQL(sql);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Fun.logw("Upgrading database from version " + oldVersion + " to " + newVersion);
    }
  }
  
}
