package com.example.cloudkiosk;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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

        // 장바구니
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.commit();
//        gridLayout = findViewById(R.id.gridlayout);

        // 크로노미터
        final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.start();


    }

    // 메뉴 클릭시 팝업
    public void mOnPopupClick(View v){

        // 클릭한 카드뷰에 따라 팝업 결정
        switch (v.getId()){
            case R.id.cardview1:
                text = "비프버거";
                price = price + 4000;
                break;
            case R.id.cardview2:
                text = "불고기버거";
                price = price + 3500;
                break;
            case R.id.cardview3:
                text = "치즈버거";
                price = price + 2500;
                break;
            case R.id.cardview4:
                text = "프렌치프라이";
                price = price + 1500;
                break;
            case R.id.cardview5:
                text = "치킨너겟";
                price = price + 2000;
                break;
            case R.id.cardview6:
                text = "샐러드";
                price = price + 2000;
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
                //데이터 받기
                String result = data.getStringExtra("result");
                txtResult.setText(result);
            }
        }
    }

}