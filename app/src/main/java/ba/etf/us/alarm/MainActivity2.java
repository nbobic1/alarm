package ba.etf.us.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.nio.charset.StandardCharsets;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drugi_sl);
        Button button=(Button)findViewById(R.id.button2);
        EditText pass=(EditText)findViewById(R.id.editText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pass.getText().toString().contains("password")) {
                    Intent output = new Intent();
                    output.putExtra("password", pass.getText().toString());
                    setResult(RESULT_OK, output);
                    finish();
                }
            }
        });

    }
}