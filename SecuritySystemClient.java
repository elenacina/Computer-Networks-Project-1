import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.lang.*;


public class SecuritySystemClient {
    public static void main(String args[]) throws IOException {
        int snapShotNumber = 1;
        int port = Integer.parseInt(args[0]);
        int type;
        String username = args[1];
        String password = args[2];
        String data = username + ":" + password;
        boolean exit = false;

        Socket client = new Socket("localhost", port);

        DataOutputStream outToServer = new DataOutputStream(client.getOutputStream());
        DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(client.getInputStream()));//data coming from server

        //first we send the first byte which in case of authorize is 0
        outToServer.writeByte(0);


        //now we pass the actual date which is username and password
        //first convert them to US-ASCII standard
        byte[] us = data.getBytes(StandardCharsets.US_ASCII);
        int length = data.length();
        outToServer.writeShort(length); // data size
        outToServer.write(us); //data send
        outToServer.flush();

        //getting response from server
        type = inFromServer.readByte();
        length = inFromServer.readShort() & 0xffff ; // maskinbg the length to make the integer short type

        if (type == 2) {
            System.out.println("Authenication Successfull");
            while (exit == false)
            {
                type = inFromServer.readByte();
                length = inFromServer.readShort() & 0xffff;

                if (type == 1) {
                    int type2;
                    //sending keep alive message back to the server
                    byte sendType = 1;
                    short sendLen = 0;
                    outToServer.writeByte(sendType);

                    try { Thread.sleep(100);}
                    catch(Exception e){}
                    outToServer.writeShort(sendLen);

                    outToServer.flush(); //remove previous write value

                    //waitiing for the server to send client
                    type2 = inFromServer.readByte();
                    length = inFromServer.readShort() & 0xffff;

                    if (type2 == 2)
                    {
                        System.out.println("Recieved OK");
                    }
                    else if (type2 == 3)
                    {
                        System.out.println("Invalid");
                    }

                } else if (type == 4) {
                    int type2;

                    byte[] screenShot = new byte[length];
                    inFromServer.read(screenShot , 0 , length);

                    String snapShotName = "snapshot_" + snapShotNumber + ".txt";
                    snapShotNumber++;

                    BufferedWriter outputWriter = new BufferedWriter(new FileWriter(snapShotName));
                    for(int i = 0 ; i < length ; i++) {
                        outputWriter.write(screenShot[i]);
                    }
                    outputWriter.flush();

                    Scanner reader = new Scanner(System.in);  // Reading from System.in
                    System.out.println("\n----------------------\nEmergency message received. Enter 1 to ring the alarm, enter 2 to discard: ");
                    int input = reader.nextInt();
                    if (input ==1){//if the user entered 1 -->ALARM MESSAGE
                        outToServer.writeByte(5);
                        outToServer.writeShort(0);
                        System.out.println("Alarm message sent.");

                        outToServer.flush();

                        type2 = inFromServer.readByte();
                        length = inFromServer.readShort() & 0xffff;
                        if(type2 == 2)
                        {
                            System.out.println("Recieved OK");
                        }
                        else  if(type2 == 3)
                        {
                            System.out.println("Invalid sending alarm message");
                        }
                    }
                    else if (input == 2) {//if user enters 2 --> DISCARD MESSAGE
                        outToServer.writeByte(6);
                        outToServer.writeShort(0);
                        System.out.println("Discard message sent.");

                        outToServer.flush();

                        type2 = inFromServer.readByte();
                        length = inFromServer.readShort() & 0xffff;
                        if(type2 == 2)
                        {
                            System.out.println("Recieved OK");
                        }
                        else if(type2 == 3)
                        {
                            System.out.println("Invalid sending discard message");
                        }
                    }
                }
                else if (type == 7) {
                    System.out.println("EXITING NOW");
                    exit = true;
                }
                else {
                }
            }
        } else if (type == 3) {
            System.out.println("Invalid");
        }

    }

}
