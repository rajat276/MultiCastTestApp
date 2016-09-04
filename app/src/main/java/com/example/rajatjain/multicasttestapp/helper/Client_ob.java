package com.example.rajatjain.multicasttestapp.helper;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.rajatjain.multicasttestapp.Interface.Communicate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Rajat Jain on 26-08-2016.
 */
public class Client_ob {
    private static final int SERVERPORT = 3333;
    private static final String SERVER_IP = "192.168.1.207";
    String ip;
    int port;
    Activity activity;
    Communicate communicate;
    private Socket socket;
    public Client_ob(String ip, int port, Activity activity){
        this.ip=ip;
        this.port=port;
        this.activity=activity;
        this.communicate=(Communicate)activity;
    }
    public void Send(String str){
       // new Thread(new ClientThread()).start();
        final String code=str;


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                InetAddress serverAddr = null;
                try {
                    serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port );
                    Log.d("me",ip);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            communicate.respond("connected to ip:"+ip);
                        }
                    });

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {

                    String str = code;
            /*PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            out.println(str);
            */
                    Log.e("sending",code);

                    DataOutputStream dataOutputStream= new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF(str);
                    dataOutputStream.flush();
                    socket.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }
        });

    }
    public void recieve(){
        final String[] str = {"null"};
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                try {
                    DataInputStream din=new DataInputStream(socket.getInputStream());
                    final String str=din.readUTF();
                    Log.e("str",str);
                    if(str.equals("ack")){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                communicate.respond("string recieved: "+str);
                            }
                        });
                    }

                    din.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        });

    }
    /*class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                Log.d("me",ip);
                socket = new Socket(serverAddr, SERVERPORT);


            } catch (UnknownHostException e1) {
                Log.e("Unknown Host",e1.toString());
            } catch (IOException e1) {
                Log.e("Ioexception",e1.toString());
                e1.printStackTrace();
            }

        }

    }*/

}
