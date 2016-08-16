package com.example.rajatjain.multicasttestapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.rajatjain.multicasttestapp.R;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Sender extends Fragment implements View.OnClickListener{

    Button packet50, packet100, packet500;
    TextView stats;
    View view;
    public Sender() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/
        view=inflater.inflate(R.layout.fragment_sender, container, false);
        packet50 = (Button) view.findViewById(R.id.packet50);
        packet100 = (Button) view.findViewById(R.id.packet100);
        packet500 = (Button) view.findViewById(R.id.packet500);
        stats = (TextView) view.findViewById(R.id.status);
        packet50.setOnClickListener(this);
        packet100.setOnClickListener(this);
        packet500.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.packet50:
              /*  AsyncTask.execute(new Runnable() {*/

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        SendPackets(50);

                        return;
                    }
                });

                break;
            case R.id.packet100:
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        SendPackets(100);

                        return;
                    }
                });
                break;
            case R.id.packet500:
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        SendPackets(500);
                        return;
                    }
                });
                break;
        }
    }


    private void SendPackets(int i) {
        Context c= view.getContext();
        takeWifi(c,true);
        try {
            InetAddress group = InetAddress.getByName("228.5.6.7");
            MulticastSocket s = new MulticastSocket(6789);
            s.joinGroup(group);
            int count = 0;
            //String stem = "BITS Test Packet- ";
            String msg;
            Log.d("Kush","Before while loop");
            int t=i;
            while (true) {
                //Scanner st= new Scanner(System.in);
                //msg= st.nextLine();
                long unixTime = System.currentTimeMillis();

                count += 1;
                msg = unixTime + "," + count;
                if (count == i)
                    msg = "STOP";
                DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);
                s.send(hi);

                //Toast.makeText(rootView.getContext(),"Sent: "+ msg, LENGTH_SHORT).show();
                final String finalMsg = msg;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stats.setText("Sending: "+ finalMsg);

                    }
                });
                //
                Log.d("Kush","Sending" + msg);
                if (count == i) {
                    s.leaveGroup(group);
                    break;
                }
                Thread.sleep(1000);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        takeWifi(c,false);
    }

    public void takeWifi(Context c, Boolean what) {
        WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock lock = null;
        if (wifi != null) {
            if (what) {
                lock = wifi.createMulticastLock("Log_Tag");
                lock.acquire();
            } else if(lock!=null) {
                lock.release();
            }
        }
    }


}

