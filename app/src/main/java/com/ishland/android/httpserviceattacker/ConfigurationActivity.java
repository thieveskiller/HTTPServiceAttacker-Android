package com.ishland.android.httpserviceattacker;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.esotericsoftware.yamlbeans.YamlWriter;
import com.ishland.app.HTTPServiceAttacker.configuration.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        Configuration config = new Configuration(new File(getApplicationContext().getFilesDir() + "/config.yml"));
        Map<String, Object> conf = config.getTarget().get(0);
        ((TextView) findViewById(R.id.target)).setText(String.valueOf(conf.get("addr")));
        ((TextView) findViewById(R.id.threads)).setText(String.valueOf(conf.get("threads")));
        ((Switch) findViewById(R.id.mode)).setChecked(Boolean.valueOf(String.valueOf(!conf.get("mode").equals("GET"))));
        ((TextView) findViewById(R.id.data)).setText(String.valueOf(conf.get("data")));
        ((TextView) findViewById(R.id.referer)).setText(String.valueOf(conf.get("Referer")));
    }


    public void onSave(View view) throws IOException {
        Configuration config = new Configuration(new File(getApplicationContext().getFilesDir() + "/config.yml"));
        Map<String, Object> conf = config.getTarget().get(0);
        conf.put("addr", ((TextView) findViewById(R.id.target)).getText().toString());
        conf.put("threads", ((TextView) findViewById(R.id.threads)).getText().toString());
        conf.put("mode", ((Switch) findViewById(R.id.mode)).isChecked() ? "POST" : "GET");
        conf.put("data", ((TextView) findViewById(R.id.data)).getText().toString());
        conf.put("Referer", ((TextView) findViewById(R.id.referer)).getText().toString());
        Map<String, Object> mainconf = new HashMap<>();
        mainconf.put("showExceptions", false);
        mainconf.put("target", config.getTarget());
        YamlWriter writer = new YamlWriter(new FileWriter(getApplicationContext().getFilesDir() + "/config.yml"));
        writer.write(mainconf);
        writer.close();
        finish();
    }


}
