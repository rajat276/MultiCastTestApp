package com.example.rajatjain.multicasttestapp.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.rajatjain.multicasttestapp.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientFragment extends Fragment implements View.OnClickListener {

    private Socket socket;
    private static final int SERVERPORT = 3333;
    private static final String SERVER_IP = "192.168.1.207";
    EditText et;
    Button send;
    public ClientFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new ClientThread()).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_client, container, false);
        et = (EditText) view.findViewById(R.id.EditText01);
        send=(Button)view.findViewById(R.id.myButton);
        send.setOnClickListener(this);
        return view;
    }
    public void onClick(View view) {
        try {

            String str = et.getText().toString();
            /*PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            out.println(str);
            */
            DataOutputStream dataOutputStream= new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(str);
            dataOutputStream.flush();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

                socket = new Socket(serverAddr, SERVERPORT);

            } catch (UnknownHostException e1) {
                Log.e("Unknown Host",e1.toString());
            } catch (IOException e1) {
                Log.e("Ioexception",e1.toString());
                e1.printStackTrace();
            }

        }

    }

}
