package org.mortalis.tasklister;

import java.util.ArrayList;
import java.util.List;

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
import android.view.ViewGroup;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.os.Handler;
import android.widget.LinearLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
  
  private Context context;
  
  private ListView lvItems;
  private LinearLayout focusCatch;
  
  private TaskListAdapter listAdapter;
  
  private boolean infoUpdated;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.activity_main);
    
    context = this;
    Fun.setContext(context);
    
    Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(myToolbar);
    
    Log.d("main", "onCreate");
    
    lvItems = (ListView) findViewById(R.id.lvItems);
    focusCatch = findViewById(R.id.focusCatch);
    
    loadTaskList();
    infoUpdated = true;
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
    case R.id.action_new:
      addItem();
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
  
  public void addItem() {
    Fun.logd("addItem()");
    listAdapter.addItem();
  }
  
  public void loadInfo(String info, int[] selectedItems) {
    String[] items = info.split("\n");
    List<TaskItem> listData = new ArrayList<>();
    for (int i = 0; i < items.length; i++) {
      listData.add(new TaskItem(i, items[i], false));
    }
    
    // int listLayout = R.layout.data_list_item;
    int listLayout = R.layout.task_list_item;
    listAdapter = new TaskListAdapter(this, listLayout, listData, selectedItems);
    lvItems.setAdapter(listAdapter);
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
  
  
  // ----------------------------------- Classes ----------------------
  
  private class TaskItem {
    int id;
    String text;
    boolean checked;
    boolean editMode;
    
    public TaskItem(int id, String text, boolean checked) {
      this.id = id;
      this.text = text;
      this.checked = checked;
    }
  }
  
  private class TaskListAdapter extends ArrayAdapter<TaskItem> {
    private TaskListAdapter taskAdapter;
    private LayoutInflater inflater;
    private int itemLayoutId;
    
    private List<TaskItem> items;
    private int[] selectedItems;

    public TaskListAdapter(Context context, int itemLayoutId, List<TaskItem> items) {
      super(context, itemLayoutId, items);
      inflater = LayoutInflater.from(context);
      this.items = items;
    }
    
    public TaskListAdapter(Context context, int itemLayoutId, List<TaskItem> items, int[] selectedItems) {
      this(context, itemLayoutId, items);
      this.itemLayoutId = itemLayoutId;
      this.selectedItems = selectedItems;
      // inflater = LayoutInflater.from(context);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      final ItemHolder holder;
      final TaskItem item = getItem(position);
      
      if (convertView == null) {
        holder = new ItemHolder();
        
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(itemLayoutId, parent, false);
        
        holder.itemRow = convertView.findViewById(R.id.itemRow);
        holder.itemCheck = convertView.findViewById(R.id.itemCheck);
        holder.itemText = convertView.findViewById(R.id.itemText);
        holder.itemEditText = convertView.findViewById(R.id.itemEditText);
        holder.btnEditConfirm = convertView.findViewById(R.id.btnEditConfirm);
        holder.btnEditCancel = convertView.findViewById(R.id.btnEditCancel);
        holder.btnEditItem = convertView.findViewById(R.id.btnEditItem);
        holder.btnDeleteItem = convertView.findViewById(R.id.btnDeleteItem);
        
        convertView.setTag(holder);
      }
      else {
        holder = (ItemHolder) convertView.getTag();
      }
      
      // -- Actions
      final View itemView = convertView;
      
      holder.btnEditItem.setOnClickListener(v -> {
        item.editMode = true;
        // notifyDataSetChanged();
        loadEditView(holder);
        // holder.itemEditText.setText(item.text);
        
        // focusCatch.requestFocus();
        // Fun.showKeyboard(focusCatch);
        // Fun.showKeyboard(holder.itemEditText);
        // Fun.showKeyboard(focusCatch, 100);
        
        holder.itemEditText.requestFocus();
        Fun.showKeyboard(holder.itemEditText);
      });
      
      holder.btnDeleteItem.setOnClickListener(v -> {
        removeItem(position);
      });
      
      holder.btnEditConfirm.setOnClickListener(v -> {
        String newText = holder.itemEditText.getText().toString();
        if (!newText.equals(item.text)) {
          holder.itemText.setText(newText);
          item.text = newText;
        }
        
        item.id = items.size();
        holder.btnEditCancel.performClick();
      });
      
      holder.btnEditCancel.setOnClickListener(v -> {
        Fun.hideKeyboard(holder.itemEditText);
        loadDefaultView(holder);
        item.editMode = false;
        
        if (item.id == -1) {
          removeItem(position);
        }
      });
      
      
      holder.itemRow.setOnClickListener(v -> {
        item.checked = !item.checked;
        holder.itemCheck.setChecked(item.checked);
      });
      
      
      // -- Load Data
      holder.itemText.setText(item.text);
      holder.itemEditText.setText(item.text);
      // holder.itemEditText.setClickable(false);
      holder.itemCheck.setChecked(item.checked);
      
      if (item.editMode) {
        loadEditView(holder);
        new Handler().post(() -> {
          holder.itemEditText.requestFocus();
          holder.itemEditText.selectAll();
          
          // focusCatch.requestFocus();
          // Fun.showKeyboard(focusCatch, 100);
          // Fun.showKeyboard(holder.itemEditText, 100);
        });
        
        if (item.id == -1) {
          holder.itemEditText.requestFocus();
          Fun.showKeyboard(holder.itemEditText);
        }
      }
      else {
        loadDefaultView(holder);
      }
      
      return convertView;
    }
    
    private void loadEditView(ItemHolder holder) {
      holder.itemRow.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
      holder.itemText.setVisibility(View.GONE);
      holder.itemEditText.setVisibility(View.VISIBLE);
      
      holder.btnEditItem.setVisibility(View.GONE);
      holder.btnDeleteItem.setVisibility(View.GONE);
      holder.btnEditConfirm.setVisibility(View.VISIBLE);
      holder.btnEditCancel.setVisibility(View.VISIBLE);
    }
    
    private void loadDefaultView(ItemHolder holder) {
      holder.itemRow.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
      holder.itemEditText.setVisibility(View.GONE);
      holder.itemText.setVisibility(View.VISIBLE);
      
      holder.btnEditItem.setVisibility(View.VISIBLE);
      holder.btnDeleteItem.setVisibility(View.VISIBLE);
      holder.btnEditConfirm.setVisibility(View.GONE);
      holder.btnEditCancel.setVisibility(View.GONE);
    }
    
    public void addItem() {
      TaskItem item = new TaskItem(-1, "", false);
      item.editMode = true;
      items.add(0, item);
      notifyDataSetChanged();
    }
    
    public void removeItem(int pos) {
      try {
        items.remove(pos);
        notifyDataSetChanged();
      }
      catch (Exception e) {
        e.printStackTrace();
        Fun.toast(context, "Error removing preset");
      }
    }
    
    
    private class ItemHolder {
      LinearLayout itemRow;
      CheckBox itemCheck;
      TextView itemText;
      EditText itemEditText;
      ImageButton btnEditConfirm;
      ImageButton btnEditCancel;
      ImageButton btnEditItem;
      ImageButton btnDeleteItem;
    }
  }
  
}
