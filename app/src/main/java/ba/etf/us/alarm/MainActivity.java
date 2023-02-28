package ba.etf.us.alarm;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    SwitchCompat alarm;
    TextView detekcija;
    MqttAndroidClient mqtt;

   void detekcija()
   {
       Calendar kalendar=Calendar.getInstance();
       DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy HH:mm:ss");
       String datum = dateFormat.format(kalendar.getTime());
       detekcija.setText("Detektovana neželjena aktivnost u: "+datum);
   }

   int b=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarm = (SwitchCompat) findViewById(R.id.swOnOff);
        detekcija=(TextView)findViewById(R.id.textView);
       // SharedPreferences sharedPrefs = getSharedPreferences("ba.etf.us.alarm", MODE_PRIVATE);
        //alarm.setChecked(sharedPrefs.getBoolean("aktiviran", false));

        mqtt = new MqttAndroidClient(getApplicationContext(), "tcp://broker.hivemq.com", MqttClient.generateClientId());

        MqttConnectOptions o = new MqttConnectOptions();
        o.setCleanSession(true);

        mqtt.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i("TAG", "connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.contains("alarmus/aktivacija"))
                detekcija();
                Log.i("TAG", "topic: " + topic + ", msg: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i("TAG", "msg delivered");
            }
        });

        try {
            mqtt.connect(o, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("TAG", "connect succeed");

                    subscribeTopic("alarmus/aktivacija");
                    if(!alarm.isChecked())
                    {
                        try {
                            mqtt.publish("alarmus/password", (new String("password")).getBytes(StandardCharsets.UTF_8),0,false);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        try {
                            mqtt.publish("alarmus/password", (new String("1")).getBytes(StandardCharsets.UTF_8),0,false);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("TAG", "connect failed");
                }
            });


        } catch (MqttException e) {
            e.printStackTrace();
        }
        ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            posalji(intent.getStringExtra("password"));
                        }
                    }
                });
        alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if(!checked)
                { Log.i("moj","goagwe23232323232");
                    mStartForResult.launch(new Intent(getApplicationContext(),MainActivity2.class));
                }
                else
                    try {
                        mqtt.publish("alarmus/password", (new String("1")).getBytes(StandardCharsets.UTF_8),0,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
            }
        });

    }


public void posalji(String t)
{
    Log.i("moj","ne radiiiiiii");
    try {
        mqtt.publish("alarmus/password", t.getBytes(StandardCharsets.UTF_8),0,false);
    } catch (MqttException e) {
        e.printStackTrace();
    }
}
    public void subscribeTopic(String topic) {
        try {
            mqtt.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("TAG", "subscribed succeed");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("TAG", "subscribed failed");
                }
            });

        } catch (MqttException e) {

        }
    }
    @Override
    protected void onDestroy() {
       // SharedPreferences.Editor editor = getSharedPreferences("ba.etf.us.alarm", MODE_PRIVATE).edit();
        //editor.putBoolean("aktiviran",alarm.isChecked());
        //editor.commit();
        super.onDestroy();
    }
}