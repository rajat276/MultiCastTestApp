package com.example.rajatjain.multicasttestapp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.rajatjain.multicasttestapp.Interface.Communicate;
import com.example.rajatjain.multicasttestapp.helper.Client_ob;
import com.example.rajatjain.multicasttestapp.helper.Reciever_ob;
import com.example.rajatjain.multicasttestapp.helper.Sender_ob;

import java.util.Date;

/**
 * Created by Rajat Jain on 26-08-2016.
 */
public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener,Communicate, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Spinner spinner;
    int testnumber;
    ArrayAdapter<CharSequence> adapter;
    TextView DebugText,info;
    Button run,testconn,sendTestcase;
    EditText multicastadd,multicastport,ipadd,ipport;
    ToggleButton toggle;
    int isServer=0;
    String address;
    Date buildDate;
    int versioncode;
    Client_ob ob4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        multicastadd=(EditText)findViewById(R.id.etmulticastadd);
        multicastport=(EditText)findViewById(R.id.etmulticastport);
        ipadd=(EditText)findViewById(R.id.etsereverip);
        ipport=(EditText)findViewById(R.id.etserverport);
        spinner = (Spinner) findViewById(R.id.spinner_testcase);
        toggle=(ToggleButton) findViewById(R.id.togglestate);
        toggle.setOnCheckedChangeListener(this);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Testcase_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        DebugText=(TextView)findViewById(R.id.debug_text);
        buildDate = new Date(BuildConfig.TIMESTAMP);//build date
        versioncode=BuildConfig.VERSION_CODE;//version code
        info=(TextView)findViewById(R.id.tvinfo);
        info.setText("BUILD DATE: "+buildDate+"    VERSION CODE: "+versioncode);
        run=(Button)findViewById(R.id.brun);
        testconn=(Button)findViewById(R.id.btest);
        sendTestcase=(Button)findViewById(R.id.bsendtestcase);
        testconn.setOnClickListener(this);
        sendTestcase.setOnClickListener(this);
        run.setOnClickListener(this);
        DebugText.setText("on Start");
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        address = info.getMacAddress();//device mac
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        Toast.makeText(this,adapterView.getItemAtPosition(pos).toString(),Toast.LENGTH_LONG).show();
        testnumber=pos;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void respond(String str) {
        String s=str+"\n"+DebugText.getText().toString();
        DebugText.setText(s);
    }

    @Override
    public void excelrespond(String str) {
        ob4.Send(str);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.brun:
                //Reciever_ob dc= new Reciever_ob();
                //String dat= dc.sendDataCollector();
                /*if(isServer==1) {
                    String str = "Started as Server" + "\n" + DebugText.getText().toString();
                    DebugText.setText(str);
                    Sender_ob ob1 = new Sender_ob(this, MainActivity2.this);
                    ob1.Test(testnumber);
                }else {
                    String str = "Started as Client" + "\n" + DebugText.getText().toString();
                    DebugText.setText(str);
                    Reciever_ob ob2 = new Reciever_ob(this, MainActivity2.this,multicastadd.getText().toString(),multicastport.getText().toString());
                    ob2.Test();

                }*/
                break;
            case R.id.btest:
                String str = "Test connection" + "\n" + DebugText.getText().toString();
                DebugText.setText(str);
                Date d = new Date();
                CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());//date of request
                String device= Build.MODEL;//device model
                int port=Integer.parseInt(ipport.getText().toString());
                Client_ob ob3=new Client_ob(ipadd.getText().toString(),port,MainActivity2.this);
                ob3.Send("test-connection#"+address+"#"+s+"#"+device);
                ob3.recieve();
                //String recv ="string recieved: " + "\n" + DebugText.getText().toString();
                //DebugText.setText(recv);
                break;
            case R.id.bsendtestcase:
                String str2 = "Sending test case data" + "\n" + DebugText.getText().toString();
                DebugText.setText(str2);
                int port2=Integer.parseInt(ipport.getText().toString());
                ob4=new Client_ob(ipadd.getText().toString(),port2,MainActivity2.this);
                int test=testnumber+1;
                ob4.Send("run-test "+test+"."+isServer);//sender:1 reciver=0
                String send = test+"."+isServer + "\n" + DebugText.getText().toString();
                DebugText.setText("String sent: "+send);
                int tp=5;
                DebugText.setText("Going for execution in: 5 seconds ");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(isServer==1) {
                    String str3 = "Started as Server" + "\n" + DebugText.getText().toString();
                    DebugText.setText(str3);
                    Sender_ob ob1 = new Sender_ob(this, MainActivity2.this);
                    ob1.Test(testnumber);
                }else {
                    String str3 = "Started as Client" + "\n" + DebugText.getText().toString();
                    DebugText.setText(str3);
                    Reciever_ob ob2 = new Reciever_ob(this, MainActivity2.this,multicastadd.getText().toString(),multicastport.getText().toString());
                    ob2.Test();

                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            // The toggle is enabled
            isServer=1;
            String str = "isServer="+isServer + "\n" + DebugText.getText().toString();
            DebugText.setText(str);
        } else {
            // The toggle is disabled
            isServer=0;
            String str = "isServer="+isServer + "\n" + DebugText.getText().toString();
            DebugText.setText(str);
        }
    }
}
