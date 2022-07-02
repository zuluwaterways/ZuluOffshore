package com.zulu.offshore;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class faq extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        String[] a = getResources().getStringArray(R.array.faq);
        LinearLayout linearLayout = findViewById(R.id.FAQ_Container);
        linearLayout.removeAllViews();
        for(int i=0;i<a.length;i=i+2){
            TextView tq= new TextView(this);
            final TextView ta= new TextView(this);
            tq.setText(a[i]);
            ta.setText(a[i+1]);
            tq.setTextSize(16);
            ta.setTextSize(14);
            tq.setPadding(10,5,5,10);
            ta.setPadding(10,5,5,10);
            tq.setBackgroundResource(R.drawable.spinner_border);
            ta.setBackgroundResource(R.drawable.textbox_border);
            linearLayout.addView(tq);
            linearLayout.addView(ta);
            ta.setVisibility(View.GONE);
            tq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ta.setVisibility(ta.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
                }
            });
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
