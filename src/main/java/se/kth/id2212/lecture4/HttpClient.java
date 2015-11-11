package se.kth.id2212.lecture4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Sends an HTTP request and prints the response.
 */
public class HttpClient {

    public static void main(String[] args) {
        String httpServer = "www.kth.se";
        int serverPort = 80;
        int timeoutMillis = 10000;
        String httpRequest = "GET / HTTP/1.1";
        String hostHeader = "Host: " + httpServer;

        try (Socket socket = new Socket(httpServer, serverPort)) {
            socket.setSoTimeout(timeoutMillis);
            PrintWriter wr = new PrintWriter(socket.getOutputStream());
            wr.println(httpRequest);
            wr.println(hostHeader);
            wr.println();
            wr.flush();
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String str;
            while ((str = reader.readLine()) != null) {
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
