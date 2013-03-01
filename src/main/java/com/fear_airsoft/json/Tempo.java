package com.fear_airsoft.json;

import java.util.List;

public class Tempo {
    private Info data;

    public Info getData() {
        return data;
    }

    public void setData(Info data) {
        this.data = data;
    }
}

class Info {
    private List<Weather> weather;

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}

class Weather {
    private String date;
    private String precipMM;
    private String tempMaxC;
    private String tempMinC;
    private List<WeatherIcon> weatherIconUrl;
    private String windspeedKmph;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrecipMM() {
        return precipMM;
    }

    public void setPrecipMM(String precipMM) {
        this.precipMM = precipMM;
    }

    public String getTempMaxC() {
        return tempMaxC;
    }

    public void setTempMaxC(String tempMaxC) {
        this.tempMaxC = tempMaxC;
    }

    public String getTempMinC() {
        return tempMinC;
    }

    public void setTempMinC(String tempMinC) {
        this.tempMinC = tempMinC;
    }

    public List<WeatherIcon> getWeatherIconUrl() {
        return weatherIconUrl;
    }

    public void setWeatherIconUrl(List<WeatherIcon> weatherIconUrl) {
        this.weatherIconUrl = weatherIconUrl;
    }

    public String getWindspeedKmph() {
        return windspeedKmph;
    }

    public void setWindspeedKmph(String windspeedKmph) {
        this.windspeedKmph = windspeedKmph;
    }
}

class WeatherIcon {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}