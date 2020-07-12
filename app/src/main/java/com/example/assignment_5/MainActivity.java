package com.example.assignment_5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Query mQuery;
    private String mQueryDate, mQueryTime;
    private boolean yesterday;

    private String mSelectedCity;

    Spinner spinner;
    Button btn_main_confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initQueryTime();

        initSpinner();

        btn_main_confirm = findViewById(R.id.btn_main_confirm);
        btn_main_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
                intent.putExtra("mQuery", mQuery);
                startActivity(intent);

            }
        });











    }


    public void initQueryTime() {
        yesterday = false;

        String tempTime = getTime();
        int tempTimeInt = Integer.parseInt(tempTime);

        if (0 <= tempTimeInt && tempTimeInt < 200){
            mQueryTime = "2300";
            yesterday = true;                                      // 00시~02시 사이에는 그 전날 23시의 데이터를 사용한다.
        }else if (200 <= tempTimeInt && tempTimeInt < 500){
            mQueryTime = "0200";
        }else if (500 <= tempTimeInt && tempTimeInt < 800){
            mQueryTime = "0500";
        }else if (800 <= tempTimeInt && tempTimeInt < 1100){
            mQueryTime = "0800";
        }else if (1100 <= tempTimeInt && tempTimeInt < 1400){
            mQueryTime = "1100";
        }else if (1400 <= tempTimeInt && tempTimeInt < 1700){
            mQueryTime = "1400";
        }else if (1700 <= tempTimeInt && tempTimeInt < 2000){
            mQueryTime = "1700";
        }else if (2000 <= tempTimeInt && tempTimeInt < 2300){
            mQueryTime = "2000";
        }else if (2300 <= tempTimeInt && tempTimeInt < 2400){
            mQueryTime = "2300";
        }                                                           // 쿼리에 실어 보낼 base_time 골라냄


        String tempDate = getDate();
        int tempDateInt = Integer.parseInt(tempDate);
        if (yesterday){
            tempDateInt--;
        }                                                           // 만약 자정부터 02시 사이면 그 전날 23시로 가야하므로 날짜 하나 줄여줌
        mQueryDate = Integer.toString(tempDateInt);                 // 쿼리에 실어 보낼 base_date 골라냄


        mQuery = new Query();           // 날짜와 시간을 세팅해줄 쿼리 하나를 생성한다.
        mQuery.setTime(mQueryTime);     // 쿼리에 실어 보낼 시간 설정
        mQuery.setDate(mQueryDate);     // 쿼리에 실어 보낼 날짜 설정
    }

    public void initSpinner() {
        spinner = findViewById(R.id.spinner_main_selectLocation);
        ArrayAdapter citiesAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_dropdown_item);
        citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(citiesAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedCity = (String)spinner.getItemAtPosition(position);
                initLocation(mSelectedCity); // 쿼리에 들어갈 nx, ny를 셋팅해주어야 하므로 Spinner에서 지역 선택 직후에 와야 함
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void initLocation(String city) {
        switch (city){
            case "경기도 가평군":
                mQuery.setLocation("가평군");
                mQuery.setNx(69);
                mQuery.setNy(133);
                break;
            case "경기도 고양시":
                mQuery.setLocation("고양시");
                mQuery.setNx(57);
                mQuery.setNy(128);

                break;
            case "경기도 과천시":
                mQuery.setLocation("과천시");
                mQuery.setNx(60);
                mQuery.setNy(124);
                break;
            case "경기도 광명시":
                mQuery.setLocation("광명시");
                mQuery.setNx(58);
                mQuery.setNy(125);
                break;
            case "경기도 광주시":
                mQuery.setLocation("광주시");
                mQuery.setNx(65);
                mQuery.setNy(123);
                break;
            case "경기도 구리시":
                mQuery.setLocation("구리시");
                mQuery.setNx(62);
                mQuery.setNy(127);
                break;
            case "경기도 군포시":
                mQuery.setLocation("군포시");
                mQuery.setNx(59);
                mQuery.setNy(122);
                break;
            case "경기도 김포시":
                mQuery.setLocation("김포시");
                mQuery.setNx(55);
                mQuery.setNy(128);
                break;
            case "경기도 남양주시":
                mQuery.setLocation("남양주시");
                mQuery.setNx(64);
                mQuery.setNy(128);
                break;
            case "경기도 동두천시":
                mQuery.setLocation("동두천시");
                mQuery.setNx(61);
                mQuery.setNy(134);
                break;
            case "경기도 부천시":
                mQuery.setLocation("부천시");
                mQuery.setNx(56);
                mQuery.setNy(125);
                break;
            case "경기도 성남시":
                mQuery.setLocation("성남시");
                mQuery.setNx(65);
                mQuery.setNy(109);
                break;
            case "경기도 수원시":
                mQuery.setLocation("수원시");
                mQuery.setNx(60);
                mQuery.setNy(120);
                break;
            case "경기도 시흥시":
                mQuery.setLocation("시흥시");
                mQuery.setNx(57);
                mQuery.setNy(123);
                break;
            case "경기도 안산시":
                mQuery.setLocation("안산시");
                mQuery.setNx(58);
                mQuery.setNy(121);
                break;
            case "경기도 안성시":
                mQuery.setLocation("안성시");
                mQuery.setNx(65);
                mQuery.setNy(115);
                break;
            case "경기도 안양시":
                mQuery.setLocation("안양시");
                mQuery.setNx(59);
                mQuery.setNy(123);
                break;
            case "경기도 양주시":
                mQuery.setLocation("양주시");
                mQuery.setNx(61);
                mQuery.setNy(131);
                break;
            case "경기도 양평군":
                mQuery.setLocation("양평군");
                mQuery.setNx(69);
                mQuery.setNy(125);
                break;
            case "경기도 여주시":
                mQuery.setLocation("여주시");
                mQuery.setNx(71);
                mQuery.setNy(121);
                break;
            case "경기도 연천군":
                mQuery.setLocation("연천군");
                mQuery.setNx(61);
                mQuery.setNy(138);
                break;
            case "경기도 오산시":
                mQuery.setLocation("오산시");
                mQuery.setNx(62);
                mQuery.setNy(118);
                break;
            case "경기도 용인시":
                mQuery.setLocation("용인시");
                mQuery.setNx(63);
                mQuery.setNy(120);
                break;
            case "경기도 의왕시":
                mQuery.setLocation("의왕시");
                mQuery.setNx(60);
                mQuery.setNy(122);
                break;
            case "경기도 의정부시":
                mQuery.setLocation("의정부시");
                mQuery.setNx(61);
                mQuery.setNy(130);
                break;
            case "경기도 이천시":
                mQuery.setLocation("이천시");
                mQuery.setNx(68);
                mQuery.setNy(121);
                break;
            case "경기도 파주시":
                mQuery.setLocation("파주시");
                mQuery.setNx(56);
                mQuery.setNy(131);
                break;
            case "경기도 평택시":
                mQuery.setLocation("평택시");
                mQuery.setNx(62);
                mQuery.setNy(114);
                break;
            case "경기도 포천시":
                mQuery.setLocation("포천시");
                mQuery.setNx(64);
                mQuery.setNy(134);
                break;
            case "경기도 하남시":
                mQuery.setLocation("하남시");
                mQuery.setNx(64);
                mQuery.setNy(126);
                break;
            case "경기도 화성시":
                mQuery.setLocation("화성시");
                mQuery.setNx(57);
                mQuery.setNy(119);
                break;
            default:
                break;
        }
    }

    public String getDate() {
        long today = System.currentTimeMillis();
        Date date = new Date(today);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }

    public String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm");
        return dateFormat.format(date);
    }


}