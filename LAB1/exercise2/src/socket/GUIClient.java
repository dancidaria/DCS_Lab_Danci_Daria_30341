package socket;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;


public class GUIClient extends JFrame {
  private JTextField txtNum1;
  private JTextField txtNum2;
  private JTextField txtResult;

  private final String SERVER_IP = "localhost";
  private final int PORT = 1900;

  public GUIClient() {

    setTitle("Network Calculator");
    setSize(350, 200);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new GridLayout(4, 2));

    txtNum1 = new JTextField();
    txtNum2 = new JTextField();
    txtResult = new JTextField();

    txtResult.setEditable(false);

    JButton btnAdd = new JButton("+");
    JButton btnSub = new JButton("-");
    JButton btnMul = new JButton("*");
    JButton btnDiv = new JButton("/");

    add(new JLabel("Number 1"));
    add(txtNum1);

    add(new JLabel("Number 2"));
    add(txtNum2);

    JPanel panel = new JPanel();

    panel.add(btnAdd);
    panel.add(btnSub);
    panel.add(btnMul);
    panel.add(btnDiv);

    add(panel);

    add(txtResult);

    btnAdd.addActionListener(e -> sendOperation("+"));
    btnSub.addActionListener(e -> sendOperation("-"));
    btnMul.addActionListener(e -> sendOperation("*"));
    btnDiv.addActionListener(e -> sendOperation("/"));

    setVisible(true);
  }

  private void sendOperation(String op) {

    try {
      Socket socket = new Socket(SERVER_IP, PORT);
      BufferedReader in = new BufferedReader(
              new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(
              socket.getOutputStream(), true);
      String message = txtNum1.getText() + ";" +
                      txtNum2.getText() + ";" + op;
      out.println(message);
      String result = in.readLine();
      txtResult.setText(result);
      socket.close();
    } catch (Exception ex) {
      txtResult.setText("Error");
      ex.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new GUIClient();
  }
}
