package com.example.cloudkiosk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private GridLayout gridLayout;

    TextView txtResult;
    String text;
    int price = 0;

    // 장바구니 리스트뷰
    private ListView listView;
    List<String> data = new ArrayList<>();

    // 서버
    private final String TAG = "MainActivityLog";
    private final String URL = "http://3.35.50.87:3000/";
    private Retrofit retrofit;
    private CRUD service;
    private Button btn_upload;

    // 크로노미터
    Chronometer chronometer;

    String laptime;
    String tot_time, time1, time2, time3 = "00:00";
    String menu1, menu2, menu3 = "";
    TextView lap1, lap2, lap3;
    int lapcount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // no title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        pageAdapter.addFragment(new Fragment1(), "햄버거");
        pageAdapter.addFragment(new Fragment2(), "사이드");
        pageAdapter.addFragment(new Fragment3(), "음료");

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.setAdapter(pageAdapter);

        // 팝업
        txtResult = (TextView) findViewById(R.id.txtResult);

        // 장바구니 프래그먼트
        // gridLayout = findViewById(R.id.gridlayout);

        // 크로노미터
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.start();

        // firstInit
        firstInit();
        //btn_upload.setOnClickListener((View.OnClickListener) this);

    }

    public void firstInit() {
        // 결제하기 버튼
        btn_upload = (Button) findViewById(R.id.button4);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chronometer.stop();
                tot_time = chronometer.getText().toString();
                // 총 경과시간 전송
                Call<ResponseBody> call_post = service.postFunc(tot_time + time1 + time2 + time3 + menu1 + menu2 + menu3);
                call_post.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String result = response.body().string();
                                Log.v(TAG, "result = " + result);
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.v(TAG, "error = " + String.valueOf(response.code()));
                            Toast.makeText(getApplicationContext(), "error = " + String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.v(TAG, "Fail");
                        Toast.makeText(getApplicationContext(), "Response Fail", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(CRUD.class);
    }


    // 메뉴 클릭시 팝업
    public void mOnPopupClick(View v){

        // 클릭한 카드뷰에 따라 팝업 결정
        switch (v.getId()){
            case R.id.cardview1:
                text = "비프버거";
                menu1 = "BIF";
                price = price + 4000;
                break;
            case R.id.cardview2:
                text = "불고기버거";
                menu1 = "BUL";
                price = price + 3500;
                break;
            case R.id.cardview3:
                text = "치즈버거";
                menu1 = "CHE";
                price = price + 2500;
                break;
            case R.id.cardview4:
                text = "프렌치프라이";
                menu2 = "FRE";
                price = price + 1500;
                break;
            case R.id.cardview5:
                text = "치킨너겟";
                menu2 = "CHI";
                price = price + 2000;
                break;
            case R.id.cardview6:
                text = "샐러드";
                menu2 = "SAL";
                price = price + 2000;
                break;
            case R.id.cardview7:
                text = "콜라";
                menu3 = "COK";
                price = price + 1200;
                break;
            case R.id.cardview8:
                text = "사이다";
                menu3 = "CID";
                price = price + 1200;
                break;
            case R.id.cardview9:
                text = "아메리카노";
                menu3 = "AME";
                price = price + 1500;
                break;

        }
        //데이터 담아 팝업액티비티 호출
        Intent intent = new Intent(this, PopupActivity.class);
        intent.putExtra("data", price + "원");

        // 리스트뷰에 메뉴 추가
        listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        data.add(text);
        adapter.notifyDataSetChanged();
        
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // 메뉴 추가
                String result = data.getStringExtra("result");
                txtResult.setText(result);

                chronometer.stop();
                laptime = chronometer.getText().toString();
                lap1 = (TextView) findViewById(R.id.lap1);
                lap2 = (TextView) findViewById(R.id.lap2);
                lap3 = (TextView) findViewById(R.id.lap3);
                switch (lapcount)
                {
                    case 0:
                        lap1.setText(laptime);
                        time1 = laptime;
                        chronometer.start();
                        lapcount = lapcount + 1;
                        break;
                    case 1:
                        lap2.setText(laptime);
                        time2 = laptime;
                        chronometer.start();
                        lapcount = lapcount + 1;
                        break;
                    case 2:
                        lap3.setText(laptime);
                        time3 = laptime;
                        chronometer.start();
                        lapcount = lapcount + 1;
                        break;

                }

            }
        }
    }



}