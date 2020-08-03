package org.mortalis.tasklister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


public class CheckListAdapter extends ArrayAdapter<String> {
  
    private LayoutInflater inflater;
    private int viewResourceId;
    private int[] selectedItems = null;
    
    // private String[] items;
    // private Drawable icon;


    public CheckListAdapter(Context context, String[] items) {
        // super(context, viewResourceId, items);
        super(context, R.layout.data_list_item, R.id.itemText, items);
        inflater = LayoutInflater.from(context);
        
        // inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // this.items = items;
        // this.viewResourceId = viewResourceId;
    }
    
    public CheckListAdapter(Context context, int viewResourceId, String[] items, int[] selectedItems) {
        super(context, R.layout.data_list_item, R.id.itemText, items);
        inflater = LayoutInflater.from(context);
        
        this.selectedItems = selectedItems;
        
        // inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      convertView = inflater.inflate(R.layout.data_list_item, null);
      
      CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.itemCheck);
      if(selectedItems != null && selectedItems[position] == 1)
        checkBox.setChecked(true);
      
      TextView textView = (TextView) convertView.findViewById(R.id.itemText);
      textView.setText(this.getItem(position));
      
      return convertView;
    }
    
}
