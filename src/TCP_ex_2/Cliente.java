package TCP_ex_2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
/**
 * Faça uma interface gráfica para os clientes de chat e que possibilite vários clientes enviarem e visualizarem
 * mensagens de os outros clientes conectados. Use o TCP
 */
public class Cliente extends JFrame implements ActionListener, KeyListener {

    private DataInputStream in;
    private DataOutputStream out;
    private JTextArea txtHistorico;
    private JLabel lblHistorico;
    private JLabel lblBuffer;
    private JTextField txtBuffer;
    private JPanel janela;
    private Socket server;
    private String nome;

    public Cliente() {
        janela = new JPanel();

        // Output da Mensagem
        lblHistorico = new JLabel("Histórico");
        txtHistorico = new JTextArea(10, 20);
        txtHistorico.setEditable(false);
        txtHistorico.setBackground(new Color(0, 255, 0));
        JScrollPane scroll = new JScrollPane(txtHistorico);

        // Imput da Mensagem
        lblBuffer = new JLabel("Mensagem");
        txtBuffer = new JTextField(20);
        txtBuffer.addKeyListener(this);

        // Adicionar componentes a janela
        janela.add(lblHistorico);
        janela.add(scroll);

        janela.add(lblBuffer);
        janela.add(txtBuffer);

        janela.setBackground(Color.PINK);
        // Titulo da janela
        setTitle("Mensagem TCP");
        // Associar janela como conteudo da janela
        setContentPane(janela);
        // Janela de atmanho fixo
        setResizable(false);
        setSize(250, 300);
        // abrir janela na frente das outras
        setVisible(true);

        // Botao X
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void connect(Socket server, String nome) {
        this.server = server;
        this.nome = nome;
        String msg = "";
        setTitle(nome + ": chat");
        try {
            out = new DataOutputStream(server.getOutputStream());
            in = new DataInputStream(server.getInputStream());
            out.writeUTF(nome);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listener() throws IOException {
        String msg = "";
        while (!"sair".equalsIgnoreCase(msg)) {
            msg = in.readUTF();

            if (msg.equals("sair"))
                txtHistorico.append("Desconectado pelo servidor!!\n");
            else
                txtHistorico.append(msg + "\n");
        }
    }

    public void send(String msg) throws IOException {

        out.writeUTF(msg);
        if ("sair".equalsIgnoreCase(msg)) return;
        txtHistorico.append("eu: " + msg + '\n');
    }

    public void close() {
        try {
            in.close();
            out.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        Cliente server = new Cliente();

        int port = 6666;
        JLabel lblMessage = new JLabel("Verificar!");
        JTextField txtNome = new JTextField("Cliente");
        Object[] texts = {lblMessage, txtNome};
        JOptionPane.showMessageDialog(null, texts);

        try {
            InetAddress address = InetAddress.getByName("localhost");
            server.connect(new Socket(address, port), txtNome.getText());
            server.listener();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.close();

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER: {
                try {
                    send(txtBuffer.getText());
                    txtBuffer.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
