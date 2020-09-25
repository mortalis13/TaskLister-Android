package org.mortalis.tasklister;

import java.util.ArrayList;
import java.util.List;

import org.home.file_chooser_lib.FilePickerDialog;

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
import android.widget.RelativeLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
  
  private Context context;
  
  private ListView itemsListView;
  private LinearLayout focusCatch;
  private ImageButton btnAddTask;
  private ImageButton btnImportText;
  private EditText taskText;
  
  private TaskListAdapter listAdapter;
  private List<TaskItem> listItems;
  private FilePickerDialog filePickerDialog;
  
  private boolean infoUpdated;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.activity_main);
    
    context = this;
    Fun.setContext(context);
    DatabaseManager.init(context);
    
    init();
    
    itemsListView = findViewById(R.id.itemsListView);
    btnAddTask = findViewById(R.id.btnAddTask);
    btnImportText = findViewById(R.id.btnImportText);
    taskText = findViewById(R.id.taskText);
    focusCatch = findViewById(R.id.focusCatch);
    
    int listLayout = R.layout.task_list_item;
    listItems = new ArrayList<>();
    listAdapter = new TaskListAdapter(this, listLayout, listItems);
    itemsListView.setAdapter(listAdapter);
    
    btnAddTask.setOnClickListener(v -> {
      addItem();
    });
    
    btnImportText.setOnClickListener(v -> {
      importFileAction();
    });
    
    taskText.setOnEditorActionListener((v, actionId, event) -> {
      addItem();
      return true;
    });
    
    loadTaskList();
    infoUpdated = true;
  }


  @Override
  protected void onResume() {
    Fun.logd("onResume");
    Fun.logd("onResume::infoUpdated: " + infoUpdated);
    
    loadTaskList();
    infoUpdated = false;
    super.onResume();
  }
  
  
// ------------------------------------------------ Actions ------------------------------------------------
  
  private void init() {
    filePickerDialog = new FilePickerDialog(context);
    filePickerDialog.setExtensionFilter("txt");
    
    Fun.changeOverScrollGlowColor(getResources(), R.color.toolbar_background);
  }
  
  public void loadTaskList() {
    // if (infoUpdated) return;
    List<TaskItem> items = DatabaseManager.getTasks(false);
    listAdapter.updateItems(items);
    listAdapter.update();
  }
  
  public void addItem() {
    Fun.logd("addItem()");
    String text = taskText.getText().toString();
    if (text.length() == 0) return;
    
    listAdapter.addItem(text);
    taskText.setText("");
    // focusCatch.requestFocus();
  }
  
  public void importFileAction() {
    filePickerDialog.setFileSelectedListener(file -> {
      String text = Fun.readFile(file);
      if (text == null) return;
      
      String[] items = text.split("\n");
      DatabaseManager.removeAllTasks();
      
      for (String item: items) {
        TaskItem task = new TaskItem();
        task.text = item;
        DatabaseManager.createTask(task);
        loadTaskList();
      }
    }).showDialog();
  }
  
  
  // ----------------------------------- Classes ----------------------
  
  private class TaskListAdapter extends ArrayAdapter<TaskItem> {
    private TaskListAdapter taskAdapter;
    private int itemLayoutId;
    
    private List<TaskItem> items;
    private int[] selectedItems;

    public TaskListAdapter(Context context, int itemLayoutId, List<TaskItem> items) {
      super(context, itemLayoutId, items);
      this.itemLayoutId = itemLayoutId;
      this.items = items;
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
      
      holder.itemRow.setOnClickListener(v -> {
        holder.itemCheck.setChecked(!item.checked);
      });
      
      holder.itemCheck.setOnCheckedChangeListener((v, isChecked) -> {
        item.checked = isChecked;
        DatabaseManager.updateTask(item);
      });
      
      holder.btnEditItem.setOnClickListener(v -> {
        item.editMode = true;
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
        DatabaseManager.removeTask(item.id);
      });
      
      holder.btnEditConfirm.setOnClickListener(v -> {
        String newText = holder.itemEditText.getText().toString();
        if (!newText.equals(item.text)) {
          holder.itemText.setText(newText);
          item.text = newText;
        }
        
        if (item.id == -1) {
          DatabaseManager.createTask(item);
          if (item.id == -1) Fun.loge("ERROR: Created task not found");
        }
        else {
          DatabaseManager.updateTask(item);
        }
        holder.btnEditCancel.performClick();
        loadTaskList();
      });
      
      holder.btnEditCancel.setOnClickListener(v -> {
        Fun.hideKeyboard(holder.itemEditText);
        loadDefaultView(holder);
        item.editMode = false;
        
        if (item.id == -1) {
          removeItem(position);
        }
      });
      
      
      // -- Load Data
      holder.itemText.setText(item.text);
      holder.itemEditText.setText(item.text);
      holder.itemCheck.setChecked(item.checked);
      
      if (item.editMode) {
        loadEditView(holder);
        new Handler().post(() -> {
          holder.itemEditText.requestFocus();
          holder.itemEditText.selectAll();
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
    
    public void addItem(String text) {
      TaskItem item = new TaskItem(-1, "", false);
      item.text = text;
      
      DatabaseManager.createTask(item);
      if (item.id == -1) Fun.loge("ERROR: Created task not found");
      loadTaskList();
      // notifyDataSetChanged();
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
    
    
    public void updateItems(List<TaskItem> items) {
      this.items.clear();
      for (TaskItem item: items) {
        this.items.add(item);
      }
    }
    
    public void update() {
      notifyDataSetChanged();
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
