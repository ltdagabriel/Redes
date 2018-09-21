package TCP_ex_1;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Fazer um código para o Cliente e Servidor se comunicarem. O cliente envia e recebe mensagens. O servidor envia
 * e recebe mensagens. Quando algum dos dois enviar 'SAIR', a comunicação entre eles deve ser finalizada. Use o TCP.
 */
public class Servidor extends Thread {

    private DataInputStream in;
    private DataOutputStream out;
    private Socket cliente;

    public Servidor(Socket cliente) {
        try {
            this.cliente = cliente;
            in = new DataInputStream(cliente.getInputStream());
            out = new DataOutputStream(cliente.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            Scanner reader = new Scanner(System.in);
            String buffer = "";
            while (!buffer.equalsIgnoreCase("sair")) {
                buffer = in.readUTF();

                System.out.println("Cliente:" + buffer);
                if(buffer.equalsIgnoreCase("sair")) break;
                System.out.print("MSG: ");
                buffer = reader.nextLine();

                out.writeUTF(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                cliente.close();
                System.out.println("Thread comunicação cliente finalizada.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        try {
            int port = 6666;

            ServerSocket server = new ServerSocket(port);

            while (true) {
                System.out.println("Servidor ligado na porta:" + port);

                Socket connect = server.accept();
                System.out.println("Connectando...");

                Servidor c = new Servidor(connect);

                c.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
