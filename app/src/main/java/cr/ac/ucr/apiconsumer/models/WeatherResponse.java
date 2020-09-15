package cr.ac.ucr.apiconsumer.models;

import java.util.List;

public class WeatherResponse {

    private Main main;
    private List<Weather> weather;
    private Sys sys;
    private String name;

    public WeatherResponse() {
    }

    public WeatherResponse(Main main, List<Weather> weather, String name,Sys sys) {
        this.main = main;
        this.weather = weather;
        this.name=name;
        this.sys=sys;
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

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WeatherResponse{" +
                "main=" + main +
                ", weather=" + weather +
                ", sys=" + sys +
                ", name='" + name + '\'' +
                '}';
    }
}
