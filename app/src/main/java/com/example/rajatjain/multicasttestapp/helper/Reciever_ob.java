package com.example.rajatjain.multicasttestapp.helper;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.rajatjain.multicasttestapp.Interface.Communicate;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by Rajat Jain on 26-08-2016.
 */
public class Reciever_ob {

    Context context;
    Activity activity;
    Communicate communicate;
    String mipadd,mport;
    int packetcount=0;
    public Reciever_ob(Context context, Activity activity,String mipadd,String mport){
        this.context=context;
        this.activity=activity;
        this.communicate=(Communicate)activity;
        this.mipadd=mipadd;
        this.mport=mport;
    }
    public void Test(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);



                try {
                    Context c = context;
                    takeWifi(c, true);
                    String mip = mipadd;
                    String port = mport;
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
                            socket.leaveGroup(group);
                            break;

                        }

                        //Initially just for simplicity
                        final String tempString=received;
                        activity.runOnUiThread(new Runnable() {
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

        String str1="Packet Received: " + packetcount;
        String str2="Received " + recvd;
        if (recvd.equals("STOP")){
            String str="Tune out. Final Packet Received: " + packetcount;
            communicate.respond(str);
        }else {
            communicate.respond(str1+" "+str2);
        }

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
