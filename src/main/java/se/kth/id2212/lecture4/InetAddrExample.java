package se.kth.id2212.lecture4;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Demonstration of <code>java.net.InetAddress</code>
 */
public class InetAddrExample {

    public static void main(String[] args) {
        InetAddress kthSe = null;
        InetAddress someAddr = null;
        InetAddress localIP = null;
        InetAddress[] allKthSe = null;
        try {
            localIP = InetAddress.getLocalHost();
            kthSe = InetAddress.getByName("kth.se");
            someAddr = InetAddress.getByName("130.237.214.1");
            allKthSe = InetAddress.getAllByName("kth.se");
            System.out.println("kth.se: " + kthSe);
            System.out.println("130.237.214.1: " + someAddr);
            System.out.println("localIP: " + localIP);
            System.out.println("all kth.se addrs: " + Arrays.toString(allKthSe));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
