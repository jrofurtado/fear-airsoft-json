package com.fear_airsoft.json;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.DataOutputStream;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.gson.Gson;

public class JsonServletJogo extends HttpServlet{
  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(JsonServletJogo.class.getName());
  private static final String tempoUrl="http://free.worldweatheronline.com/feed/weather.ashx?format=json&num_of_days=5&key=795c730da3133229131502";
  private static final String getEventUrl = "https://www.googleapis.com/calendar/v3/calendars/jrofurtado%40fear-airsoft.com/events/"; 
  private static final String getNewCalendarReadTokenUrl = "https://accounts.google.com/o/oauth2/token";
  private static final String calendarReadRefreshToken = "1/cGqAQPMGDEqt6OZbbQojLjVZ4UX6Y6G97Y9RFNIa9B4";
  private static final String clientId = "443618782243-riilq46hvgjmkalg3c4vb13tlf16eaek.apps.googleusercontent.com";
  private static final String clientSecret = "ig0M5tLxj1eJzNTmK2uDx6aJ";
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
      MemcacheService cache = prepareCacheService();
      Weather weather=getWeather(lat, lng, printDate(jogo.ano,jogo.mes,jogo.dia), cache);
      jogo.setTempo(weather);
      jogo.setParticipantes(getParticipantes(jogo.getId(), cache));
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
     return parseJogo(jsonClient.getResponseAndClose(jsonClient.prepareCall(JsonServletPublishedData.publishedDataUrl+"/jogo"), "UTF-8"));
  }
  
  Jogo[] parseJogo(String content){
     Gson gson = new Gson();
     return (Jogo[]) gson.fromJson(content, Jogo[].class);
  }
  
  MemcacheService prepareCacheService(){
    MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
    cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.FINER));
    return cache;
  }
  
  List<Participante> getParticipantes(String eventId, MemcacheService cache){
    String token = (String)cache.get("calendarReadToken");
    HttpURLConnection connection=null;
    try{    
      if(token!=null){
        connection = jsonClient.prepareCall(getEventUrl(eventId, token));
        if(connection.getResponseCode()==401){
          logger.info("unauthorized! Token needs refresh");
          connection.disconnect();
          token = getNewCalendarReadToken(cache);
          connection = jsonClient.prepareCall(getEventUrl(eventId, token));      
        }
      }else{
        token = getNewCalendarReadToken(cache);
        connection = jsonClient.prepareCall(getEventUrl(eventId, token)); 
      }
      String result = jsonClient.getResponseAndClose(connection, null);        
      return parseParticipantes(result);
    } catch (Exception e) {
      logger.throwing(JsonServletJogo.class.getName(),"getParticipantes",e);
      return new ArrayList<Participante>();
    } finally {
      if(connection != null) {
        connection.disconnect(); 
      }
    }
  }
  
  List<Participante> parseParticipantes(String content){
    Gson gson = new Gson();      
    Event event = gson.fromJson(content, Event.class);
    if(event!=null)
      return event.getAttendees();
    else 
      return new ArrayList<Participante>();
  }   
  
  String getNewCalendarReadToken(MemcacheService cache){
    logger.info("getting new token");
    HttpURLConnection connection = jsonClient.prepareCall(getNewCalendarReadTokenUrl);
    try{    
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      String urlParameters = "client_id="+clientId+"&client_secret="+clientSecret+"&grant_type=refresh_token&refresh_token="+calendarReadRefreshToken;
      connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
      connection.setUseCaches (false);
      connection.setDoInput(true);
      connection.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.writeBytes(urlParameters);
      wr.flush();
      wr.close();
      if(connection.getResponseCode()==200){
        String result = jsonClient.getResponseAndClose(connection, null);
        Gson gson = new Gson();      
        OAuth2Token token = gson.fromJson(result, OAuth2Token.class);
        cache.put("calendarReadToken", token.getAccess_token(), Expiration.byDeltaMillis(Integer.parseInt(token.getExpires_in())*1000-100000));
        return token.getAccess_token();
      } else
        return null;
    } catch (Exception e) {
      logger.throwing(JsonServletJogo.class.getName(),"getNewCalendarReadToken",e);
      return null;
    } finally {
      if(connection != null) {
        connection.disconnect(); 
      }
    }
  }
  
  String getEventUrl(String eventId, String token){
    return getEventUrl+eventId+"?access_token="+token;
  }
  
  String getWeatherCacheKey(String lat, String lng, String data){
    return lat+","+lng+","+data;
  }
  
  Weather getWeather(String lat, String lng, String data, MemcacheService cache){
    Weather result=getWeatherFromCache(lat,lng,data,cache);
    if(result==null){
      String json=jsonClient.getResponseAndClose(jsonClient.prepareCall(tempoUrl+"&q="+lat+","+lng),null);
      result=parseWeatherData(json, data);
      cache.put(getWeatherCacheKey(lat, lng, data), result, Expiration.byDeltaMillis(3600000));
    }
    return result;
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

class Event{
   List<Participante> attendees;
   
   public List<Participante> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<Participante> attendees) {
        this.attendees = attendees;
    }
}

