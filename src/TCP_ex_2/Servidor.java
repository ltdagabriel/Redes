package TCP_ex_2;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Faça uma interface gráfica para os clientes de chat e que possibilite vários clientes enviarem e visualizarem
 * mensagens de os outros clientes conectados. Use o TCP
 */
public class Servidor extends Thread {

    private DataInputStream in;
    private DataOutputStream out;
    private static ArrayList<Socket> clientes;
    private Socket cliente;
    private String nome;

    public Servidor(Socket cliente) {
        this.cliente = cliente;
        clientes.add(cliente);
        try{
            in = new DataInputStream(cliente.getInputStream());
            out = new DataOutputStream(cliente.getOutputStream());
            nome = in.readUTF();
            send(cliente,nome +": entrou no chat!");
            out.writeUTF("Conexão aceita!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void send(Socket o, String msg) throws IOException {
        for(Socket c: clientes){
            DataOutputStream ouw = new DataOutputStream(c.getOutputStream());
            if(!c.equals(o)) ouw.writeUTF(msg);
        }
    }

    public void run() {
        try {
            Scanner reader = new Scanner(System.in);
            String buffer = "";
            while (!buffer.equalsIgnoreCase("sair")) {
                buffer = in.readUTF();

                System.out.println(buffer);
                if (buffer.equalsIgnoreCase("sair")) break;
                send(cliente,nome+": "+buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                clientes.remove(cliente);
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
            clientes = new ArrayList<Socket>();

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
