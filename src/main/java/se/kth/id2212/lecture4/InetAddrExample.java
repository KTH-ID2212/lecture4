package se.kth.id2212.lecture4;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Demonstration of <code>java.net.InetAddress</code>
 */
public class InetAddrExample {

    public static void main(String[] args) {
        InetAddress ip1 = null;
        InetAddress ip2 = null;
        InetAddress localIP = null;
        InetAddress[] ips = null;
        try {
            localIP = InetAddress.getLocalHost();
            ip1 = InetAddress.getByName("kth.se");
            ip2 = InetAddress.getByName("130.237.214.1");
            ips = InetAddress.getAllByName("kth.se");
            System.out.println("ip1: " + ip1);
            System.out.println("ip2: " + ip2);
            System.out.println("localIP: " + localIP);
            System.out.println("ips: " + Arrays.toString(ips));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
