package com.ishland.android.httpserviceattacker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ishland.app.HTTPServiceAttacker.attacker.Attack;
import com.ishland.app.HTTPServiceAttacker.attacker.threads.MonitorThread;
import com.ishland.app.HTTPServiceAttacker.configuration.Configuration;
import com.ishland.app.HTTPServiceAttacker.manager.WSContent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static boolean isStarted = false;
    private static Configuration config = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            initLog();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MonitorThread.registerCallback(() -> {
            WSContent wsContent = MonitorThread.wsContent;
            TextView status = findViewById(R.id.textView3);
            String text = "";
            text += "Total: " + wsContent.successcount.longValue()
                    + wsContent.failurecount.longValue() + wsContent.errored.longValue() + "\n";
            text += "Success info: " + wsContent.success.toString() + "\n";
            text += "Failure info: " + wsContent.failure.toString() + " + " + wsContent.errored.toString()
                    + " exceptions" + "\n";
            text += "RPS: " + wsContent.vaildRPS + "/" + wsContent.totalRPS + "\n";
            text += "RPM: " + wsContent.vaildRPS.longValue() * 60 + "/"
                    + wsContent.totalRPS.longValue() * 60 + "\n";
            text += "Queued requests: " + wsContent.createdConnections + "/"
                    + wsContent.maxAllowedConnections + "\n";
            text += "Memory usage: " + wsContent.usedHeap.longValue() / 1024 / 1024 + "MB / "
                    + wsContent.allocatedHeap.longValue() / 1024 / 1024 + "MB / "
                    + wsContent.maxHeap.longValue() / 1024 / 1024 + "MB";
            status.setText(text);
            return null;
        });
    }

    public void onConfigurationButton(View view) {
        Intent intent = new Intent();
        intent.setClass(this, ConfigurationActivity.class);
        startActivity(intent);
    }

    public void onAttackButton(View view) {
        config = new Configuration(new File(getApplicationContext().getFilesDir() + "/config.yml"));
        Attack.setConfig(config);
        Attack.maxConnectionPerThread = 960;
        if (!isStarted) {
            ((Button) findViewById(R.id.attackButton)).setText("开始攻击  Starting attack...");
            findViewById(R.id.attackButton).setEnabled(false);
            isStarted = true;
            new Handler().postDelayed(() -> {
                try {
                    Attack.startAttack();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                new Handler().postDelayed(() -> {
                    ((Button) findViewById(R.id.attackButton)).setText("停止攻击  Stop attack");
                    findViewById(R.id.attackButton).setEnabled(true);
                }, 1000);
            }, 100);
        } else {
            ((Button) findViewById(R.id.attackButton)).setText("停止攻击 Stopping attack...");
            findViewById(R.id.attackButton).setEnabled(false);
            isStarted = false;
            new Handler().postDelayed(() -> {
                        Attack.stopAttack();
                        new Handler().postDelayed(() -> {
                            ((Button) findViewById(R.id.attackButton)).setText("开始攻击  Start attack");
                            findViewById(R.id.attackButton).setEnabled(true);
                        }, 1000);
                        ((TextView) findViewById(R.id.textView3)).setText("Attack not running");
                    }
                    , 100);
        }
    }

    private void initLog() throws IOException {
        System.out.println(getApplicationContext().getFilesDir());
        InputStream in = getAssets().open("logback.xml");
        File dataDirectory = getApplicationContext().getFilesDir();
        if (!dataDirectory.exists())
            dataDirectory.mkdirs();
        File logbackFile = new File(getApplicationContext().getFilesDir() + "/logback.xml");
        if (!logbackFile.exists())
            logbackFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(logbackFile));
        while (in.available() > 0)
            writer.write(in.read());
        in.close();
        writer.close();
        System.setProperty("log4j.configurationFile", getApplicationContext().getFilesDir() + "/logback.xml");
    }
}
