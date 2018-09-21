package TCP_ex_3;


import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
/**
 * Faça um servidor para processar as seguintes mensagens dos clientes. O servidor deve suportar mensagens de
 * múltiplos clientes. Use o TCP.
 * As mensagens estão no formato String UTF:
 * TIME * Retorna a hora do sistema como uma String UTF no formato HH:MM:SS
 * DATE * Retorna a data do sistema como uma String UTF no formato DD/MM/AAAA
 * FILES
 *       * Retorna os arquivos da pasta definida por padrao (p. ex. /home/user/shared)
 *       * retorna um inteiro indicando o número de arquivos
 *       * envia o nome de um arquivo por vez como uma String UTF
 * DOWN nome-arquivo
 *       * Faz o download do arquivo nome-arquivo
 *       * retorna 0 se nome não existe ou retorna o tamanho do arquivo
 *       * lê o número de bytes indicado por tamanho do arquivo e grava em um diretório padrão
 * EXIT
 *       * Finaliza a conexão
 */
public class Servidor extends Thread {

    private DataInputStream in;
    private DataOutputStream out;
    private Socket cliente;
    private static File diretorio;

    public Servidor(Socket cliente) {
        try {
            diretorio = new File("C:\\Users\\gabri\\OneDrive\\Documentos\\shared");
            this.cliente = cliente;
            in = new DataInputStream(cliente.getInputStream());
            out = new DataOutputStream(cliente.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void process(String MSG) throws IOException {
        String msg[]= MSG.split(" ");

        switch (msg[0].toLowerCase()){
            case "exit":{
                out.writeUTF("exit");
                break;
            }
            case "time":{
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date hora = Calendar.getInstance().getTime(); // Ou qualquer outra forma que tem
                String dataFormatada = sdf.format(hora);
                out.writeUTF(dataFormatada);
                break;
            }
            case "date":{
                Date data = new Date(System.currentTimeMillis());
                SimpleDateFormat formatarDate = new SimpleDateFormat("dd-MM-yyyy");
                String a = formatarDate.format(data);
                out.writeUTF(a);
                break;
            }
            case "files":{
                File files[] = diretorio.listFiles();
                assert files != null;
                out.writeUTF(String.valueOf(files.length));
                for(File a:files){
                    String nome = a.getName();
                    out.writeUTF(nome);
                }
                break;
            }
            case "down":{
                File files[] = diretorio.listFiles();
                assert files != null;
                boolean find = false;
                for (File a:files){
                    if(a.getName().equalsIgnoreCase(msg[1])){
                        find = true;
                        FileInputStream fis = new FileInputStream(a);
                        byte [] mybytearray  = new byte [(int)a.length()];

                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(a));

                        bis.read(mybytearray, 0, mybytearray.length);
                        out.writeUTF("file "+a.getName());
                        out.writeInt((int)a.length());
                        out.write(mybytearray, 0, mybytearray.length);

                    }
                }
                if(!find){
                    out.writeUTF("0");
                }
            }
        }
    }

    public void run() {
        try {
            Scanner reader = new Scanner(System.in);
            String buffer = "";
            while (!buffer.equalsIgnoreCase("exit")) {
                buffer = in.readUTF();

                System.out.println("Cliente:" + buffer);
                if(buffer.equalsIgnoreCase("exit")) break;
                process(buffer);
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
