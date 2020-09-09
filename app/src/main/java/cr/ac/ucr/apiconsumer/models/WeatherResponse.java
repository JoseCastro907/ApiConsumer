package cr.ac.ucr.apiconsumer.models;

import java.util.List;

public class WeatherResponse {

    private Main main;
    private List<Weather> weather;

    public WeatherResponse() {
    }

    public WeatherResponse(Main main, List<Weather> weather) {
        this.main = main;
        this.weather = weather;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "WeatherResponse{" +
                "main=" + main +
                ", weather=" + weather +
                '}';
    }
}
