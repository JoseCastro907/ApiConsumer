package cr.ac.ucr.apiconsumer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import cr.ac.ucr.apiconsumer.api.RetrofitBuilder;
import cr.ac.ucr.apiconsumer.api.WeatherService;
import cr.ac.ucr.apiconsumer.models.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void getWeather(){
        WeatherService service = RetrofitBuilder.createService(WeatherService.class);

        Call<WeatherResponse> response =service.getWeatherByCoordinates(0,0);

        response.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {

                if(response.isSuccessful()){
                    WeatherResponse weatherResponse = response.body();
                } else {
                    Log.e(TAG,"OnError: "+response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, Throwable t) {
                throw new RuntimeException(t);
            }
        });
    }
}