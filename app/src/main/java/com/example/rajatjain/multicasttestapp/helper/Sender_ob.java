package com.example.rajatjain.multicasttestapp.helper;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.example.rajatjain.multicasttestapp.Interface.Communicate;
import com.example.rajatjain.multicasttestapp.R;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by Rajat Jain on 26-08-2016.
 */
public class Sender_ob {

    Context context;
    Activity activity;
    Communicate communicate;
    public Sender_ob(Context context, Activity activity){
        this.context=context;
        this.activity=activity;
        this.communicate=(Communicate)activity;
    }
    public void Test(final int testcase ){
            AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                if(testcase==0)
                    SendPackets(200, (float) 0.5); //200*0.5 = 100 Seconds
                else if(testcase==1)
                    SendPackets(1000, (float) 0.1); //1000*0.1= 100 Seconds
                else
                    SendPackets(2000, (float) 0.01); //2000*0.01= 20 Seconds but 2000 packets already reached
            }
            });

            /*   switch (testcase) {
            case 0:
               // AsyncTask.execute(new Runnable() {

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        SendPackets(50);

                        return;
                    }
                });

                break;
            case 1:
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        SendPackets(100);

                        return;
                    }
                });
                break;
            case 2:
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                        SendPackets(500);
                        return;
                    }
                });
                break;
        }*/

    }
    private void SendPackets(int packets, float timeInterval) {
        Context c= context;
        takeWifi(c,true);
        try {
            InetAddress group = InetAddress.getByName("228.5.6.7");
            MulticastSocket s = new MulticastSocket(6789);
            s.joinGroup(group);
            int count = 0;
            //String stem = "BITS Test Packet- ";
            String msg;
            Log.d("Kush","Before while loop");
            int t=packets;
            while (true) {
                //Scanner st= new Scanner(System.in);
                //msg= st.nextLine();
                long unixTime = System.currentTimeMillis();

                count += 1;
                msg = unixTime + "," + count;
                if (count == packets)
                    msg = "STOP";
                DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);
                s.send(hi);

                //Toast.makeText(rootView.getContext(),"Sent: "+ msg, LENGTH_SHORT).show();
                final String finalMsg = msg;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*stats.setText("Sending: "+ finalMsg);*/
                        String str="Sending: "+ finalMsg;
                        communicate.respond(str);

                    }
                });

                Log.d("Kush","Sending" + msg);
                if (count == packets) {
                    s.leaveGroup(group);
                    break;
                }
                Thread.sleep((long) (timeInterval*1000));
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
