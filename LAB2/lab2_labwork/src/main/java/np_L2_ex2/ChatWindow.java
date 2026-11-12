package np_L2_ex2;

import javax.swing.*;
import java.awt.*;
import java.net.*;

public class ChatWindow extends JFrame {

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    private DatagramSocket socket;
    private InetAddress remoteAddress;

    private int localPort;
    private int remotePort;

    public ChatWindow(int localPort, int remotePort) {
        this.localPort = localPort;
        this.remotePort = remotePort;

        try {
            socket = new DatagramSocket(localPort);
            remoteAddress = InetAddress.getByName("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeGUI();

        Thread receiverThread = new Thread(new Receiver());
        receiverThread.start();
    }

    private void initializeGUI() {

        setTitle("UDP Chat - Port " + localPort);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField();

        sendButton = new JButton("Send");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());

        setVisible(true);
    }

    private void sendMessage() {

        String msg = messageField.getText().trim();

        if (msg.isEmpty())
            return;

        try {

            byte[] buffer = msg.getBytes();

            DatagramPacket packet =
                    new DatagramPacket(
                            buffer,
                            buffer.length,
                            remoteAddress,
                            remotePort);

            socket.send(packet);

            chatArea.append("Me: " + msg + "\n");

            messageField.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class Receiver implements Runnable {

        @Override
        public void run() {

            while (true) {

                try {

                    byte[] buffer = new byte[1024];

                    DatagramPacket packet =
                            new DatagramPacket(buffer, buffer.length);

                    socket.receive(packet);

                    String msg =
                            new String(
                                    packet.getData(),
                                    0,
                                    packet.getLength());

                    SwingUtilities.invokeLater(() ->
                            chatArea.append("Peer: " + msg + "\n"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}