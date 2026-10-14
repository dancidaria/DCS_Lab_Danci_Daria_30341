package socket;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1900);
            System.out.println("Server started...");
            while(true) {
                Socket socket = serverSocket.accept();
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String request = in.readLine();
                String[] data = request.split(";");

                double a = Double.parseDouble(data[0]);
                double b = Double.parseDouble(data[1]);
                String op = data[2];
                double result = 0;
                switch (op) {
                    case "+":
                        result = a + b;
                        break;
                    case "-":
                        result = a - b;
                        break;
                    case "*":
                        result = a * b;
                        break;
                    case "/":
                        result = a / b;
                        break;
                }
                out.println(result);
                socket.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}