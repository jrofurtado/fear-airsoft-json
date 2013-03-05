package com.fear_airsoft.json;

import java.util.List;
import java.util.Locale;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.google.appengine.api.memcache.*;
import com.google.gson.Gson;

public class JsonServletJogo extends HttpServlet{
  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(JsonServletJogo.class.getName());
  private static final String tempoUrl="http://free.worldweatheronline.com/feed/weather.ashx?format=json&num_of_days=5&key=795c730da3133229131502";
  private JsonClient jsonClient = new JsonClient();

  public JsonClient getJsonClient() {
    return jsonClient;
  }

  public void setJsonClient(JsonClient jsonClient) {
    this.jsonClient = jsonClient;
  }

 @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    Jogo[] jogos = getJogo();
    if(jogos!=null&&jogos.length>0){
      Jogo jogo = jogos[0];
      DecimalFormat fourDec = new DecimalFormat("0.0000", new DecimalFormatSymbols(Locale.US));
      fourDec.setGroupingUsed(false);
      String lat = fourDec.format(jogo.getCampo().getLat());
      String lng = fourDec.format(jogo.getCampo().getLng());
      jogo.setTempo(getWeather(lat, lng, printDate(jogo.ano,jogo.mes,jogo.dia)));
    }
    String result = new Gson().toJson(jogos);
    PrintWriter out = resp.getWriter();
    out.write(result);
    out.close();
  }
  
  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{ 
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods", "GET");
  }
  
  String printDate(int ano, int mes, int dia){
    return ""+ano+"-"+print2digits(mes)+"-"+print2digits(dia);
  }
  
  String print2digits(int num){
    String str = "0"+num;
    return str.substring(str.length()-2);
  }
  
  Jogo[] getJogo(){
     return parseJogo(jsonClient.executeGet(JsonServletPublishedData.publishedDataUrl+"jogo"));
  }
  
  Jogo[] parseJogo(String content){
     Gson gson = new Gson();
     return (Jogo[]) gson.fromJson(content, Jogo[].class);
  }
  
  Weather getWeather(String lat, String lng, String data){
    MemcacheService cache = prepareCacheService();
    Weather result=getWeatherFromCache(lat,lng,data,cache);
    if(result==null){
      String json=jsonClient.executeGet(tempoUrl+"&q="+lat+","+lng);
      result=parseWeatherData(json, data);
      cache.put(getWeatherCacheKey(lat, lng, data), result, Expiration.byDeltaMillis(3600000));
    }
    return result;
  }

  MemcacheService prepareCacheService(){
    MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
    cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.FINER));
    return cache;
  }
  
  String getWeatherCacheKey(String lat, String lng, String data){
    return lat+","+lng+","+data;
  }
  
  Weather getWeatherFromCache(String lat, String lng, String data, MemcacheService cache){
    String key = getWeatherCacheKey(lat, lng, data);
    return (Weather)cache.get(key);
  }
  
  Tempo parseTempo(String content){
    Gson gson = new Gson();
    return (Tempo) gson.fromJson(content, Tempo.class);
  }
  
  Weather parseWeatherData(String content, String data){
   Tempo tempo = parseTempo(content);
    Weather foundWeather=null;
    List<Weather> weatherList =  tempo.getData().getWeather();
    for (Weather weather : weatherList){
      String weatherData = weather.getDate();
      if(data.equals(weatherData)){
        foundWeather=weather;
        break;
      }
    }
    return foundWeather;
  }
}
