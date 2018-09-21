package TCP_ex_1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
/**
 * Fazer um código para o Cliente e Servidor se comunicarem. O cliente envia e recebe mensagens. O servidor envia
 * e recebe mensagens. Quando algum dos dois enviar 'SAIR', a comunicação entre eles deve ser finalizada. Use o TCP.
 */
public class Cliente {
    public static void main(String args[]){
        Socket server = null;
        Scanner reader = new Scanner(System.in);

        try{
            int port = 6666;
            InetAddress address = InetAddress.getByName("localhost");

            server = new Socket(address,port);

            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            DataInputStream in = new DataInputStream(server.getInputStream());

            String buffer = "";
            while (!buffer.equalsIgnoreCase("sair")){
                System.out.print("MSG: ");
                buffer = reader.nextLine();

                out.writeUTF(buffer);

                if(buffer.equalsIgnoreCase("sair")) break;

                buffer = in.readUTF();
                System.out.println("Server: " + buffer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                assert server != null;
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
