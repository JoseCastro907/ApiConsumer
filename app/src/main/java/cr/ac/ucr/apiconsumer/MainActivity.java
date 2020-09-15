package cr.ac.ucr.apiconsumer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cr.ac.ucr.apiconsumer.api.RetrofitBuilder;
import cr.ac.ucr.apiconsumer.api.WeatherService;
import cr.ac.ucr.apiconsumer.models.Main;
import cr.ac.ucr.apiconsumer.models.Sys;
import cr.ac.ucr.apiconsumer.models.Weather;
import cr.ac.ucr.apiconsumer.models.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private final String TAG = "MainActivity";
    private final int LOCATION_CODE_REQUEST =23423;

    private  ConstraintLayout clContainer;
    private  TextView tvCity;
    private  TextView tvGreeting;
    private  TextView tvDescription ;
    private TextView tvMinmax;
    private  TextView tvTemperature;
    private  ImageView ivImage;
    private String day;
    private LocationManager locationManager;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude =10.060727;
        longitude =-84.4378495;

        clContainer = findViewById(R.id.cl_container);
        tvCity = findViewById(R.id.tv_city);
        tvGreeting = findViewById(R.id.tv_greeting);
        tvDescription = findViewById(R.id.tv_description);
        tvMinmax = findViewById(R.id.tv_minmax);
        tvTemperature = findViewById(R.id.tv_temperature);

        ivImage = findViewById(R.id.iv_image);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkPermissions();

        setBackGroundAndGreeting();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_CODE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
            } else {
                getWeather(latitude, longitude);
            }
        }
    }
    private void checkPermissions() {
        // Los permisos de esta forma se solicitan despues de la version M de android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                        },
                        LOCATION_CODE_REQUEST
                );
                return;
            }
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        try {

            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                onLocationChanged(location);
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("Para una mejor funcionalidad, activa el GPS")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .show();

                getWeather(latitude, longitude);

            }

        } catch (Exception e){
//            e.printStackTrace();
//            Log.i(TAG, "Oh no!, hubo un error");

            getWeather(latitude, longitude);
        }
    }


    private void setBackGroundAndGreeting() {

        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 5 && timeOfDay <12){
            tvGreeting.setText(R.string.day);
            clContainer.setBackgroundResource(R.drawable.background_day);
        }else if(timeOfDay >=12 && timeOfDay <19){
            tvGreeting.setText(R.string.afternoon);
            clContainer.setBackgroundResource(R.drawable.background_afternoon);
        }else{
            tvGreeting.setText(R.string.night);
            clContainer.setBackgroundResource(R.drawable.background_night);
        }

        day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

    }


    private void getWeather(double latitude, double longitude) {
        WeatherService service = RetrofitBuilder.createService(WeatherService.class);

        Call<WeatherResponse> response = service.getWeatherByCoordinates(latitude, longitude);

        final AppCompatActivity activity = this;

        response.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {

                Log.i(TAG, String.valueOf(call.request().url()));

                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();

                    Main main = weatherResponse.getMain();
                    List<Weather> weatherList = weatherResponse.getWeather();
                    Sys sys = weatherResponse.getSys();

                    String temperature = getString(R.string.temperature, String.valueOf(Math.round(main.getTemp())));

                    tvTemperature.setText(temperature);

                    String minmax = getString(R.string.minmax, String.valueOf(Math.round(main.getTemp_min())), String.valueOf(Math.round(main.getTemp_max())));

                    tvMinmax.setText(minmax);

                    if (weatherList.size() > 0) {
                        Weather weather = weatherList.get(0);

                        // tvDescription.setText(String.format("%s, %s", day, weather.getDescription()));
                        tvDescription.setText(String.format("%s, %s", day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase(), weather.getMain()));

                        String imageUrl = String.format("https://openweathermap.org/img/wn/%s@2x.png", weather.getIcon());

                        RequestOptions options = new RequestOptions()
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .priority(Priority.HIGH);

                        Glide.with(activity)
                                .load(imageUrl)
                                .apply(options)
                                .into(ivImage);
                    }

                    tvCity.setText(String.format("%s, %s", weatherResponse.getName(), sys.getCountry()));


                } else {
                    Log.e(TAG, "OnError: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                throw new RuntimeException(t);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        getWeather(latitude, longitude);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}