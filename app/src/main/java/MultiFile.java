import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.io.FileWriter;

public class MultiFile implements Runnable {
    Thread t;
    String parseMeta[];
    String workingDir = System.getProperty("user.dir");
    static float arr[][] = new float[10][10]; //Number of packets, time delay between successive packets

    //TODO :This is the method to autopopulate the test case 2D array as declared above
    public static void autopopulate(int minPackets, float maxInterval) //interval in milliseconds
    {
        for (int i = 0; i < 10; i++) {
            arr[i][0] = minPackets;
            arr[i][1] = maxInterval;
            minPackets += 50;
            maxInterval -= 100;
        }
    }

    public void run() {
        try {
            //  System.out.println("Waiting for TCP Test Connection ping\n");

            ServerSocket ss = new ServerSocket(3334);
            while (true) {
                System.out.println("Waiting for TCP Test Connection ping");
                String str = "", str2 = "";
                Socket socket = ss.accept();
                DataInputStream din = new DataInputStream(socket.getInputStream());
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

                //Test case number, sender->0 receiver->1, manual test case info(optional)

                str = din.readUTF();
                System.out.println("\nConnection Established");
                System.out.println("client says: " + str);
                //App sends test-connection#...#...#..
                if (str.startsWith("test-connection")) {
                    parseMeta = str.split("#");

                    str2 = "ack";
                    //We send back "ack" to tell the mobile device that connection has been established
                    dout.writeUTF(str2);
                    dout.flush();
                    continue;
                }
                //The run-test part runs when send testcase and run button is pressed on the app
                else if (str.startsWith("run-test")) {
                    //Parse the testcase metadata
                    String testLabels[] = str.split(" ");
                    double ins = Double.parseDouble(testLabels[1]);
                    int testcaseno = (int) ins;

                    int testFlavor = ((int) (ins * 10)) % 10;
                    String tF = (testFlavor == 0) ? "Sender" : "Receiver";
                    //Print the testcase metadata
                    System.out.println("Test Case Information:\n1. Test case number: " + testcaseno + "\n2. Number of Packets: " + arr[testcaseno - 1][0] + "\n3. Time Interval between packets: " + arr
                            [testcaseno - 1][1] + "\n4. Test Case Flavor: " + tF + "\nPress any button to start: ");
                    executeTest(testcaseno, testFlavor);
                    din.close();
                    socket.close();
                    //System.out.println("Jay Ambe Maa!");
                    if (testFlavor == 0) {
                        Socket cket = ss.accept();
                        DataInputStream dhin = new DataInputStream(cket.getInputStream());
                        String allinfo = dhin.readUTF();

                        dhin.close();
                        cket.close();
                        String csvFile = workingDir + "/" + parseMeta[3] + "-Receiver" + "-TotalPackets" + arr[testcaseno - 1][0] + "-Interval" + (arr[testcaseno - 1][1]) / 1000.0 + "seconds.csv";
                        FileWriter writer = new FileWriter(csvFile);
                        writer.append("Packet Time, Packet Count, System Time, Delay(ms), Delay(sec)\n");
                        String tuples[];
                        tuples = allinfo.split(" ");
                        System.out.println(tuples[0]);
                        for (String mesg : tuples) {

                            writer.append(mesg + "\n");
                        }
                        writer.flush();
                        writer.close();
                    }
                    Thread.currentThread().setName("tester");
                    continue;

                } else if (str.startsWith("terminate")) {
                    ss.close();
                    return;
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void executeTest(int testcaseno, int sendOrReceive) {
        try {
            //Waiting for keystroke to start execution
            //Scanner sta= new Scanner(System.in);
            //String garbage= sta.nextLine();
            int tp = 5;
            System.out.println("Going for execution in: \n5");
            while (tp > 0) {
                Thread.sleep(1000);
                System.out.println(--tp);
            }
            if (sendOrReceive == 0) {
                Thread.currentThread().setName("sender");
            } else {
                Thread.currentThread().setName("receiver");
            }

            InetAddress group = InetAddress.getByName("228.5.6.7");
            MulticastSocket s = new MulticastSocket(6789);
            s.joinGroup(group);
            if ((Thread.currentThread().getName()).equals("sender")) {

                String msg = "";
                int count = 0;
                while (true) {
                    Date date = new Date();
                    long t = date.getTime();
                    count += 1;
                    msg = t + "," + count;
                    if (count == (int) arr[testcaseno - 1][0])
                        msg = "STOP";
                    DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, 6789);
                    s.send(hi);
                    System.out.println(msg);
                /*if((msg.toLowerCase()).equals("stop"))
                {
                s.leaveGroup(group);
                break;
            }
            */
                    if (count == (int) arr[testcaseno - 1][0]) {
                        s.leaveGroup(group);
                        break;
                    }
                    Thread.sleep((int) arr[testcaseno - 1][1]);
                }
            } else {
                //Dlink 605L
                String csvFile = workingDir + "/" + parseMeta[3] + "-Sender" + "-TotalPackets" + arr[testcaseno - 1][0] + "-Interval" + (arr[testcaseno - 1][1]) / 1000.0 + "seconds.csv";
                FileWriter writer = new FileWriter(csvFile);

                writer.append("Packet Time, Packet Count, System Time, Delay(ms), Delay(sec)\n");
                while (true) {
                    byte[] buf = new byte[1000];
                    DatagramPacket recv = new DatagramPacket(buf, buf.length);
                    s.receive(recv);
                    String received = new String(recv.getData(), 0, recv.getLength());
                    Date date = new Date();
                    long t = date.getTime();
                    String msg = received + "," + t + "\n";
                    writer.append(msg);

                    if (!((received.toLowerCase()).equals("stop")))
                        System.out.println(msg);

                    else {
                        s.leaveGroup(group);
                        break;
                    }


                }
                writer.flush();
                writer.close();


            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {   //TODO: This is the call to the autopopulate function to populate the array
        autopopulate(50, 1000);
        Thread sender = new Thread(new MultiFile());
        sender.start();

    }
}
