package TCP_ex_4;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Fazer uma aplicação com um servidor que gerencia a comunicação entre dois clientes usando TCP. Para cada cliente é
 * criada uma thread no servidor. A comunicação entre as threads deve usar recursos como pipe ou memória compartilhada.
 */
public class Servidor extends Thread {

    private DataInputStream in;
    private DataOutputStream out;
    private static ArrayList<Socket> clientes;
    private static ArrayList<String> clientes_nome;
    private Socket cliente;
    private String nome;

    public Servidor(Socket cliente) {
        this.cliente = cliente;
        clientes.add(cliente);
        try{
            in = new DataInputStream(cliente.getInputStream());
            out = new DataOutputStream(cliente.getOutputStream());
            nome = in.readUTF();
            for(String a: clientes_nome){
                if(a.equalsIgnoreCase(nome)) break;
                out.writeUTF("connect "+ a);
            }
            send(cliente,"connect "+nome);
            clientes_nome.add(nome.toLowerCase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void send(Socket o, String msg) throws IOException {
        for(Socket c: clientes){
            System.out.println(c.getPort()+": "+msg);
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
                String[] token = buffer.split(" ");
                if(token[0].equalsIgnoreCase("conectar")){
                    System.out.println(token[1]);
                    int index = clientes_nome.indexOf(token[1].toLowerCase());
                    if(index<0) {
                        out.writeUTF("Usuario não encontrado!");
                        break;
                    }
                    Socket to = clientes.get(index);

                    DataOutputStream outto = new DataOutputStream(to.getOutputStream());
                    while(!buffer.equalsIgnoreCase("desconectar")){
                        buffer = in.readUTF();
                        if (buffer.equalsIgnoreCase("sair")){
                            break;
                        }
                        outto.writeUTF(nome+": "+buffer);
                    }
                }
                if (buffer.equalsIgnoreCase("sair")) break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                send(cliente,"disconnect "+ nome);
                clientes.remove(cliente);
                clientes_nome.remove(nome);
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
            clientes_nome = new ArrayList<String>();

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
