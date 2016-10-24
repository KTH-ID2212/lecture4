package se.kth.id2212.lecture4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Counts how many words and numbers there are in a HTTP response. This illustrates how the
 * <code>java.io.StreamTokenizer</code> class can be used.
 */
public class InputStreamParser {

    public static void main(String[] args) {
        String httpServer = "www.kth.se";
        int serverPort = 80;
        int timeoutMillis = 10000;
        String httpRequest = "GET / HTTP/1.1";
        String hostHeader = "Host: " + httpServer;
        int numCounter = 0;
        int wordCounter = 0;
        
        try (Socket socket = new Socket(httpServer, serverPort)) {
            socket.setSoTimeout(timeoutMillis);
            PrintWriter wr = new PrintWriter(socket.getOutputStream());
            wr.println(httpRequest);
            wr.println(hostHeader);
            wr.println();
            wr.flush();
            StreamTokenizer rd = new StreamTokenizer(new BufferedReader(new InputStreamReader(
                    socket.getInputStream())));
            int lineBreak = 0;
            while (rd.nextToken() != StreamTokenizer.TT_EOF) {
                switch (rd.ttype) {
                    case StreamTokenizer.TT_NUMBER:
                        numCounter++;
                        break;
                    case StreamTokenizer.TT_WORD:
                        wordCounter++;
                        if (++lineBreak % 5 == 0) {
                            System.out.println(rd.sval);
                        } else {
                            System.out.print(rd.sval + " ");
                        }
                }
            }
        } catch (SocketTimeoutException timeout) {
            System.out.println();
            System.out.println("Numbers: " + numCounter);
            System.out.println("Words: " + wordCounter);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
