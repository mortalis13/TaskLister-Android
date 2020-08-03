package org.mortalis.tasklister;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;


public class DatabaseManager extends SQLiteOpenHelper {
  
    public static final String TABLE_NAME = "tasks";
    public static final String ID_FIELD = "_id";
    
    public static final String TASK_LIST_FIELD = "task_list";
    public static final String SELECTED_ITEMS_FIELD = "selected_items";
    
    
    public DatabaseManager(Context context) {
      super(context, TABLE_NAME, null, 1);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("db", "onCreate");
        String sql = "CREATE TABLE " + TABLE_NAME
                + " (" + ID_FIELD + " INTEGER, "
                + TASK_LIST_FIELD + " TEXT,"
                + SELECTED_ITEMS_FIELD + " TEXT,"
                + " PRIMARY KEY (" + ID_FIELD + "));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        Log.d("db", "onUpdate");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void updateTaskList(String info) {
        Log.d("db", "updateTaskList"); 
        
        String selectedItems = "";
        String[] listData = info.split("\n");
        
        String[] selectedItemsList = new String[listData.length];
        for(int i=0; i<selectedItemsList.length; ++i){
          selectedItemsList[i] = "0";
        }
        selectedItems = TextUtils.join(",", selectedItemsList);
        
        Log.d("editor", "selectedItems-Save: " + selectedItems);
        
        
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_FIELD, 0);
        values.put(TASK_LIST_FIELD, info);
        values.put(SELECTED_ITEMS_FIELD, selectedItems);
        
        int id = (int) db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d("db", "insertWithOnConflict-res: " + id);
        
        db.close();
    }
    
    public String getTaskList() {
        Log.d("db", "getTaskList");
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
          Log.d("db", "cursor-ok");
          String info = cursor.getString(1);
          return info;
        }
        Log.d("db", "cursor-null");
        return null;
    }
    
    public void setTaskChecked(int position, boolean state) {
        Log.d("db", "setTaskChecked");
        
        String selectedItems = "";
        SQLiteDatabase db = null;
        
        db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
          selectedItems = cursor.getString(2);
        }
        
        String[] selectedItemsList = selectedItems.split(",");
        selectedItemsList[position] = state?"1":"0";
        selectedItems = TextUtils.join(",", selectedItemsList);
        
        Log.d("editor", "state-Set: " + state);
        Log.d("editor", "selectedItems-Set: " + selectedItems);
        
        
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SELECTED_ITEMS_FIELD, selectedItems);
        int res = db.update(TABLE_NAME, values, ID_FIELD + "=0", null);
        Log.d("db", "update-res: " + res);
    }
    
    public int[] getSelectedTasks() {
        Log.d("db", "getSelectedTasks");
        
        String selectedItems = "";
        SQLiteDatabase db = null;
        
        db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
          selectedItems = cursor.getString(2);
        }
        
        String[] selectedItemsList = selectedItems.split(",");
        int listLen = selectedItemsList.length;
        int[] res = new int[listLen];
        
        for(int i=0; i<listLen; ++i){
          res[i] = selectedItemsList[i].equals("0")?0:1;
        }
        
        return res;
    }
    
}