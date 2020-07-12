package com.example.assignment_5;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForecastActivity extends AppCompatActivity {

    private TextView tv_forecast_info;

    private ImageView iv_presentSkyStat;
    private TextView tv_forecast_presentTemperature;

    private TextView tv_location;

    private ArrayList<ForecastItem> fcstItemList;       // 받아온 100개의 item들을 저장할 ArrayList
    private ArrayList<ForecastUnit> fcstUnitList;       // 받아온 100개의 item들을 가공하여 3시간 단위로 제공할 8개의 Unit을 담을 ArrayList

    private Query mQuery;

    private TextView tv_forecast_summary, tv_forecast_highTemp, tv_forecast_lowTemp;

    private ForecastUnit forecastUnit_1, forecastUnit_2, forecastUnit_3, forecastUnit_4, forecastUnit_5, forecastUnit_6, forecastUnit_7, forecastUnit_8;

    private ImageView sky_1, sky_2, sky_3, sky_4, sky_5, sky_6, sky_7, sky_8;

    private TextView date_1, date_2, date_3, date_4, date_5, date_6, date_7, date_8,
            time_1, time_2, time_3, time_4, time_5, time_6, time_7, time_8,
            temperature_1, temperature_2, temperature_3, temperature_4, temperature_5, temperature_6, temperature_7, temperature_8,
            rainPossibility_1, rainPossibility_2, rainPossibility_3, rainPossibility_4, rainPossibility_5, rainPossibility_6, rainPossibility_7, rainPossibility_8,
            humidity_1, humidity_2, humidity_3, humidity_4, humidity_5, humidity_6, humidity_7, humidity_8;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        iv_presentSkyStat = findViewById(R.id.iv_forecast_presentSkyStat);

        tv_forecast_presentTemperature = findViewById(R.id.tv_forecast_presentTemperature);

        tv_forecast_summary = findViewById(R.id.tv_forecast_summary);
        tv_forecast_highTemp = findViewById(R.id.tv_forecast_highTemp);
        tv_forecast_lowTemp = findViewById(R.id.tv_forecast_lowTemp);

        initTextViews();





//        tv_forecast_info = findViewById(R.id.tv_forecast_info);

        fcstItemList = new ArrayList<>();

        fcstUnitList = new ArrayList<>();

//        Query 객체 하나 만들어서 intent로 넘어온 mQuery 정보 받아옴(날짜 / 시간 / 지역의 x좌표 / 지역의 y좌표)
        mQuery = new Query();
        mQuery = (Query) getIntent().getSerializableExtra("mQuery");


        MyAsyncTask mProcessTask = new MyAsyncTask();
        mProcessTask.execute();

    }

    //    AsyncTask 생성 - 모든 네트워크 로직을 여기서 작성해 준다.
    public class MyAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = new ProgressDialog(ForecastActivity.this);
        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("\t데이터를 불러오는 중...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=J4pOcwtDet2eQP7URaYu3VEaIobya1dETL2nBwVxYuEsR6DKoq9vo37eHLt70WkxK9xZZZr0Gdq%2BpWiuMKfblA%3D%3D").newBuilder();

            urlBuilder.addQueryParameter("numOfRows", "100");
            urlBuilder.addQueryParameter("pageNo", "1");
            urlBuilder.addQueryParameter("base_date", mQuery.getDate());
            urlBuilder.addQueryParameter("base_time", mQuery.getTime());
            urlBuilder.addQueryParameter("nx", mQuery.getNx());
            urlBuilder.addQueryParameter("ny", mQuery.getNy());
            urlBuilder.addQueryParameter("dataType", "JSON");

            String url = urlBuilder.build().toString();

            Request request = new Request.Builder().url(url).build();           // 완성시킨 url을 Request타입으로 만들어진 request에 담는다.

            try {
                Response response = client.newCall(request).execute();          // request를 실행한 후에 답장을 response에 담은 후에,
                return response.body().string();                                // JSON 형식의 String으로 바꾸어서 return해줌

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            /**
             * 일단 가볍게 지역명부터 사용자가 고른 것으로 설정해주자
             * */
            tv_location.setText(mQuery.getLocation());

            /**
             * JSON 형식으로 된 String이 넘어왔으므로, 그것을 다시 JsonObject로 만들어야 파싱이 가능하다.
             * */
            JsonParser parser = new JsonParser();
            Object obj = parser.parse(result);
            JsonObject jsonObj = (JsonObject) obj;

            /**
             * jsonObject를 뜯어내서 "item" 자리까지 들어간 후, 그것을 하나의 jsonElement로 만들어준다.
             * */
            JsonElement jsonElement = parser.parse(String.valueOf(jsonObj.getAsJsonObject().get("response").getAsJsonObject().get("body").getAsJsonObject().get("items").getAsJsonObject().get("item")));

            /**
             * jsonElement로 만들어버린 "item" 안에 총 100개의 JsonElement가 있으므로, 이 "jsonArray"를 JsonArray 형식으로 만든다.
             * */
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            /**
             * JsonArray 형식으로 이루어진 "item"들을 쪼개서, for문을 통해 ArrayList<JsonObject> 안에 100번 집어넣는다.
             * */
            ArrayList<JsonObject> arrayJsonObject = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                JsonObject tempJsonObject = jsonArray.get(i).getAsJsonObject();
                arrayJsonObject.add(tempJsonObject);
            }

            /**
             * ArrayList<JsonObject> 안에 있는 100개의 JsonObject를 이용하여 ArrayList<ForecastItem> 100개를 채워준다.
             * */
            for (int i = 0; i < 100; i++) {
                ForecastItem forecastItem = new ForecastItem();

                forecastItem.setBaseDate(arrayJsonObject.get(i).getAsJsonObject().get("baseDate").toString());
                forecastItem.setBaseTime(arrayJsonObject.get(i).getAsJsonObject().get("baseTime").toString());
                forecastItem.setCategory(arrayJsonObject.get(i).getAsJsonObject().get("category").toString());
                forecastItem.setFcstDate(arrayJsonObject.get(i).getAsJsonObject().get("fcstDate").toString());
                forecastItem.setFcstTime(arrayJsonObject.get(i).getAsJsonObject().get("fcstTime").toString());
                forecastItem.setFcstValue(arrayJsonObject.get(i).getAsJsonObject().get("fcstValue").toString());
                forecastItem.setNx(Integer.parseInt(arrayJsonObject.get(i).getAsJsonObject().get("nx").toString()));
                forecastItem.setNy(Integer.parseInt(arrayJsonObject.get(i).getAsJsonObject().get("ny").toString()));

                fcstItemList.add(forecastItem);
            }

            /**
             * 이제 100개의 ForecastItem이 채워졌으니, 3시간별로 잘라서 담아 표현해야 함.
             * */
            switch (mQuery.getTime()) {
                /**
                 * 쿼리의 baseTime이 02시 일 때!!! (완성되었으나 테스트 필요)
                 * */
                case "0200":
                    forecastUnit_1 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0600")) {

                            forecastUnit_1.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_1.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_1.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_1.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_1.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_1.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_1.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_1.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_1.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_1.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_1.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_1.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_1);

                    forecastUnit_2 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0900")) {

                            forecastUnit_2.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_2.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_2.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_2.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_2.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_2.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_2.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_2.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_2.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_2.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_2.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_2.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_2);

                    forecastUnit_3 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1200")) {

                            forecastUnit_3.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_3.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_3.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_3.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_3.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_3.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_3.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_3.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_3.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_3.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_3.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_3.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_3);

                    forecastUnit_4 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1500")) {

                            forecastUnit_4.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_4.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_4.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_4.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_4.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_4.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_4.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_4.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_4.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_4.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_4.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_4.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_4);

                    forecastUnit_5 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1800")) {

                            forecastUnit_5.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_5.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_5.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_5.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_5.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_5.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_5.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_5.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_5.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_5.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_5.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_5.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_5);

                    forecastUnit_6 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("2100")) {

                            forecastUnit_6.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_6.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_6.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_6.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_6.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_6.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_6.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_6.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_6.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_6.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_6.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_6.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_6);

                    forecastUnit_7 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0000")) {

                            forecastUnit_7.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_7.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_7.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_7.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_7.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_7.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_7.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_7.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_7.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_7.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_7.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_7.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_7);

                    forecastUnit_8 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0300")) {

                            forecastUnit_8.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_8.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_8.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_8.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_8.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_8.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_8.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_8.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_8.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_8.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_8.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_8.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_8);

                    setTextViewsWithData();
                    break;

                /**
                 * 쿼리의 baseTime이 05시 일 때!!! (완성되었으나 테스트 필요)
                 * */
                case "0500":
                    forecastUnit_1 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0900")) {

                            forecastUnit_1.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_1.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_1.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_1.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_1.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_1.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_1.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_1.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_1.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_1.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_1.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_1.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_1);

                    forecastUnit_2 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1200")) {

                            forecastUnit_2.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_2.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_2.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_2.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_2.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_2.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_2.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_2.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_2.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_2.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_2.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_2.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_2);

                    forecastUnit_3 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1500")) {

                            forecastUnit_3.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_3.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_3.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_3.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_3.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_3.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_3.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_3.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_3.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_3.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_3.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_3.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_3);

                    forecastUnit_4 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1800")) {

                            forecastUnit_4.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_4.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_4.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_4.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_4.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_4.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_4.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_4.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_4.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_4.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_4.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_4.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_4);

                    forecastUnit_5 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("2100")) {

                            forecastUnit_5.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_5.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_5.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_5.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_5.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_5.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_5.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_5.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_5.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_5.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_5.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_5.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_5);

                    forecastUnit_6 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0000")) {

                            forecastUnit_6.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_6.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_6.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_6.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_6.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_6.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_6.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_6.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_6.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_6.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_6.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_6.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_6);

                    forecastUnit_7 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0300")) {

                            forecastUnit_7.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_7.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_7.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_7.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_7.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_7.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_7.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_7.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_7.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_7.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_7.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_7.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_7);

                    forecastUnit_8 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0600")) {

                            forecastUnit_8.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_8.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_8.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_8.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_8.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_8.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_8.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_8.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_8.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_8.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_8.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_8.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_8);

                    setTextViewsWithData();
                    break;

                /**
                 * 쿼리의 baseTime이 08시 일 때!!! (완성되었으나 테스트 필요)
                 * */
                case "0800":
                    forecastUnit_1 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1200")) {

                            forecastUnit_1.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_1.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_1.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_1.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_1.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_1.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_1.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_1.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_1.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_1.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_1.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_1.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_1);

                    forecastUnit_2 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1500")) {

                            forecastUnit_2.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_2.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_2.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_2.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_2.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_2.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_2.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_2.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_2.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_2.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_2.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_2.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_2);

                    forecastUnit_3 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1800")) {

                            forecastUnit_3.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_3.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_3.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_3.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_3.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_3.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_3.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_3.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_3.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_3.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_3.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_3.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_3);

                    forecastUnit_4 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("2100")) {

                            forecastUnit_4.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_4.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_4.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_4.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_4.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_4.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_4.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_4.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_4.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_4.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_4.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_4.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_4);

                    forecastUnit_5 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0000")) {

                            forecastUnit_5.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_5.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_5.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_5.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_5.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_5.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_5.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_5.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_5.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_5.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_5.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_5.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_5);

                    forecastUnit_6 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0300")) {

                            forecastUnit_6.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_6.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_6.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_6.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_6.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_6.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_6.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_6.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_6.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_6.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_6.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_6.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_6);

                    forecastUnit_7 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0600")) {

                            forecastUnit_7.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_7.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_7.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_7.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_7.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_7.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_7.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_7.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_7.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_7.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_7.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_7.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_7);

                    forecastUnit_8 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0900")) {

                            forecastUnit_8.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_8.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_8.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_8.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_8.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_8.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_8.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_8.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_8.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_8.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_8.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_8.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_8);

                    setTextViewsWithData();
                    break;

                /**
                 * 쿼리의 baseTime이 11시 일 때!!! (완성되었으나 테스트 필요)
                 * */
                case "1100":
                    forecastUnit_1 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1500")) {

                            forecastUnit_1.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_1.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_1.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_1.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_1.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_1.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_1.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_1.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_1.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_1.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_1.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_1.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_1);

                    forecastUnit_2 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1800")) {

                            forecastUnit_2.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_2.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_2.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_2.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_2.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_2.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_2.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_2.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_2.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_2.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_2.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_2.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_2);

                    forecastUnit_3 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("2100")) {

                            forecastUnit_3.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_3.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_3.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_3.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_3.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_3.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_3.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_3.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_3.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_3.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_3.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_3.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_3);

                    forecastUnit_4 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0000")) {

                            forecastUnit_4.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_4.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_4.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_4.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_4.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_4.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_4.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_4.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_4.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_4.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_4.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_4.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_4);

                    forecastUnit_5 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0300")) {

                            forecastUnit_5.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_5.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_5.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_5.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_5.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_5.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_5.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_5.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_5.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_5.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_5.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_5.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_5);

                    forecastUnit_6 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0600")) {

                            forecastUnit_6.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_6.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_6.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_6.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_6.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_6.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_6.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_6.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_6.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_6.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_6.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_6.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_6);

                    forecastUnit_7 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0900")) {

                            forecastUnit_7.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_7.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_7.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_7.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_7.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_7.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_7.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_7.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_7.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_7.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_7.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_7.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_7);

                    forecastUnit_8 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1200")) {

                            forecastUnit_8.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_8.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_8.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_8.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_8.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_8.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_8.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_8.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_8.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_8.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_8.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_8.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_8);

                    setTextViewsWithData();
                    break;


                /**
                 * 쿼리의 baseTime이 14시 일 때!!! (완성되었으나 테스트 필요)
                 * */
                case "1400":
                    forecastUnit_1 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1800")) {

                            forecastUnit_1.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_1.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_1.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_1.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_1.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_1.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_1.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_1.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_1.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_1.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_1.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_1.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_1);

                    forecastUnit_2 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("2100")) {

                            forecastUnit_2.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_2.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_2.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_2.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_2.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_2.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_2.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_2.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_2.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_2.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_2.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_2.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_2);

                    forecastUnit_3 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0000")) {

                            forecastUnit_3.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_3.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_3.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_3.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_3.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_3.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_3.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_3.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_3.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_3.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_3.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_3.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_3);

                    forecastUnit_4 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0300")) {

                            forecastUnit_4.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_4.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_4.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_4.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_4.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_4.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_4.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_4.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_4.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_4.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_4.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_4.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_4);

                    forecastUnit_5 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0600")) {

                            forecastUnit_5.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_5.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_5.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_5.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_5.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_5.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_5.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_5.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_5.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_5.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_5.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_5.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_5);

                    forecastUnit_6 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0900")) {

                            forecastUnit_6.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_6.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_6.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_6.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_6.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_6.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_6.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_6.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_6.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_6.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_6.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_6.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_6);

                    forecastUnit_7 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1200")) {

                            forecastUnit_7.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_7.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_7.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_7.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_7.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_7.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_7.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_7.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_7.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_7.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_7.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_7.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_7);

                    forecastUnit_8 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1500")) {

                            forecastUnit_8.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_8.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_8.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_8.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_8.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_8.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_8.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_8.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_8.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_8.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_8.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_8.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_8);

                    setTextViewsWithData();
                    break;


                /**
                 * 쿼리의 baseTime이 17시 일 때!!! (완성!!)
                 * */
                case "1700":
                    forecastUnit_1 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {
                        if (fcstItemList.get(i).getFcstDate().replaceAll("\"", "").equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("2100")) {

                            forecastUnit_1.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_1.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_1.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_1.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_1.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_1.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_1.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_1.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_1.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_1.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_1.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_1.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_1);

                    forecastUnit_2 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0000")) {

                            forecastUnit_2.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_2.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_2.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_2.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_2.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_2.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_2.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_2.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_2.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_2.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_2.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_2.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_2);

                    forecastUnit_3 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0300")) {

                            forecastUnit_3.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_3.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_3.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_3.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_3.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_3.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_3.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_3.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_3.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_3.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_3.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_3.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_3);

                    forecastUnit_4 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0600")) {

                            forecastUnit_4.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_4.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_4.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_4.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_4.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_4.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_4.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_4.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_4.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_4.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_4.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_4.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_4);

                    forecastUnit_5 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0900")) {

                            forecastUnit_5.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_5.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_5.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_5.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_5.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_5.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_5.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_5.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_5.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_5.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_5.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_5.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_5);

                    forecastUnit_6 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1200")) {

                            forecastUnit_6.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_6.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_6.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_6.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_6.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_6.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_6.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_6.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_6.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_6.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_6.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_6.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_6);

                    forecastUnit_7 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1500")) {

                            forecastUnit_7.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_7.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_7.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_7.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_7.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_7.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_7.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_7.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_7.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_7.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_7.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_7.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_7);

                    forecastUnit_8 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1800")) {

                            forecastUnit_8.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_8.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_8.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_8.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_8.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_8.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_8.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_8.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_8.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_8.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_8.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_8.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_8);

                    setTextViewsWithData();
                    break;

                /**
                 * 쿼리의 baseTime이 20시 일 때!!! (완성!!)
                 * */
                case "2000":
                    forecastUnit_1 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0000")) {

                            forecastUnit_1.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_1.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_1.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_1.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_1.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_1.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_1.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_1.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_1.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_1.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_1.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_1.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_1);

                    forecastUnit_2 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0300")) {

                            forecastUnit_2.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_2.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_2.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_2.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_2.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_2.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_2.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_2.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_2.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_2.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_2.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_2.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_2);

                    forecastUnit_3 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0600")) {

                            forecastUnit_3.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_3.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_3.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_3.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_3.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_3.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_3.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_3.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_3.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_3.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_3.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_3.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_3);

                    forecastUnit_4 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0900")) {

                            forecastUnit_4.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_4.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_4.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_4.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_4.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_4.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_4.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_4.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_4.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_4.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_4.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_4.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_4);

                    forecastUnit_5 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1200")) {

                            forecastUnit_5.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_5.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_5.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_5.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_5.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_5.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_5.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_5.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_5.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_5.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_5.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_5.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_5);

                    forecastUnit_6 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1500")) {

                            forecastUnit_6.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_6.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_6.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_6.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_6.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_6.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_6.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_6.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_6.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_6.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_6.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_6.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_6);

                    forecastUnit_7 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1800")) {

                            forecastUnit_7.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_7.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_7.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_7.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_7.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_7.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_7.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_7.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_7.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_7.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_7.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_7.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_7);

                    forecastUnit_8 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("2100")) {

                            forecastUnit_8.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_8.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_8.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_8.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_8.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_8.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_8.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_8.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_8.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_8.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_8.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_8.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_8);

                    setTextViewsWithData();
                    break;

                /**
                 * 쿼리의 baseTime이 23시 일 때!!! (완성되었으나 테스트 필요)
                 * */
                case "2300":
                    forecastUnit_1 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0300")) {

                            forecastUnit_1.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_1.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_1.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_1.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_1.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_1.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_1.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_1.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_1.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_1.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_1.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_1.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_1);

                    forecastUnit_2 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0600")) {

                            forecastUnit_2.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_2.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_2.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_2.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_2.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_2.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_2.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_2.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_2.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_2.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_2.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_2.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_2);

                    forecastUnit_3 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0900")) {

                            forecastUnit_3.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_3.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_3.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_3.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_3.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_3.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_3.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_3.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_3.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_3.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_3.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_3.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_3);

                    forecastUnit_4 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1200")) {

                            forecastUnit_4.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_4.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_4.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_4.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_4.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_4.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_4.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_4.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_4.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_4.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_4.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_4.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_4);

                    forecastUnit_5 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1500")) {

                            forecastUnit_5.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_5.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_5.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_5.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_5.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_5.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_5.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_5.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_5.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_5.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_5.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_5.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_5);

                    forecastUnit_6 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("1800")) {

                            forecastUnit_6.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_6.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_6.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_6.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_6.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_6.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_6.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_6.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_6.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_6.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_6.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_6.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_6);

                    forecastUnit_7 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate--;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("2100")) {

                            forecastUnit_7.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_7.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_7.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_7.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_7.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_7.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_7.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_7.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_7.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_7.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_7.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_7.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_7);

                    forecastUnit_8 = new ForecastUnit();
                    for (int i = 0; i < fcstItemList.size(); i++) {

                        int int_fcstDate = Integer.parseInt(fcstItemList.get(i).getFcstDate().replaceAll("\"", ""));
                        int_fcstDate -= 2;

                        if (Integer.toString(int_fcstDate).equals(mQuery.getDate()) && fcstItemList.get(i).getFcstTime().replaceAll("\"", "").equals("0000")) {

                            forecastUnit_8.setFcstDate(fcstItemList.get(i).getFcstDate());
                            forecastUnit_8.setFcstTime(fcstItemList.get(i).getFcstTime());
                            // fcstDate와 fcstTime 셋팅 완료

                            // POP~WSD 10개 변수 셋팅 완료
                            if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("POP")) {
                                forecastUnit_8.setPOP_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("PTY")) {
                                forecastUnit_8.setPTY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("R06")) {
                                forecastUnit_8.setR06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("REH")) {
                                forecastUnit_8.setREH_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("S06")) {
                                forecastUnit_8.setS06_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("SKY")) {
                                forecastUnit_8.setSKY_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("T3H")) {
                                forecastUnit_8.setT3H_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMN")) {
                                forecastUnit_8.setTMN_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("TMX")) {
                                forecastUnit_8.setTMX_value(fcstItemList.get(i).getFcstValue());
                            } else if (fcstItemList.get(i).getCategory().replaceAll("\"", "").equals("WSD")) {
                                forecastUnit_8.setWSD_value(fcstItemList.get(i).getFcstValue());
                            }
                        }
                    }
                    fcstUnitList.add(forecastUnit_8);

                    setTextViewsWithData();
                    break;
            }



        }
    }

    private void initTextViews() {

        tv_location = findViewById(R.id.tv_main_location);

//        date_1 = findViewById(R.id.date_1);
//        date_2 = findViewById(R.id.date_2);
//        date_3 = findViewById(R.id.date_3);
//        date_4 = findViewById(R.id.date_4);
//        date_5 = findViewById(R.id.date_5);
//        date_6 = findViewById(R.id.date_6);
//        date_7 = findViewById(R.id.date_7);
//        date_8 = findViewById(R.id.date_8);

        time_1 = findViewById(R.id.time_1);
        time_2 = findViewById(R.id.time_2);
        time_3 = findViewById(R.id.time_3);
        time_4 = findViewById(R.id.time_4);
        time_5 = findViewById(R.id.time_5);
        time_6 = findViewById(R.id.time_6);
        time_7 = findViewById(R.id.time_7);
        time_8 = findViewById(R.id.time_8);

        temperature_1 = findViewById(R.id.temperature_1);
        temperature_2 = findViewById(R.id.temperature_2);
        temperature_3 = findViewById(R.id.temperature_3);
        temperature_4 = findViewById(R.id.temperature_4);
        temperature_5 = findViewById(R.id.temperature_5);
        temperature_6 = findViewById(R.id.temperature_6);
        temperature_7 = findViewById(R.id.temperature_7);
        temperature_8 = findViewById(R.id.temperature_8);

        sky_1 = findViewById(R.id.sky_stat_1);
        sky_2 = findViewById(R.id.sky_stat_2);
        sky_3 = findViewById(R.id.sky_stat_3);
        sky_4 = findViewById(R.id.sky_stat_4);
        sky_5 = findViewById(R.id.sky_stat_5);
        sky_6 = findViewById(R.id.sky_stat_6);
        sky_7 = findViewById(R.id.sky_stat_7);
        sky_8 = findViewById(R.id.sky_stat_8);

        rainPossibility_1 = findViewById(R.id.possibility_rain_1);
        rainPossibility_2 = findViewById(R.id.possibility_rain_2);
        rainPossibility_3 = findViewById(R.id.possibility_rain_3);
        rainPossibility_4 = findViewById(R.id.possibility_rain_4);
        rainPossibility_5 = findViewById(R.id.possibility_rain_5);
        rainPossibility_6 = findViewById(R.id.possibility_rain_6);
        rainPossibility_7 = findViewById(R.id.possibility_rain_7);
        rainPossibility_8 = findViewById(R.id.possibility_rain_8);

//        humidity_1 = findViewById(R.id.humidity_1);
//        humidity_2 = findViewById(R.id.humidity_2);
//        humidity_3 = findViewById(R.id.humidity_3);
//        humidity_4 = findViewById(R.id.humidity_4);
//        humidity_5 = findViewById(R.id.humidity_5);
//        humidity_6 = findViewById(R.id.humidity_6);
//        humidity_7 = findViewById(R.id.humidity_7);
//        humidity_8 = findViewById(R.id.humidity_8);
    }

    public void setTextViewsWithData() {

        /**
         * 상단 정보 채우기
         * */

        switch (fcstUnitList.get(0).getSKY_value().replaceAll("\"", "")){           // 현재 날씨
            case "1":
                iv_presentSkyStat.setImageResource(R.drawable.sunny);
                break;
            case "3":
                iv_presentSkyStat.setImageResource(R.drawable.cloudy);
                break;
            case "4":
                iv_presentSkyStat.setImageResource(R.drawable.rainy);
                break;
        }

        tv_forecast_presentTemperature.setText(fcstUnitList.get(0).getT3H_value().replaceAll("\"", "") + "°C");       // 현재 기온

        switch (fcstUnitList.get(0).getSKY_value().replaceAll("\"", "")){           // 현재 날씨 설명
            case "1":
                tv_forecast_summary.setText("맑은 날");
                break;
            case "3":
                tv_forecast_summary.setText("흐린 날");
                break;
            case "4":
                tv_forecast_summary.setText("비 오는 날");
                break;
        }

        for (int i = 0; i < fcstUnitList.size(); i++){
            if(fcstUnitList.get(i).getFcstTime().replaceAll("\"", "").equals("1500"))
                tv_forecast_highTemp.setText(fcstUnitList.get(i).getTMX_value().replaceAll("\"", "") + "°C");           // 최고기온 표시
        }

        for (int i = 0; i < fcstUnitList.size(); i++){
            if(fcstUnitList.get(i).getFcstTime().replaceAll("\"", "").equals("0600"))
                tv_forecast_lowTemp.setText(fcstUnitList.get(i).getTMN_value().replaceAll("\"", "") + "°C");           // 최저기온 표시
        }




        /**
         * 1번 유닛 채우기
         * */
        switch (fcstUnitList.get(0).getFcstTime().replaceAll("\"", "")){            // 1. time 표기
            case "0000":
                time_1.setText("밤 12시");
                break;
            case "0300":
                time_1.setText("새벽 3시");
                break;
            case "0600":
                time_1.setText("오전 6시");
                break;
            case "0900":
                time_1.setText("오전 9시");
                break;
            case "1200":
                time_1.setText("낮 12시");
                break;
            case "1500":
                time_1.setText("오후 3시");
                break;
            case "1800":
                time_1.setText("오후 6시");
                break;
            case "2100":
                time_1.setText("밤 9시");
                break;
        }

        switch (fcstUnitList.get(0).getSKY_value().replaceAll("\"", "")){           // 2. 하늘 상태 표기
            case "1":
                sky_1.setImageResource(R.drawable.sunny);
                break;
            case "3":
                sky_1.setImageResource(R.drawable.cloudy);
                break;
            case "4":
                sky_1.setImageResource(R.drawable.rainy);
                break;
        }

        temperature_1.setText(fcstUnitList.get(0).getT3H_value().replaceAll("\"", "") + "°C");       // 3. 기온 표기

        rainPossibility_1.setText(fcstUnitList.get(0).getPOP_value().replaceAll("\"", "") + "%");    // 4. 강수확률 표기


        /**
         * 2번 유닛 채우기
         * */
        switch (fcstUnitList.get(1).getFcstTime().replaceAll("\"", "")){            // 1. time 표기
            case "0000":
                time_2.setText("밤 12시");
                break;
            case "0300":
                time_2.setText("새벽 3시");
                break;
            case "0600":
                time_2.setText("오전 6시");
                break;
            case "0900":
                time_2.setText("오전 9시");
                break;
            case "1200":
                time_2.setText("낮 12시");
                break;
            case "1500":
                time_2.setText("오후 3시");
                break;
            case "1800":
                time_2.setText("오후 6시");
                break;
            case "2100":
                time_2.setText("밤 9시");
                break;
        }

        switch (fcstUnitList.get(1).getSKY_value().replaceAll("\"", "")){           // 2. 하늘 상태 표기
            case "1":
                sky_2.setImageResource(R.drawable.sunny);
                break;
            case "3":
                sky_2.setImageResource(R.drawable.cloudy);
                break;
            case "4":
                sky_2.setImageResource(R.drawable.rainy);
                break;
        }

        temperature_2.setText(fcstUnitList.get(1).getT3H_value().replaceAll("\"", "") + "°C");       // 3. 기온 표기

        rainPossibility_2.setText(fcstUnitList.get(1).getPOP_value().replaceAll("\"", "") + "%");    // 4. 강수확률 표기


        /**
         * 3번 유닛 채우기
         * */
        switch (fcstUnitList.get(2).getFcstTime().replaceAll("\"", "")){            // 1. time 표기
            case "0000":
                time_3.setText("밤 12시");
                break;
            case "0300":
                time_3.setText("새벽 3시");
                break;
            case "0600":
                time_3.setText("오전 6시");
                break;
            case "0900":
                time_3.setText("오전 9시");
                break;
            case "1200":
                time_3.setText("낮 12시");
                break;
            case "1500":
                time_3.setText("오후 3시");
                break;
            case "1800":
                time_3.setText("오후 6시");
                break;
            case "2100":
                time_3.setText("밤 9시");
                break;
        }

        switch (fcstUnitList.get(2).getSKY_value().replaceAll("\"", "")){           // 2. 하늘 상태 표기
            case "1":
                sky_3.setImageResource(R.drawable.sunny);
                break;
            case "3":
                sky_3.setImageResource(R.drawable.cloudy);
                break;
            case "4":
                sky_3.setImageResource(R.drawable.rainy);
                break;
        }

        temperature_3.setText(fcstUnitList.get(2).getT3H_value().replaceAll("\"", "") + "°C");       // 3. 기온 표기

        rainPossibility_3.setText(fcstUnitList.get(2).getPOP_value().replaceAll("\"", "") + "%");    // 4. 강수확률 표기


        /**
         * 4번 유닛 채우기
         * */
        switch (fcstUnitList.get(3).getFcstTime().replaceAll("\"", "")){            // 1. time 표기
            case "0000":
                time_4.setText("밤 12시");
                break;
            case "0300":
                time_4.setText("새벽 3시");
                break;
            case "0600":
                time_4.setText("오전 6시");
                break;
            case "0900":
                time_4.setText("오전 9시");
                break;
            case "1200":
                time_4.setText("낮 12시");
                break;
            case "1500":
                time_4.setText("오후 3시");
                break;
            case "1800":
                time_4.setText("오후 6시");
                break;
            case "2100":
                time_4.setText("밤 9시");
                break;
        }

        switch (fcstUnitList.get(3).getSKY_value().replaceAll("\"", "")){           // 2. 하늘 상태 표기
            case "1":
                sky_4.setImageResource(R.drawable.sunny);
                break;
            case "3":
                sky_4.setImageResource(R.drawable.cloudy);
                break;
            case "4":
                sky_4.setImageResource(R.drawable.rainy);
                break;
        }

        temperature_4.setText(fcstUnitList.get(3).getT3H_value().replaceAll("\"", "") + "°C");       // 3. 기온 표기

        rainPossibility_4.setText(fcstUnitList.get(3).getPOP_value().replaceAll("\"", "") + "%");    // 4. 강수확률 표기


        /**
         * 5번 유닛 채우기
         * */
        switch (fcstUnitList.get(4).getFcstTime().replaceAll("\"", "")){            // 1. time 표기
            case "0000":
                time_5.setText("밤 12시");
                break;
            case "0300":
                time_5.setText("새벽 3시");
                break;
            case "0600":
                time_5.setText("오전 6시");
                break;
            case "0900":
                time_5.setText("오전 9시");
                break;
            case "1200":
                time_5.setText("낮 12시");
                break;
            case "1500":
                time_5.setText("오후 3시");
                break;
            case "1800":
                time_5.setText("오후 6시");
                break;
            case "2100":
                time_5.setText("밤 9시");
                break;
        }

        switch (fcstUnitList.get(4).getSKY_value().replaceAll("\"", "")){           // 2. 하늘 상태 표기
            case "1":
                sky_5.setImageResource(R.drawable.sunny);
                break;
            case "3":
                sky_5.setImageResource(R.drawable.cloudy);
                break;
            case "4":
                sky_5.setImageResource(R.drawable.rainy);
                break;
        }

        temperature_5.setText(fcstUnitList.get(4).getT3H_value().replaceAll("\"", "") + "°C");       // 3. 기온 표기

        rainPossibility_5.setText(fcstUnitList.get(4).getPOP_value().replaceAll("\"", "") + "%");    // 4. 강수확률 표기


        /**
         * 6번 유닛 채우기
         * */
        switch (fcstUnitList.get(5).getFcstTime().replaceAll("\"", "")){            // 1. time 표기
            case "0000":
                time_6.setText("밤 12시");
                break;
            case "0300":
                time_6.setText("새벽 3시");
                break;
            case "0600":
                time_6.setText("오전 6시");
                break;
            case "0900":
                time_6.setText("오전 9시");
                break;
            case "1200":
                time_6.setText("낮 12시");
                break;
            case "1500":
                time_6.setText("오후 3시");
                break;
            case "1800":
                time_6.setText("오후 6시");
                break;
            case "2100":
                time_6.setText("밤 9시");
                break;
        }

        switch (fcstUnitList.get(5).getSKY_value().replaceAll("\"", "")){           // 2. 하늘 상태 표기
            case "1":
                sky_6.setImageResource(R.drawable.sunny);
                break;
            case "3":
                sky_6.setImageResource(R.drawable.cloudy);
                break;
            case "4":
                sky_6.setImageResource(R.drawable.rainy);
                break;
        }

        temperature_6.setText(fcstUnitList.get(5).getT3H_value().replaceAll("\"", "") + "°C");       // 3. 기온 표기

        rainPossibility_6.setText(fcstUnitList.get(5).getPOP_value().replaceAll("\"", "") + "%");    // 4. 강수확률 표기

        /**
         * 7번 유닛 채우기
         * */
        switch (fcstUnitList.get(6).getFcstTime().replaceAll("\"", "")){            // 1. time 표기
            case "0000":
                time_7.setText("밤 12시");
                break;
            case "0300":
                time_7.setText("새벽 3시");
                break;
            case "0600":
                time_7.setText("오전 6시");
                break;
            case "0900":
                time_7.setText("오전 9시");
                break;
            case "1200":
                time_7.setText("낮 12시");
                break;
            case "1500":
                time_7.setText("오후 3시");
                break;
            case "1800":
                time_7.setText("오후 6시");
                break;
            case "2100":
                time_7.setText("밤 9시");
                break;
        }

        switch (fcstUnitList.get(6).getSKY_value().replaceAll("\"", "")){           // 2. 하늘 상태 표기
            case "1":
                sky_7.setImageResource(R.drawable.sunny);
                break;
            case "3":
                sky_7.setImageResource(R.drawable.cloudy);
                break;
            case "4":
                sky_7.setImageResource(R.drawable.rainy);
                break;
        }

        temperature_7.setText(fcstUnitList.get(6).getT3H_value().replaceAll("\"", "") + "°C");       // 3. 기온 표기

        rainPossibility_7.setText(fcstUnitList.get(6).getPOP_value().replaceAll("\"", "") + "%");    // 4. 강수확률 표기


        /**
         * 8번 유닛 채우기
         * */
        switch (fcstUnitList.get(7).getFcstTime().replaceAll("\"", "")){            // 1. time 표기
            case "0000":
                time_8.setText("밤 12시");
                break;
            case "0300":
                time_8.setText("새벽 3시");
                break;
            case "0600":
                time_8.setText("오전 6시");
                break;
            case "0900":
                time_8.setText("오전 9시");
                break;
            case "1200":
                time_8.setText("낮 12시");
                break;
            case "1500":
                time_8.setText("오후 3시");
                break;
            case "1800":
                time_8.setText("오후 6시");
                break;
            case "2100":
                time_8.setText("밤 9시");
                break;
        }

        switch (fcstUnitList.get(7).getSKY_value().replaceAll("\"", "")){           // 2. 하늘 상태 표기
            case "1":
                sky_8.setImageResource(R.drawable.sunny);
                break;
            case "3":
                sky_8.setImageResource(R.drawable.cloudy);
                break;
            case "4":
                sky_8.setImageResource(R.drawable.rainy);
                break;
        }

        temperature_8.setText(fcstUnitList.get(7).getT3H_value().replaceAll("\"", "") + "°C");       // 3. 기온 표기

        rainPossibility_8.setText(fcstUnitList.get(7).getPOP_value().replaceAll("\"", "") + "%");    // 4. 강수확률 표기


    }
}