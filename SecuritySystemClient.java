import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Elena Cina on 3/11/2018.
 */
public class SecuritySystemClient {
    public static void main( String args[]) throws IOException {

        int port = 1234;//Integer.parseInt(args[0]);
        int type;
        String username = "bilkent";//args[1];
        String password  = "cs421";//args[2];
        String data = username + ":" + password;
        boolean exit = false;
        System.out.println ("whatever");
        try {
            Socket client = new Socket("localhost",port );

            //the output stream is connected to the input stream
           // OutputStream outToServer = client.getOutputStream();

            //send the authentication message
            DataOutputStream outToServer = new DataOutputStream(client.getOutputStream());

            //first we send the first byte which in case of authorize is 0
            outToServer.writeByte(0);
            //then we send data size

            //now we pass the actual date which is username and password
            //first convert them to US-ASCII standard
            byte [] us =  data.getBytes(StandardCharsets.US_ASCII);
            short length = (short) data.length();
            outToServer.writeShort(length);
            outToServer.write(us); //data send

            //getting response from server
            DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(client.getInputStream()));//data coming from server
            type = inFromServer.readByte();
//            System.out.println("Type recieved is :" + type);

            length = inFromServer.readShort();
//            System.out.println("Length is " + length);

            if(type == 2)
            {
                System.out.println("Authenication Successfull");

                while(exit == false)
                {
                    type = inFromServer.readByte();
                    System.out.println("Type is " + type );
                    length = inFromServer.readShort();
                    System.out.println("Length is " + length);

                    if(type == 1)
                    {
                        System.out.println("Recieved KeepAlive Signal ");
                        //sending keep alive message back to the server
                        outToServer.writeByte(1);
                        outToServer.writeShort(0);

                        //waitiing for the server ot send me back the shitty
                        type = inFromServer.readByte();
                        length = inFromServer.readShort();
                        if(type == 2)
                        {
                            System.out.println("Recieved OK\n-----------");
                            //outToServer.writeByte(2);
                            //outToServer.writeShort(0);
                        }
                        else if (type == 3)
                        {
                            System.out.println("Invalid after Authentication");
                        }
                    }
                    else if (type == 7){
                        System.out.println("EXITING NOW");
                        exit = true;
                        client.close();
                    }
                    else{
                        System.out.println("Nothing recieved");
                    }
                }
            }
            else if(type == 3) {
                System.out.println("Invalid");
            }

            // InputStream reply = client.getInputStream();
         //   DataInputStream in = new DataInputStream(reply);
        //    System.out.println("Server says " + in.readUTF());



        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
