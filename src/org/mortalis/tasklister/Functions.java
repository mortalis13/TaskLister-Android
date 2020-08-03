package org.mortalis.tasklister;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;


public class Functions {
  
  static final String nl = "\n";
  
  
  public static String readFile1(String file) {
    String doc = "", line = null;
    BufferedReader br = null;

    try {
      br = new BufferedReader(new FileReader(file));               // read by lines
      while ((line = br.readLine()) != null) {
        doc += line+nl;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (br != null)
          br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return doc;
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
  
  
//  public static String readWholeFile(String path){
//    String res = "";
//    
//    try {
//      Path filePath = Paths.get(path);
//      Charset charset = Charset.forName("UTF-8");
//      
//      byte[] data = Files.readAllBytes(filePath);
//      res=new String(data, charset);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    
//    return res;
//  }
      
}
