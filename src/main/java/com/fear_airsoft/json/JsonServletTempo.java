package com.fear_airsoft.json;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.logging.*;
import java.io.*;
import java.net.*;
import com.google.appengine.api.memcache.*;
import com.google.gson.Gson;

public class JsonServletTempo extends HttpServlet{
  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(JsonServletTempo.class.getName());
  private static final String tempoUrl="http://free.worldweatheronline.com/feed/weather.ashx?format=json&num_of_days=5&key=795c730da3133229131502";
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    String lat=req.getParameter("lat");
    String lng=req.getParameter("lng");
    String data=req.getParameter("data");
    String result=getTempo(lat,lng,data);
    PrintWriter out = resp.getWriter();
    out.write(result);
    out.close();
  }
  
   @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{ 
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "GET");
  }
  
  String getTempo(String lat, String lng, String data){
    String result;
    MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
    cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    String key = lat+","+lng+","+data;
    result = (String)cache.get(key);
    if(result==null){
      result=parseData(executeGet(tempoUrl+"&q="+lat+","+lng), data);
      cache.put(key, result);
    }
    return result;
  }
  
  DataJSON parse(String content){
    Gson gson = new Gson();
    return (DataJSON) gson.fromJson(content, DataJSON.class);
  }
  
  String parseData(String content, String data){
   DataJSON dataJson = parse(content);
    Weather foundWeather=null;
    for (Weather weather : dataJson.getData().getWeather()){
      if(data.equals(weather.getDate())){
        foundWeather=weather;
        break;
      }
    }
    Gson gson = new Gson();
    if(foundWeather==null)
      return gson.toJson(null);
    else
      return gson.toJson(foundWeather);
  }
  
  String executeGet(String targetURL){
    URL url;
    HttpURLConnection connection = null;  
    try {
      url = new URL(targetURL);
      connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("GET");
      connection.setUseCaches(true);
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuffer response = new StringBuffer();
      String inputLine;
      while((inputLine = in.readLine())!=null) 
        response.append(inputLine);
      in.close();
      return response.toString();
    } catch (Exception e) {
      logger.throwing(JsonServletTempo.class.getName(),"executeGet",e);
      return null;
    } finally {
      if(connection != null) {
        connection.disconnect(); 
      }
    }
  }
}