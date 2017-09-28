package com.cj.simplecontacts.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.simplecontacts.IndexActivity;
import com.cj.simplecontacts.R;
import com.cj.simplecontacts.enity.Aqi;
import com.cj.simplecontacts.enity.Basic;
import com.cj.simplecontacts.enity.DialyForecast;
import com.cj.simplecontacts.enity.HourlyForcast;
import com.cj.simplecontacts.enity.Now;
import com.cj.simplecontacts.enity.Suggestion;
import com.cj.simplecontacts.enity.Weather;
import com.cj.simplecontacts.service.CityService;
import com.cj.simplecontacts.service.WeatherService;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chenjun on 2017/6/a11.
 */

public class LifeAssistantFragment extends Fragment {
    private final static String TAG = "LifeAssistantFragment";
    private IndexActivity indexActivity;
    private Context context;
    private TextView tv_city;
    private static final String BASE_URL = "https://free-api.heweather.com/v5/";
    private static final String API_KEY = "e8121a9b3b9248228b5cd4fb17eb45d7";
    private LocationManager locationManager;
    private LocationListener gpsLocationListener;
    private LocationListener networkListener;
    private String currentCityName = "正在定位城市...";
    private Location currentBestLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "LifeAssistantFragment onCreate");
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "LifeAssistantFragment onCreateView");
        View view = inflater.inflate(R.layout.life_assistant_fragment, null);

        tv_city = (TextView) view.findViewById(R.id.city);
        tv_city.setText(currentCityName);
        startLocate(indexActivity);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "LifeAssistantFragment onAttach  activity");
        super.onAttach(activity);

    }



    private void startLocate(final Activity activity) {
        Log.d(TAG, "startLocate");
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);


        String locationProvider = LocationManager.NETWORK_PROVIDER;
        // Or use LocationManager.GPS_PROVIDER
        boolean lastLocationNotNull = false;
        final Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        double lastLatitude = -1d;
        double lastLongitude = -1d;
        if (lastKnownLocation != null) {
            lastLocationNotNull = true;
            lastLatitude = lastKnownLocation.getLatitude();
            lastLongitude = lastKnownLocation.getLongitude();
        } else {
            lastLocationNotNull = false;
        }

        Log.d(TAG, "last 纬度：" + lastLatitude);
        Log.d(TAG, "last 经度：" + lastLongitude);


        if (lastLocationNotNull) {
            inquiryWeatherByLocation(lastLongitude,lastLatitude);
        }
        currentBestLocation = lastKnownLocation;
        gpsLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                //位置信息变化时触发
                Log.d(TAG, "gps 纬度：" + location.getLatitude());
                Log.d(TAG, "gps 经度：" + location.getLongitude());
                Log.d(TAG, "gps 海拔：" + location.getAltitude());
                Log.d(TAG, "gps 时间：" + location.getTime());
                boolean betterLocation = isBetterLocation(location, currentBestLocation);
                if (betterLocation) {
                    currentBestLocation = location;
                    if(currentBestLocation != null){
                        inquiryWeatherByLocation(currentBestLocation.getLongitude(),currentBestLocation.getLongitude());
                    }
                }

                locationManager.removeUpdates(networkListener);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //GPS状态变化时触发
                Log.d(TAG, "gps onStatusChanged");
                if (LocationProvider.OUT_OF_SERVICE == status) {
                    Toast.makeText(activity, "GPS服务丢失,切换至网络定位",
                            Toast.LENGTH_SHORT).show();
                    checkAcessFineLocationPermission();
                    locationManager
                            .requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER, 0, 0,
                                    networkListener);
               }
            }

            @Override
            public void onProviderEnabled(String provider) {
                //GPS禁用时触发
                Log.d(TAG, "gps onProviderEnabled" );
            }

            @Override
            public void onProviderDisabled(String provider) {
                //GPS开启时触发
                Log.d(TAG, "gps onProviderDisabled" );
            }
        };
        /**
         * 绑定监听
         * 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种，前者是GPS,后者是GPRS以及WIFI定位
         * 参数2，位置信息更新周期.单位是毫秒
         * 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
         * 参数4，监听
         * 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
         */
        networkListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                //位置信息变化时触发
                Log.d(TAG, "network 纬度：" + location.getLatitude());
                Log.d(TAG, "network 经度：" + location.getLongitude());
                Log.d(TAG, "network 海拔：" + location.getAltitude());
                Log.d(TAG, "network 时间：" + location.getTime());
                boolean betterLocation = isBetterLocation(location, lastKnownLocation);
                if(betterLocation){
                    currentBestLocation = location;
                }
                if(currentBestLocation != null){
                    inquiryWeatherByLocation(currentBestLocation.getLongitude(),currentBestLocation.getLongitude());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //GPS状态变化时触发
                Log.d(TAG, "network onStatusChanged" );

            }

            @Override
            public void onProviderEnabled(String provider) {
                //GPS禁用时触发
                Log.d(TAG, "network onProviderEnabled" );
            }

            @Override
            public void onProviderDisabled(String provider) {
                //GPS开启时触发
                Log.d(TAG, "network onProviderDisabled" );
            }
        };

        checkAcessFineLocationPermission();
        Log.d(TAG, "startLocate+++");
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000 * 2,50,gpsLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,networkListener);
    }

    private void inquiryWeatherByLocation(double longitude,double latitude){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())//解析方法
                .baseUrl(BASE_URL)//主机地址
                .build();
        //2.创建访问API的请求
        CityService service = retrofit.create(CityService.class);
        Call<ResponseBody> call = service.getResult(longitude, latitude, API_KEY);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "先定位再查城市");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = jsonObject.getJSONArray("HeWeather5");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String weatherContent = null;
                    try {
                        weatherContent = jsonArray.getJSONObject(0).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Weather weather = gson.fromJson(weatherContent, Weather.class);
                    String status = weather.getStatus();
                    Log.d("test", status);
                    Basic basic = weather.getBasic();
                    String id = basic.getId();
                    String city = basic.getCity();
                    String cnty = basic.getCnty();
                    String lat = basic.getLat();
                    String lon = basic.getLon();
                    String prov = basic.getProv();
                    String s1 = " id = " + id + " city1 = " + city + " cnty = " + cnty + " lat = " + lat + " lon = " + lon + " prov = " + prov;

                    if(currentCityName.equals(city)){
                        //城市相同就啥都不用做了
                        Log.d("test", " 定位城市一样");
                    }else{
                        Log.d("test", " current thread:"+Thread.currentThread().getName());
                        currentCityName = city;
                        tv_city.setText(currentCityName);
                        //就去获取新城市的天气
                        inquiryWeatherByCity(currentCityName);
                    }


                    Log.d("test", s1);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void inquiryWeatherByCity(String city){
        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())//解析方法
                .baseUrl(BASE_URL)//主机地址
                .build();

        //2.创建访问API的请求
        WeatherService service = retrofit.create(WeatherService.class);
        Call<ResponseBody> call = service.getResult(city, API_KEY);

        //3.发送请求
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("test", " onResponse");
                //4.处理结果
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = jsonObject.getJSONArray("HeWeather5");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String weatherContent = null;
                    try {
                        weatherContent = jsonArray.getJSONObject(0).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    Weather weather = gson.fromJson(weatherContent, Weather.class);
                    String status = weather.getStatus();
                    Log.d("test", status);
                    Aqi aqi = weather.getAqi();
                    Aqi.City city = aqi.getCity();
                    String aqi1 = city.getAqi();
                    String co = city.getCo();
                    String no2 = city.getNo2();
                    String o3 = city.getO3();
                    String pm10 = city.getPm10();
                    String pm25 = city.getPm25();
                    String qlty = city.getQlty();
                    String so2 = city.getSo2();
                    String s = " aqi1 = " + aqi1 + " co = " + co + " no2 = " + no2 + " o3 = " + o3 + " pm10 = " + pm10 + " pm25 = " + pm25
                            + " qlty = " + qlty + " so2 = " + so2;
                    Log.d("test", s);
                    Basic basic = weather.getBasic();
                    String id = basic.getId();
                    String city1 = basic.getCity();
                    String cnty = basic.getCnty();
                    String lat = basic.getLat();
                    String lon = basic.getLon();
                    String prov = basic.getProv();
                    Basic.Update update = basic.getUpdate();
                    String loc = update.getLoc();
                    String utc = update.getUtc();
                    String s1 = " id = " + id + " city1 = " + city1 + " cnty = " + cnty + " lat = " + lat + " lon = " + lon + " prov = " + prov
                            + " loc = " + loc + " utc = " + utc;
                    Log.d("test", s1);

                    Now now = weather.getNow();
                    Now.Cond cond = now.getCond();
                    String code = cond.getCode();
                    String txt = cond.getTxt();
                    String fl = now.getFl();
                    String hum = now.getHum();
                    String pcpn = now.getPcpn();
                    String pres = now.getPres();
                    String tmp = now.getTmp();
                    String vis = now.getVis();
                    Now.Wind wind = now.getWind();

                    String deg = wind.getDeg();
                    String dir = wind.getDir();
                    String sc = wind.getSc();
                    String spd = wind.getSpd();
                    String s2 = " code = " + code + " txt = " + txt + " fl = " + fl + " hum = " + hum + " pcpn = " + pcpn + " pres = " + pres
                            + " tmp = " + tmp + " vis = " + vis + " deg = " + deg + " dir = " + dir + " sc = " + sc + " spd = " + spd;
                    Log.d("test", s2);

                    Suggestion suggestion = weather.getSuggestion();
                    Suggestion.Air air = suggestion.getAir();
                    String brf = air.getBrf();
                    String txt1 = air.getTxt();

                    Suggestion.Comf comf = suggestion.getComf();
                    String brf1 = comf.getBrf();
                    String txt2 = comf.getTxt();

                    Suggestion.Cw cw = suggestion.getCw();
                    String brf2 = cw.getBrf();
                    String txt3 = cw.getTxt();

                    Suggestion.Drsg drsg = suggestion.getDrsg();
                    String brf3 = drsg.getBrf();
                    String txt4 = drsg.getTxt();

                    Suggestion.Flu flu = suggestion.getFlu();
                    String brf4 = flu.getBrf();
                    String txt5 = flu.getTxt();

                    Suggestion.Trav trav = suggestion.getTrav();
                    String brf5 = trav.getBrf();
                    String txt6 = trav.getTxt();

                    Suggestion.Uv uv = suggestion.getUv();
                    String brf6 = uv.getBrf();
                    String txt7 = uv.getTxt();

                    String s3 = " brf = " + brf + " txt1 = " + txt1 + " brf1 = " + brf1 + " txt2 = " + txt2 + " brf2 = " + brf2 + " txt3 = " + txt3
                            + " brf3 = " + brf3 + " txt4 = " + txt4 + " brf4 = " + brf4 + " txt5 = " + txt5 + " brf5 = " + brf5 + " txt6 = " + txt6 +
                            " brf6 = " + brf6 + " txt7 = " + txt7;
                    Log.d("test", s3);

                    List<DialyForecast> daily_forecast = weather.getDaily_forecast();
                    int size = 0;
                    if (daily_forecast != null) {
                        size = daily_forecast.size();
                        Log.d("test", "size = " + size);
                        for (int i = 0; i < size; i++) {
                            DialyForecast dialyForecast = daily_forecast.get(i);
                            DialyForecast.Astro astro = dialyForecast.getAstro();
                            String mr = astro.getMr();
                            String ms = astro.getMs();
                            String sr = astro.getSr();
                            String ss = astro.getSs();

                            DialyForecast.Cond cond1 = dialyForecast.getCond();
                            String code_d = cond1.getCode_d();
                            String code_n = cond1.getCode_n();
                            String txt_d = cond1.getTxt_d();
                            String txt_n = cond1.getTxt_n();

                            String date = dialyForecast.getDate();
                            String hum1 = dialyForecast.getHum();
                            String pcpn1 = dialyForecast.getPcpn();
                            String pop = dialyForecast.getPop();
                            String pres1 = dialyForecast.getPres();

                            DialyForecast.Tmp tmp1 = dialyForecast.getTmp();
                            String max = tmp1.getMax();
                            String min = tmp1.getMin();

                            String vis1 = dialyForecast.getVis();

                            DialyForecast.Wind wind1 = dialyForecast.getWind();
                            String deg1 = wind1.getDeg();
                            String dir1 = wind1.getDir();
                            String sc1 = wind1.getSc();
                            String spd1 = wind1.getSpd();

                            String s4 = "dialyForecast: " + i + " mr = " + mr + " ms = " + ms + " sr = " + sr + " ss = " + ss + " code_d = " + code_d + " code_n = " + code_n
                                    + " txt_d = " + txt_d + " txt_n = " + txt_n + " date = " + date + " hum1 = " + hum1 + " pcpn1 = " + pcpn1 + " pop = " + pop +
                                    " pres1 = " + pres1 + " max = " + max + " min = " + min + " vis1 = " + vis1 + " deg1 = " + deg1 + " dir1 = " + dir1
                                    + " sc1 = " + sc1 + " spd1 = " + spd1;

                            Log.d("test", s4);
                        }
                    }

                    List<HourlyForcast> hourly_forecast = weather.getHourly_forecast();
                    int size1 = 0;
                    if (hourly_forecast != null) {
                        size1 = hourly_forecast.size();
                        Log.d("test", "size1 = " + size1);
                        for (int i = 0; i < size1; i++) {
                            HourlyForcast hourlyForcast = hourly_forecast.get(i);
                            HourlyForcast.Cond cond1 = hourlyForcast.getCond();
                            String code1 = cond1.getCode();
                            String txt8 = cond1.getTxt();

                            String date = hourlyForcast.getDate();

                            String hum1 = hourlyForcast.getHum();

                            String pop = hourlyForcast.getPop();

                            String pres1 = hourlyForcast.getPres();

                            String tmp1 = hourlyForcast.getTmp();

                            HourlyForcast.Wind wind1 = hourlyForcast.getWind();
                            String deg1 = wind1.getDeg();
                            String dir1 = wind1.getDir();
                            String sc1 = wind1.getSc();
                            String spd1 = wind1.getSpd();
                            String s5 = "hourlyForcast: " + i + " code1 = " + code1 + " txt8 = " + txt8 + " date = " + date + " hum1 = " + hum1 + " pop = " + pop + " pres1 = " + pres1
                                    + " tmp1 = " + tmp1 + " deg1 = " + deg1 + " dir1 = " + dir1
                                    + " sc1 = " + sc1 + " spd1 = " + spd1;

                            Log.d("test", s5);
                        }
                    }
                   // weatherInfo.setText(status + "  " + s + "  " + s1 + "  " + s2 + "  " + s3 + " " + size + "  " + size1);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("test", " onFailure");
            }
        });

    }


    private void checkAcessFineLocationPermission(){
        if (ActivityCompat.checkSelfPermission(indexActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(indexActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }


    private static final int TWO_MINUTES = 1000 * 60 * 2;
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "LifeAssistantFragment onAttach context");
        super.onAttach(context);
        this.context = context;
        this.indexActivity = (IndexActivity) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "LifeAssistantFragment onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "LifeAssistantFragment onStart");
        super.onStart();

    }

    @Override
    public void onResume() {
        Log.d(TAG, "LifeAssistantFragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "LifeAssistantFragment onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "LifeAssistantFragment onStop");
        super.onStop();
        locationManager.removeUpdates(networkListener);
        locationManager.removeUpdates(gpsLocationListener);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "LifeAssistantFragment onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "LifeAssistantFragment onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "LifeAssistantFragment onDestroyView");
        super.onDestroyView();
    }


}
