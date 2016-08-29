package com.example.rajatjain.multicasttestapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.rajatjain.multicasttestapp.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;



public class ServerFragment extends Fragment implements View.OnClickListener {

    private ServerSocket serverSocket;
    Handler updateConversationHandler;
    Thread serverThread = null;
    public TextView text,addr;
    public static final int SERVERPORT =6000;
    Button start;


    public ServerFragment() {
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
        View view=inflater.inflate(R.layout.fragment_server, container, false);
        text = (TextView) view.findViewById(R.id.tvserver);
        addr=(TextView)view.findViewById(R.id.ipnport);
        start=(Button)view.findViewById(R.id.bserverStart);
        start.setOnClickListener(this);
        return view;

    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        updateConversationHandler = new Handler();
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
        Log.e("started","thread start");
    }

    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {

                serverSocket = new ServerSocket(SERVERPORT);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("serever not","erorr.......");
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {

                    socket = serverSocket.accept();
                   /* DataInputStream din=new DataInputStream(socket.getInputStream());
                    BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
                    text.setText(text.getText().toString()+"\n"+din.readUTF());
*/                  CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;
        private DataInputStream din;
        String str;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                this.din=new DataInputStream(this.clientSocket.getInputStream());
                str=din.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = str;

                    updateConversationHandler.post(new updateUIThread(read));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            String address= null;
            try {
                address = "id:"+ InetAddress.getLocalHost()+"   port: 8080";
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            addr.setText(address);
            text.setText(text.getText().toString()+"Client Says: "+ msg + "\n");
        }
    }
}




