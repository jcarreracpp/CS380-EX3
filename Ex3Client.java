
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jorge
 */
public class Ex3Client {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("codebank.xyz", 38103)) {
            int buffer;
            short response;
            int answer;
            
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            
            System.out.println("Connected to server.");
            buffer = is.read();
            System.out.println("Reading " + buffer + " bytes.");
            System.out.println("Data received: ");
            byte[] dataholder = new byte[buffer];
            
            for(int i = 0; i < buffer; i++){
                dataholder[i] = (byte)is.read();
//                System.out.printf("%02X", dataholder[i]);
//                if((i+1)%10 == 0){
//                    System.out.println();
//                }
            }
            System.out.println("\n");
            
            
            response = checksum(dataholder, buffer);
            System.out.println(response);

            os.write((byte)(response >>> 8));
            os.write((byte)(response));
            answer = is.read();
            System.out.print("Succeed? " + answer +"\n");

            }
        }
        public static short checksum(byte[] b, int counter){
            long sum = 0;
            long a;
            long c;
            int overflow = 0;
            
            for(int i = 0; i < counter; i += 2){

                if((counter - i) > 1){
                    a = (b[i] & 0xFF);
                    a <<= 8;
                    c = (b[(i+1)] & 0xFFFF);
                    a += c;
                    sum = (short)(sum + a);
                }else{
                    System.out.println("ONCE");
                    a = (b[i] & 0xFF);
                    a <<= 8;
                    sum += (short)a;
                }
                
                if((sum & 0xFFFF0000) != 0x00000000){
                    overflow++;
                    sum &= 0xFFFF;
                    sum++;
                }

            }
            System.out.println("OVERFLOWED " + overflow + " TIME(S)");
            return (short)~(sum & 0xFFFF);
        }
}
