
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
            
            System.out.print("\t");
            
            for(int i = 0; i < buffer; i++){
                dataholder[i] = (byte)is.read();
                System.out.printf("%02X", dataholder[i]);
                if((i+1)%10 == 0){
                    System.out.println();
                    System.out.print("\t");
                }
            }
            System.out.println("\n");
            
            
            response = checksum(dataholder, buffer);

            os.write((short)(response >>> 8));
            os.write((response & 0xFF));
            
            answer = is.read();
            
            if(answer == 1){
                System.out.println("Response good.");
            }else{
                System.out.println("Response bad.");
            }

            }
        }
    
        public static short checksum(byte[] b, int counter){
            long sum = 0;
            long a;
            long c;
            
            for(int i = 0; i < counter; i += 2){
                if((counter - i) > 1){
                    a = (b[i] & 0xFF);
                    a <<= 8;
                    c = (b[(i+1)] & 0xFF);
                    a += c;
                    sum = (sum + a);
                }else{
                    a = (b[i] & 0xFF);
                    a <<= 8;
                    sum = (sum + a);
                }
                
                if((sum & 0xFFFF0000) != 0){
                    sum &= 0xFFFF;
                    sum++;
                }
            }
            return (short)~(sum &= 0xFFFF);
        }
}
