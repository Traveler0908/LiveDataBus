package com.example.qiwei.livedatabus;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LiveDataBus.get().with("MainActivity", LiveDataBean.class).observe(this, new Observer<LiveDataBean>() {
            @Override
            public void onChanged(@Nullable LiveDataBean huawei) {
                if (huawei != null) {
                    Bean bean = (Bean) huawei.getData();
                    Toast.makeText(MainActivity.this, bean.getName() + "", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void send(View view) {
        Bean bean = new Bean();
        bean.setName("123");
        bean.setType("type");
        LiveDataBean liveDataBean = new LiveDataBean<>("ceshi", bean);
        LiveDataBus.get().with("MainActivity", LiveDataBean.class).postValue(liveDataBean);
    }

}
