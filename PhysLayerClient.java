
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


/**
 *
 * @author Jorge
 */
public class PhysLayerClient {
        public static void main(String[] args) throws Exception {
                    try (Socket socket = new Socket("codebank.xyz", 38002)) {
                    byte a;
                    byte[] b = new byte[320];
                    byte[] fiveB = new byte[64];
                    byte[] A = new byte[64];
                    byte[] result = new byte[32];
                    double c = 0.0;
                        
                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();
                    
                    
                    System.out.println("Connected to server...");
                    
                    for(int i = 0; i < 64; i++){
                        c = c + is.read();
                    }
                    
                    c = (double)(c/64);
                    
                    System.out.printf("Baseline established from preamble: %.2f" + "\n", c);
                    
                    for(int j = 0; j < 320; j++){
                        a = (byte)is.read();
                        b[j] = a;
                    }

                    for(int m = 0; m < 320; m = m + 5){
                        a = 0;
                        
                        for(int n = 0; n < 5; n++){
                            if((((int)b[(m + n)]) & 0xFF) >= c){
                                a |= (byte) (1 << (4 - n));
                            }else{
                                a &= (byte) ~(1 << (4 - n));
                            }
                        }
                        fiveB[m/5] = (byte)a;
                    }                    

                    ReverseNRZI(fiveB, 64, 5, A);
                    
                    FiveBto4B(A, 64 ,fiveB);
                    
                    combineHalves(fiveB, 64, result);
                    
                    System.out.print("Bytes received: ");
                    for(int o = 0; o < 32; o++){

                        System.out.printf("%02X", result[o]);
                    }
                    System.out.println();
                    

                    os.write(result);
                    if(is.read() == 1){
                        System.out.println("Response good.");
                    }else{
                        System.out.println("Response bad.");
                    }
                    System.out.println("Disconnecting from server.");
                    socket.close();
                    }
        }
        
        public static void ReverseNRZI(byte[] in, int size, int widthSize, byte[] out){
            int previousNum = 0;
            
                    for(int k = 0; k < size; k++){

                        for(int p = (widthSize-1); p >= 0; p--){
                            if(previousNum == 0){
                                if       ((((in[k] >> p) & 1) == 1)){
                                    out[k] |= (1 << p);
                                    previousNum = 1;
                                }else if ((((in[k] >> p) & 1) == 0)){
                                    out[k] &= ~(1 << p);
                                    previousNum = 0;
                                }
                            }else{
                                if       ((((in[k] >> p) & 1) == 1)){
                                    out[k] &= ~(1 << p);
                                    previousNum = 1;
                                }else if ((((in[k] >> p) & 1) == 0)){
                                    out[k] |= (1 << p);
                                    previousNum = 0;
                                }
                            }
                        }
                    }
        }

        public static void FiveBto4B(byte[] in, int size, byte[] out){
            for(int i = 0; i < size; i++){
                switch (((int)(in[i]) & 0xFF)) {
                    case 30:    out[i] = 0;
                        break;
                    case 9:     out[i] = 1;
                        break;
                    case 20:    out[i] = 2;
                        break;
                    case 21:    out[i] = 3;
                        break;
                    case 10:    out[i] = 4;
                        break;
                    case 11:    out[i] = 5;
                        break;
                    case 14:    out[i] = 6;
                        break;
                    case 15:    out[i] = 7;
                        break;
                    case 18:      out[i] = 8;
                        break;
                    case 19:      out[i] = 9;
                        break;
                    case 22:      out[i] = 10;
                        break;
                    case 23:      out[i] = 11;
                        break;
                    case 26:      out[i] = 12;
                        break;
                    case 27:      out[i] = 13;
                        break;
                    case 28:      out[i] = 14;
                        break;
                    case 29:      out[i] = 15;
                        break;
                }
            }
        }
        public static void combineHalves(byte[] in, int size, byte[]out){
            byte a;
            byte b;
            
            for(int i = 0; i < size; i +=2){
                a = (byte) in[i];
                a = (byte) (a << 4);
                b = (byte) in[i+1];
                a = (byte) (a + b);
                out[i/2] = a;
            }
        }
}