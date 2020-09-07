package com.china.unicom.mqtt.utils;

import com.china.unicom.mqtt.bean.MqttSessionBean;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
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
                    if (inetAddress instanceof Inet4Address && !inetAddress.isAnyLocalAddress()
                        && !inetAddress.isLinkLocalAddress() && !inetAddress.isLoopbackAddress()) { // IPV4
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

    // 拆分list为N个组
    public static <T> List<List<T>> splitList(List<T> list, int num) {
        int length = list.size();
        int groupSize = list.size()/num;
        List<List<T>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = Math.min((i + 1) * groupSize, length);
            newList.add(list.subList(fromIndex, toIndex));
        }
        return newList;
    }

    public static List<MqttSessionBean> readCSVFileData(String srcPath) {

        BufferedReader reader = null;
        List<MqttSessionBean> mqttSessionBeanSet = new LinkedList<>();
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(srcPath));
        } catch (FileNotFoundException e) {
            LOGGER.error("", e);
        }
        try {
            while ((line = reader.readLine()) != null) {
                String[] input = line.split(",");
                if (input.length != 4) {
                    LOGGER.error("illegal input length {} for {}", input.length, input);
                } else {
                    mqttSessionBeanSet.add(MqttSessionBean.builder().userName(input[0]).passwd(input[1])
                            .clientId(input[2]).topic(input[3]).build());
                }

            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return mqttSessionBeanSet;
    }
}
