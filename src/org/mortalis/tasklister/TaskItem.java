package org.mortalis.tasklister;

import java.util.Date;

public class TaskItem {
  
  int id;
  String text;
  Date date;
  boolean checked;
  boolean archived;
  boolean editMode;
  
  public TaskItem() {}
  public TaskItem(int id, String text, boolean checked) {
    this.id = id;
    this.text = text;
    this.checked = checked;
  }

}
