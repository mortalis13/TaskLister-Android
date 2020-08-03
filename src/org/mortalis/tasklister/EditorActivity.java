package org.mortalis.tasklister;

import java.io.File;
import org.mortalis.tasklister.FileChooser.FileSelectedListener;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;


public class EditorActivity extends AppCompatActivity {
  
  EditText etEditorText;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_editor);
    
    Log.d("editor", "onCreate");
    
    Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_child_toolbar);
    setSupportActionBar(myChildToolbar);
    
    ActionBar ab = getSupportActionBar();
    ab.setDisplayHomeAsUpEnabled(true);
    ab.setHomeButtonEnabled(true);
    
    etEditorText = (EditText) findViewById(R.id.editor_text);
    
    loadTaskList();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.editor, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    
    switch(id){
    case R.id.action_clear:
      clearAction();
      return true;
    case R.id.action_save:
      saveAction();
      return true;
    case R.id.action_import_text:
      importFileAction();
      return true;
    case android.R.id.home:
      onBackPressed();
      return true;
    }
    
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(0, 0);
  }
  
  
// ------------------------------------------------ Actions ------------------------------------------------
  
  public void loadTaskList() {
    DatabaseManager db = new DatabaseManager(this);
    String info = db.getTaskList();
    if(info != null){
      etEditorText.setText(info);
    }
  }
    
  public void clearAction() {
    etEditorText.setText("");
  }
    
  public void saveAction() {
    String info = etEditorText.getText().toString();
    DatabaseManager db = new DatabaseManager(this);
    db.updateTaskList(info);
    finish();
  }


// ------------------------------------------------ Service ------------------------------------------------

  public void importFileAction() {
    new FileChooser(this).setFileListener( new FileSelectedListener() {
      @Override
      public void fileSelected(File file) {
        String text = Functions.readFile(file);
        if(text != null)
          etEditorText.setText(text);
        
        Log.d("editor", "finishReadFile");
      }
    }).showDialog();
  }
    
}
