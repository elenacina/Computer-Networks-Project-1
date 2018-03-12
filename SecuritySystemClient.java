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
        String username = "bilkent";//args[1];
        String password  = "cs421";//args[2];
        String data = username + ":" + password;
        System.out.println ("whatever");
        try {
            Socket client = new Socket("localhost",port );
            //the output stream is connected to the input stream
            OutputStream outToServer = client.getOutputStream();
            //send the authentication message
            DataOutputStream authorize = new DataOutputStream(outToServer);
            //first we send the first byte which in case of authorize is 0
            authorize.writeByte(0);
            //then we send data size

            //now we pass the actual date which is username and password
            //first convert them to US-ASCII standard
            byte [] us =  data.getBytes(StandardCharsets.US_ASCII);
            short length = (short) data.length();
            authorize.writeShort(length);
            authorize.write(us);

            //getting response from server
            InputStream reply = client.getInputStream();
            DataInputStream in = new DataInputStream(reply);
            System.out.println("Server says " + in.readUTF());


             client.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
