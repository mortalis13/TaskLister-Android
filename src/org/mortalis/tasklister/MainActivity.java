package org.mortalis.tasklister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
  
  ListView lvItems;
  boolean infoUpdated = false;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.activity_main);
    
    Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(myToolbar);
    
    Log.d("main", "onCreate");
    
    lvItems = (ListView) findViewById(R.id.lvItems);
    
    loadTaskList();
    infoUpdated = true;
    
    // String[] data = new String[] { "-abc", "-def", "-ghi", "-jkl", "-mno", "-pqr", "-stu", "-vwx", "-yz" };
    // CheckListAdapter listAdapter = new CheckListAdapter(this, data);
    // lvItems.setAdapter(listAdapter);
    
    lvItems.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        String item = (String) parent.getItemAtPosition(position);
//        item = item.trim();
        
        // CheckedTextView item = (CheckedTextView) view.findViewById(R.id.checkItem);
        // item.setChecked(!item.isChecked());
        
        CheckBox item = (CheckBox) view.findViewById(R.id.itemCheck);
        boolean state = !item.isChecked();
        
        // item.setChecked(state);
        item.toggle();
        
        setTaskChecked(position, state);
        
        // toast("List Clicked");
      }
    });
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch(id){
    case R.id.action_edit:
      editAction();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  protected void onResume() {
    Log.d("main", "onResume");
    Log.d("main", "onResume::infoUpdated: " + infoUpdated);
    
    loadTaskList();
    infoUpdated = false;
    super.onResume();
  }
  
  
// ------------------------------------------------ Actions ------------------------------------------------
  
  public void loadTaskList() {
    if(infoUpdated) return;
    
    DatabaseManager db = new DatabaseManager(this);
    String info = db.getTaskList();
    int[] selectedItems = db.getSelectedTasks();
    
    if(info != null){
      loadInfo(info, selectedItems);
    }
  }
  
  public void editAction() {
    Intent intent = new Intent(this, EditorActivity.class);
    startActivity(intent);
    overridePendingTransition(0, 0);
  }
  
  public void loadInfo(String info, int[] selectedItems) {
    String[] listData = info.split("\n");
    int listLayout = R.layout.data_list_item;
//    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, listLayout, listData);
    CheckListAdapter adapter = new CheckListAdapter(this, listLayout, listData, selectedItems);
    lvItems.setAdapter(adapter);
    
//    for(int i=0; i<selectedItems.length; ++i){
//      lvItems.getItemAtPosition(i);
//    }
  }
  
  public void setTaskChecked(int position, boolean state) {
    DatabaseManager db = new DatabaseManager(this);
    db.setTaskChecked(position, state);
  }
  
  
// ------------------------------------------------ Service ------------------------------------------------
  
  public void toast(String msg){
    int duration = Toast.LENGTH_LONG;
    Toast toast = Toast.makeText(MainActivity.this, msg, duration);
    toast.show();
  }  
  
}
