package com.china.unicom.mqtt.utils;

import org.apache.logging.log4j.LogManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Utils {
    private static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Utils.class);

    /**
     * 获取到所有的在活动的网卡IP 包含虚拟网卡
     *
     * @return IP列表
     */
    public static List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address &&
                            !inetAddress.isAnyLocalAddress() &&
                            !inetAddress.isLinkLocalAddress() &&
                            !inetAddress.isLoopbackAddress()) { // IPV4
                        LOGGER.info(inetAddress.getHostName());
                        ip = inetAddress.getHostAddress();
                        LOGGER.info(ip);
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList;
    }
}
