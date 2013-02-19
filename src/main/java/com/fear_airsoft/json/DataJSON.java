package com.fear_airsoft.json;
import java.util.List;

public class DataJSON{
  private Info data;
  
  public Info getData(){
    return data;
  }
  public void setData(Info data){
    this.data=data;
  }
}

class Info{
  private List<Weather> weather;
  
  public List<Weather> getWeather(){
    return weather;
  }
  public void setWeather(List<Weather> weather){
    this.weather=weather;
  }
}

class Weather{
  private String date;
  precipMMM;
  tempMaxC;
  tempMinC;
  weatherDesc[value];
  weatherIconUrl[value];
  windspeedKmph;
  
  public String getDate(){
    return date;
  }
  public void setDate(String date){
    this.date=date;
  }
}