
package so.wih.android.jjewatch.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

    /**
     * 2017/1/9 天气
     */
public class WeatherResult {


    private String currentCity;

    private String pm25;

    private List<Index> index = null;

    private List<WeatherDatum> weather_data = null;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public String getCurrentCity() {
        return currentCity;
    }


    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }


    public String getPm25() {
        return pm25;
    }


    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }


    public List<Index> getIndex() {
        return index;
    }

    public void setIndex(List<Index> index) {
        this.index = index;
    }


    public List<WeatherDatum> getWeatherData() {
        return weather_data;
    }


    public void setWeatherData(List<WeatherDatum> weatherData) {
        this.weather_data = weatherData;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
