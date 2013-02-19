package com.fear_airsoft.json;

import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;

public class JsonServletTempoTest extends TestCase {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalMemcacheServiceTestConfig());
    static final String jsonRest = "{ \"data\": { \"current_condition\": [ {\"cloudcover\": \"50\", \"humidity\": \"94\", \"observation_time\": \"10:14 AM\", \"precipMM\": \"0.0\", \"pressure\": \"1013\", \"temp_C\": \"16\", \"temp_F\": \"61\", \"visibility\": \"10\", \"weatherCode\": \"116\",  \"weatherDesc\": [ {\"value\": \"Partly Cloudy\" } ],  \"weatherIconUrl\": [ {\"value\": \"http:\\/\\/www.worldweatheronline.com\\/images\\/wsymbols01_png_64\\/wsymbol_0002_sunny_intervals.png\" } ], \"winddir16Point\": \"W\", \"winddirDegree\": \"270\", \"windspeedKmph\": \"30\", \"windspeedMiles\": \"19\" } ],  \"request\": [ {\"query\": \"Lat 37.77 and Lon -25.58\", \"type\": \"LatLon\" } ],  \"weather\": [ {\"date\": \"2013-02-19\", \"precipMM\": \"0.3\", \"tempMaxC\": \"18\", \"tempMaxF\": \"64\", \"tempMinC\": \"16\", \"tempMinF\": \"60\", \"weatherCode\": \"119\",  \"weatherDesc\": [ {\"value\": \"Cloudy\" } ],  \"weatherIconUrl\": [ {\"value\": \"http:\\/\\/www.worldweatheronline.com\\/images\\/wsymbols01_png_64\\/wsymbol_0003_white_cloud.png\" } ], \"winddir16Point\": \"WSW\", \"winddirDegree\": \"257\", \"winddirection\": \"WSW\", \"windspeedKmph\": \"40\", \"windspeedMiles\": \"25\" }, {\"date\": \"2013-02-20\", \"precipMM\": \"5.7\", \"tempMaxC\": \"18\", \"tempMaxF\": \"65\", \"tempMinC\": \"15\", \"tempMinF\": \"58\", \"weatherCode\": \"113\",  \"weatherDesc\": [ {\"value\": \"Sunny\" } ],  \"weatherIconUrl\": [ {\"value\": \"http:\\/\\/www.worldweatheronline.com\\/images\\/wsymbols01_png_64\\/wsymbol_0001_sunny.png\" } ], \"winddir16Point\": \"WSW\", \"winddirDegree\": \"259\", \"winddirection\": \"WSW\", \"windspeedKmph\": \"66\", \"windspeedMiles\": \"41\" }, {\"date\": \"2013-02-21\", \"precipMM\": \"4.8\", \"tempMaxC\": \"16\", \"tempMaxF\": \"60\", \"tempMinC\": \"14\", \"tempMinF\": \"58\", \"weatherCode\": \"353\",  \"weatherDesc\": [ {\"value\": \"Light rain shower\" } ],  \"weatherIconUrl\": [ {\"value\": \"http:\\/\\/www.worldweatheronline.com\\/images\\/wsymbols01_png_64\\/wsymbol_0009_light_rain_showers.png\" } ], \"winddir16Point\": \"W\", \"winddirDegree\": \"269\", \"winddirection\": \"W\", \"windspeedKmph\": \"65\", \"windspeedMiles\": \"40\" }, {\"date\": \"2013-02-22\", \"precipMM\": \"0.5\", \"tempMaxC\": \"14\", \"tempMaxF\": \"57\", \"tempMinC\": \"13\", \"tempMinF\": \"56\", \"weatherCode\": \"113\",  \"weatherDesc\": [ {\"value\": \"Sunny\" } ],  \"weatherIconUrl\": [ {\"value\": \"http:\\/\\/www.worldweatheronline.com\\/images\\/wsymbols01_png_64\\/wsymbol_0001_sunny.png\" } ], \"winddir16Point\": \"WNW\", \"winddirDegree\": \"296\", \"winddirection\": \"WNW\", \"windspeedKmph\": \"49\", \"windspeedMiles\": \"30\" }, {\"date\": \"2013-02-23\", \"precipMM\": \"0.0\", \"tempMaxC\": \"15\", \"tempMaxF\": \"58\", \"tempMinC\": \"14\", \"tempMinF\": \"57\", \"weatherCode\": \"116\",  \"weatherDesc\": [ {\"value\": \"Partly Cloudy\" } ],  \"weatherIconUrl\": [ {\"value\": \"http:\\/\\/www.worldweatheronline.com\\/images\\/wsymbols01_png_64\\/wsymbol_0002_sunny_intervals.png\" } ], \"winddir16Point\": \"SSW\", \"winddirDegree\": \"199\", \"winddirection\": \"SSW\", \"windspeedKmph\": \"42\", \"windspeedMiles\": \"26\" } ] }}";
    MemcacheService cache;
    JsonServletTempo servlet;

    public void setUp() {
        servlet = new JsonServletTempo();
        cache = servlet.prepareCacheService();
        helper.setUp();
    }

    public void tearDown() {
        cache = null;
        servlet = null;
        helper.tearDown();
    }

    public void testGetTempoFromCache() {
        /*assertNull(servlet.getTempoFromCache("37.77", "-25.58", "2013-02-19",
                cache));*/
        String result = servlet.getTempo("37.77", "-25.58", "2013-02-19");
        assertNotNull(result);
        assertEquals(result, servlet.getTempoFromCache("37.77", "-25.58",
                "2013-02-19", cache));
        /*assertNull(servlet.getTempoFromCache("37.77", "-25.58", "2013-02-20",
                cache));
        assertNull(servlet.getTempoFromCache("37.77", "-25.50", "2013-02-19",
                cache));
        assertNull(servlet.getTempoFromCache("37.70", "-25.58", "2013-02-19",
                cache));*/
    }

    public void testParseDataFound() {
        String jsonResult = servlet.parseData(jsonRest, "2013-02-19");
        Gson gson = new Gson();
        Weather weather = gson.fromJson(jsonResult, Weather.class);
        assertEquals(weather.getPrecipMM(), "0.3");
        assertEquals(weather.getTempMaxC(), "18");
        assertEquals(weather.getTempMinC(), "16");
        assertEquals(
                weather.getWeatherIconUrl().get(0).getValue(),
                "http://www.worldweatheronline.com/images/wsymbols01_png_64/wsymbol_0003_white_cloud.png");
        assertEquals(weather.getWindspeedKmph(), "40");
    }

    public void testParseDataNotFound() {
        String jsonResult = servlet.parseData(jsonRest, "2013-02-18");
        assertEquals(jsonResult, "null");
    }

    public void testParse() {
        DataJSON data = servlet.parse(jsonRest);
        List<Weather> lWeather = data.getData().getWeather();
        assertEquals(lWeather.size(), 5);
        Weather weather = lWeather.get(0);
        assertEquals(weather.getDate(), "2013-02-19");
        assertEquals(weather.getPrecipMM(), "0.3");
        assertEquals(weather.getTempMaxC(), "18");
        assertEquals(weather.getTempMinC(), "16");
        assertEquals(
                weather.getWeatherIconUrl().get(0).getValue(),
                "http://www.worldweatheronline.com/images/wsymbols01_png_64/wsymbol_0003_white_cloud.png");
        assertEquals(weather.getWindspeedKmph(), "40");
        weather = lWeather.get(1);
        assertEquals(weather.getDate(), "2013-02-20");
        weather = lWeather.get(2);
        assertEquals(weather.getDate(), "2013-02-21");
        weather = lWeather.get(3);
        assertEquals(weather.getDate(), "2013-02-22");
        weather = lWeather.get(4);
        assertEquals(weather.getDate(), "2013-02-23");
    }
}
