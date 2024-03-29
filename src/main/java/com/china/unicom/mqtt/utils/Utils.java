package com.china.unicom.mqtt.utils;

import com.china.unicom.mqtt.bean.EventPubBean;
import com.china.unicom.mqtt.bean.MqttSessionBean;
import com.china.unicom.mqtt.bean.PropertiesPubBean;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class Utils {
    private static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Utils.class);
    static Random random = new Random();

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

    // 拆分list为num个组
    public static <T> List<List<T>> splitList(List<T> list, int num) {
        int length = list.size();
        int groupSize = list.size() / num;
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
                if (input.length < 5) {
                    LOGGER.error("illegal input length {} for {}", input.length, input);
                } else {
                    // 0=用户名 1=密码 2=clientID 3= topic 4 订阅topic
                    mqttSessionBeanSet.add(MqttSessionBean.builder().userName(input[0]).passwd(input[1])
                            .clientId(input[2]).topic(input[3]).subTopic(input[4]).build());
                }

            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return mqttSessionBeanSet;
    }

    // 最近一个月到现在的随机时间
    public static long randomTime() {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        Date currentDate = new Date();
        calendar.setTime(currentDate); // 设置为当前时间
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月

        long begin = calendar.getTimeInMillis();
        return begin + (long) (Math.random() * (System.currentTimeMillis() - begin));

    }

    public static Integer randomInteger() {
        int max = Integer.MAX_VALUE - 10;
        int min = 1;
        Random random = new Random();

        int s = random.nextInt(max) % (max - min) + min;
        return s;
    }

    public static String getInputStringByDefault(Integer messageId, long time) {
        StringBuilder sb = new StringBuilder("{\"messageId\":\"");
        sb.append(messageId);
        sb.append("\",\"ts\":\"");
        sb.append(time);
        sb.append(
                "\",\"params\":{\"data\":[{\"key\":\"lightVoltage\",\"value\":499.01},{\"key\":\"lightCurrent\",\"value\":9.01},{\"key\":\"tiltValue\",\"value\":13},{\"key\":\"lightIllumination\",\"value\":12},{\"key\":\"powerConsumption\",\"value\":1.01}]}}");
        return sb.toString();
    }

    public static String getInputString(Integer selectType, String messageId) {
        if (selectType == 1)
            return EventPubBean.getPropertiesPub1(messageId);
        if (selectType == 2)
            return EventPubBean.getPropertiesPub2(messageId);
        if (selectType == 3)
            return PropertiesPubBean.getPropertiesPub(messageId);
        if (selectType == 4)
            return PropertiesPubBean.getLightPropertiesPub(messageId);
        return EventPubBean.getPropertiesPub1(messageId);
    }

    public static int rangeInteger(Integer max, Integer min) {

        return random.nextInt(max - min) + min;
    }

    public static void main(String[] args) {
        System.out.println(getInputStringByDefault(1,System.currentTimeMillis()));
    }
}
