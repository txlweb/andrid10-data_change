package com.k.pk;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.NoCopySpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class console extends ComponentActivity {
    public static final Handler mainHandler = new Handler(Looper.getMainLooper());
    static TextView csv = null;
    private Handler handler = new Handler();
    private ValueAnimator colorAnimator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console);
        csv = findViewById(R.id.Console);
        Button btn = findViewById(R.id.button5);
        btn.setOnClickListener(View->{
            try {
                FileOutputStream ot = new FileOutputStream(this.getApplicationContext().getFilesDir().toString()+"/console.log");
                ot.write(csv.getText().toString().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(this, "已经抓取Log信息到文件.", Toast.LENGTH_SHORT).show();
            File file = new File(this.getApplicationContext().getFilesDir().toString()+"/console.log");
            Uri uri = FileProvider.getUriForFile(this, this.getPackageName()+".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "text/plain");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity(intent);
        });
        // 设置起始颜色和结束颜色
        int startColor = Color.RED;
        int endColor = Color.GREEN;

        // 创建ValueAnimator实例，用于在颜色间插值
        colorAnimator = ValueAnimator.ofInt(startColor, endColor);
        colorAnimator.setEvaluator(new ArgbEvaluator()); // 使用ArgbEvaluator进行颜色插值
        colorAnimator.setDuration(3000); // 设置动画持续时间，例如3000毫秒
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 在每次更新时设置TextView的文本颜色
                csv.setTextColor((int) animation.getAnimatedValue());
            }
        });

        // 开始动画
        colorAnimator.start();

        // 如果你想要动画无限循环，可以这样做
         colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        //colorAnimator.setRepeatMode(ValueAnimator.REVERSE); // 或者REVERSE，取决于你想要的循环方式
    }
    // 确保在Activity销毁时停止动画，以避免内存泄漏
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (colorAnimator != null && colorAnimator.isRunning()) {
            colorAnimator.cancel();
        }
    }
}
