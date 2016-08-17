package com.example.rajatjain.multicasttestapp.fragment;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;

import com.example.rajatjain.multicasttestapp.R;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Reciever extends Fragment implements View.OnClickListener {
    View view;
    Button submit;
    TextView packetstatus;
    TextView receivedstatus;
    private int packetcount = 0;
    EditText mIpAddress, mPort;
    public Reciever() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_reciever, container, false);


        packetstatus = (TextView) view.findViewById(R.id.packetreceive);
        receivedstatus = (TextView) view.findViewById(R.id.textView3);
        submit = (Button) view.findViewById(R.id.button);
        submit.setOnClickListener(this);
        mIpAddress = (EditText) view.findViewById(R.id.editText);
        mPort = (EditText) view.findViewById(R.id.editText2);
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

        Log.e("clicked","clicked");

      final View temp=view;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);



        try {
            Context c = temp.getContext();
            takeWifi(c, true);
            String mip = mIpAddress.getText().toString();
            String port = mPort.getText().toString();
            InetAddress group = null;
            MulticastSocket socket = null;
            group = InetAddress.getByName(mip);
            socket = new MulticastSocket(Integer.parseInt(port));
            socket.joinGroup(group);

            while (true) {
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                Log.d("VIVZ", "Datagram packet created");
                socket.receive(recv);
                Log.d("VIVZ", "It was recvd");
                String received = new String(recv.getData(), 0, recv.getLength());
                if (received.equals("STOP")) {
                   /* updatePC();
                    updateUI(received);*/
                    break;

                }

                //Initially just for simplicity
                final String tempString=received;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updatePC();
                        updateUI(tempString);
                    }
                });

           }
            takeWifi(c, false);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("VIVZ", String.valueOf(e));
        }

                return;
            }
        });
    }

    public void updateUI(String recvd) {

        packetstatus.setText("Packet Received: " + packetcount);
        receivedstatus.setText("Received " + recvd);
        if (recvd == "STOP")
            packetstatus.setText("Tune out. Final Packet Received: " + packetcount);
    }

    public void updatePC() {

        packetcount += 1;
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
