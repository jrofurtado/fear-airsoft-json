package com.fear_airsoft.json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import java.nio.charset.Charset;

public class JsonClient{
  private static final Logger logger = Logger.getLogger(JsonClient.class.getName());
  
  HttpURLConnection prepareCall(String targetURL){
    URL url;
    HttpURLConnection connection = null;  
    try {
      url = new URL(targetURL);
      connection = (HttpURLConnection)url.openConnection();
      return connection;      
    } catch (Exception e) {
      logger.throwing(JsonClient.class.getName(),"executeGet",e);
      return null;
    }
  }
  
  String getResponseAndClose(HttpURLConnection connection, String encoding){
    try {
      if(connection.getResponseCode()==200 || connection.getResponseCode()==304){
        InputStreamReader inputStreamReader;
        if(encoding==null)
          inputStreamReader=new InputStreamReader(connection.getInputStream());
        else
          inputStreamReader=new InputStreamReader(connection.getInputStream(), Charset.forName(encoding));
        BufferedReader in = new BufferedReader(inputStreamReader);
        StringBuffer response = new StringBuffer();
        String inputLine;
        while((inputLine = in.readLine())!=null) 
          response.append(inputLine);
        in.close();
        return response.toString();
      }else{
        return null;
      }
    } catch (Exception e) {
      logger.throwing(JsonClient.class.getName(),"getResponseAsString",e);
      return null;
    } finally {
      if(connection != null) {
        connection.disconnect(); 
      }
    }
  }
}