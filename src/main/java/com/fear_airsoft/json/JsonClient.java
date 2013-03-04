package com.fear_airsoft.json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import java.nio.charset.Charset;

public class JsonClient{
 private static final Logger logger = Logger.getLogger(JsonClient.class.getName());
 
 String executeGet(String targetURL){
    URL url;
    HttpURLConnection connection = null;  
    try {
      url = new URL(targetURL);
      connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("GET");
      connection.setUseCaches(true);
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
      StringBuffer response = new StringBuffer();
      String inputLine;
      while((inputLine = in.readLine())!=null) 
        response.append(inputLine);
      in.close();
      return response.toString();
    } catch (Exception e) {
      logger.throwing(JsonClient.class.getName(),"executeGet",e);
      return null;
    } finally {
      if(connection != null) {
        connection.disconnect(); 
      }
    }
  }
}