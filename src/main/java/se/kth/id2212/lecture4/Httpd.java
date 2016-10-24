package se.kth.id2212.lecture4;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.OutputStream;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * A simple HTTP server.
 * @author vladimir
 */
public class Httpd {

    static final String USAGE = "java Httpd [base] [port] ";

    public static void main(String[] args) {
        String base = "www";
        int port = 8080;

        if (args.length > 0) {
            base = args[0];
        }
        if (base.equalsIgnoreCase("-h") || base.equalsIgnoreCase("-help")) {
            System.out.println(USAGE);
            System.exit(1);
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println(USAGE);
                System.exit(0);
            }
        }
        try {
            ServerSocket listeningSocket = new ServerSocket(port);
            while (true) {
                // the main server's loop
                Socket clientSocket = listeningSocket.accept();
                Thread handler = new Thread(new Handler(clientSocket, base));
                handler.setPriority(handler.getPriority() + 1);
                handler.start();
            }
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}

/**
 * The class Handler processes the client's request in a separate thread
 */
class Handler implements Runnable {

    static final String SERVER = "Server: Httpd 1.0";
    static final String OK = "HTTP/1.0 200 OK";
    static final String NOT_FOUND = "HTTP/1.0 404 File Not Found";
    static final String NOT_FOUND_HTML = "<HTML><HEAD><TITLE>File Not Found</TITLE></HEAD><BODY><H1>HTTP Error 404: File Not Found</H1></BODY></HTML>";
    static final String NOT_IMPL = "HTTP/1.0 501 Not Implemented";
    static final String NOT_IMPL_HTML = "<HTML><HEAD><TITLE>Not Implemented</TITLE></HEAD><BODY><H1>HTTP Error 501: Not Implemented</H1></BODY></HTML>";
    private Socket s;
    private String base;

    Handler(Socket s, String base) {
        this.s = s;
        this.base = base;
    }

    @Override
    public void run() {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String str;
            if ((str = r.readLine()) == null) {
                s.close();
                return;
            }
            ;
            OutputStream os = s.getOutputStream(); // get output stream of the socket
            System.out.println(str); // for log
            StringTokenizer st = new StringTokenizer(str);
            String method = st.nextToken();
            String name = st.nextToken();
            String version = st.nextToken();
            while ((str = r.readLine()) != null && !str.trim().equals("")) {
                System.out.println(str); // for log
            } // away empty lines
            // if (str != null) System.out.println(str); // for log
            // Here is the GET request processed
            if (method.equals("GET")) {
                if (name.endsWith("/")) {
                    name += "index.html";
                }
                try {
                    File file = new File(base, name.substring(1, name.length()));
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buf = new byte[(int) file.length()];
                    fis.read(buf);
                    fis.close();
                    if (version.startsWith("HTTP/")) {
                        PrintWriter pw = new PrintWriter(os);
                        // Printing a head for response
                        pw.println(OK);
                        pw.println("Date:" + (new Date()));
                        pw.println(SERVER);
                        pw.println("Content-length: " + buf.length);
                        pw.println("Content-type: " + ContentTypeFrom(name));
                        pw.println();
                        pw.flush();
                    }
                    os.write(buf);
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    sendErrorMessage(os, NOT_FOUND, NOT_FOUND_HTML, version);
                }
            } else if (method.equals("POST")) {
                String query = r.readLine();
                if (query != null) {
                    System.out.println(query);
                }
                File file = new File(base, name.substring(1, name.length()));
                if (file.exists()) {
                    // the script file is found
                    Process cgiscript = Runtime.getRuntime().exec(file.getPath());
                    //System.out.println("Executing " + file.getPath() + " " + query);
                    PrintWriter pw = new PrintWriter(cgiscript.getOutputStream());
                    // Printing the query into the process input
                    pw.println(query);
                    pw.close();
                    BufferedInputStream is = new BufferedInputStream(cgiscript.getInputStream(),
                                                                     4096);
                    BufferedOutputStream bos = new BufferedOutputStream(os, 4096);
                    byte[] buf = new byte[4096];
                    int count;
                    // Fetching the script's output
                    if ((count = is.read(buf, 0, buf.length)) != -1) {
                        bos.write(buf, 0, count);
                    }
                    is.close();
                    bos.flush();
                    bos.close();
                } else {
                    sendErrorMessage(os, NOT_FOUND, NOT_FOUND_HTML, version);
                }
            } else {
                sendErrorMessage(os, NOT_IMPL, NOT_IMPL_HTML, version);
            }
            s.close();
        } catch (IOException e) {
            System.err.println("OBS, " + e.toString());
        }
    }

    /*
     * Gets the requested documents type from its extension
     */
    String ContentTypeFrom(String name) {
        if (name.endsWith(".html") || name.endsWith(".htm")) {
            return "text/html";
        } else if (name.endsWith(".txt") || name.endsWith(".java")) {
            return "text/plain";
        } else if (name.endsWith(".gif")) {
            return "image/gif";
        } else if (name.endsWith(".class")) {
            return "application/octet-stream";
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "text/plain";
        }
    }

    /*
     * Prints an error code into a given output stream
     */
    void sendErrorMessage(OutputStream os, String code, String html, String version) {
        PrintWriter pw = new PrintWriter(os);
        if (version.startsWith("HTTP/")) {
            pw.println(code);
            pw.println("Date:" + (new Date()));
            pw.println(SERVER);
            pw.println("Content-type: text/html");
            pw.println();
        }
        pw.println(html);
        pw.flush();
        pw.close();
    }
}
