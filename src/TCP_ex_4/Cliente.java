package TCP_ex_4;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Fazer uma aplicação com um servidor que gerencia a comunicação entre dois clientes usando TCP. Para cada cliente é
 * criada uma thread no servidor. A comunicação entre as threads deve usar recursos como pipe ou memória compartilhada.
 */
public class Cliente extends JFrame implements ActionListener, KeyListener, ListSelectionListener {

    private JList<String> list;
    private DefaultListModel<String> listModel;
    private JSplitPane janela;
    private DataInputStream in;
    private DataOutputStream out;
    private JTextArea txtHistorico;
    private JLabel lblHistorico;
    private JLabel lblBuffer;
    private JTextField txtBuffer;
    private JPanel right;
    private JPanel left;
    private Socket server;
    private String nome;
    private ArrayList<String> users;
    private boolean fechar;
    private String user;

    public Cliente(String nome) {
        users = new ArrayList<String>();
        users.add(nome);
        user = nome;
        fechar = false;
        this.nome = nome;
        setTitle("CHAT: " + nome);
        left = new JPanel();

        listModel = new DefaultListModel<String>();
        listModel.addElement(nome);
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(0);
        list.addListSelectionListener(this);
        left.add(list);

        right = new JPanel();

        // Output da Mensagem
        lblHistorico = new JLabel("Histórico");
        txtHistorico = new JTextArea(10, 20);
        txtHistorico.setEditable(false);
        txtHistorico.setBackground(new Color(0, 255, 0));

        txtHistorico.append("" +
                "---- Comandos ----\n" +
                "conectar USER\n" +
                "-- CONECTA COM O USUARIO\n" +
                "desconectar\n" +
                "-- VOLTA A SE COMUNICAR COM O SERVIDOR\n" +
                "");
        JScrollPane scroll = new JScrollPane(txtHistorico);

        // Imput da Mensagem
        lblBuffer = new JLabel("Mensagem");
        txtBuffer = new JTextField(20);
        txtBuffer.addKeyListener(this);

        // Adicionar componentes a janela
        right.add(lblHistorico);
        right.add(scroll);

        right.add(lblBuffer);
        right.add(txtBuffer);

        right.setBackground(Color.PINK);

        janela = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                left, right);
//        janela.setOneTouchExpandable(true);
        janela.setDividerLocation(150);

        Dimension minimumSize = new Dimension(100, 50);

        left.setMinimumSize(minimumSize);
//        right.setMinimumSize(minimumSize);

        // Titulo da janela
        setTitle("Mensagem TCP: " + nome);
        // Associar janela como conteudo da janela
        setContentPane(janela);
        // Janela de atmanho fixo
        setResizable(false);
        setSize(400, 300);
        // abrir janela na frente das outras
        setVisible(true);

        // Botao X
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void connect(Socket server) {
        this.server = server;
        String msg = "";

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
        while (!fechar && !"sair".equalsIgnoreCase(msg)) {
            msg = in.readUTF();

            String[] received = msg.split(" ");
            if (received[0].equalsIgnoreCase("connect")) {
                users.add(received[1]);
                listModel.addElement(received[1]);
            }
            else if(received[0].equalsIgnoreCase("disconnect")){
                users.remove(received[1]);
                listModel.removeElement(received[1]);
            }
            else if (msg.equals("sair"))
                txtHistorico.append("Desconectado pelo servidor!!\n");
            else
                txtHistorico.append(msg + "\n");
        }
    }

    public void send(String msg) throws IOException {
        String[] token = msg.split(" ");
        if (token[0].equalsIgnoreCase("conectar")) {
            out.writeUTF(msg + " " + user);
            return;
        }
        if (token[0].equalsIgnoreCase("desconectar")) {
            out.writeUTF(msg);
            return;
        }
        out.writeUTF(msg);
        if ("sair".equalsIgnoreCase(msg)) return;
        txtHistorico.append("eu : " + msg + '\n');
    }

    public void close() {
        try {
            out.writeUTF("sair");
            this.dispose();
            fechar = true;
            in.close();
            out.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {


        int port = 6666;
        JLabel lblMessage = new JLabel("Verificar!");
        JTextField txtNome = new JTextField("Cliente");
        Object[] texts = {lblMessage, txtNome};
        JOptionPane.showMessageDialog(null, texts);

        Cliente server = new Cliente(txtNome.getText());
        try {
            InetAddress address = InetAddress.getByName("localhost");
            server.connect(new Socket(address, port));
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

    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList) e.getSource();
        user = users.get(list.getSelectedIndex());
        System.out.println(user);
    }
}
