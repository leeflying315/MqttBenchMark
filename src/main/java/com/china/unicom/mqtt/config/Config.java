package com.china.unicom.mqtt.config;

/**
 * @Author: lifei
 * @Description: 配置类文件
 * @Date: 2020/9/7
 */

public class Config {

    public Server server;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    // 总连接数
    public int totalConnection;
    // 单个实例的连接间隔
    public Integer interval;

    public static class Server {
        public Integer port;

        public String ip;

        public boolean useTls;

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public boolean isUseTls() {
            return useTls;
        }

        public void setUseTls(boolean useTls) {
            this.useTls = useTls;
        }
    }

    // 源IP列表，使用逗号分隔
    public String ipLists;

    public int getTotalConnection() {
        return totalConnection;
    }

    public void setTotalConnection(int totalConnection) {
        this.totalConnection = totalConnection;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getIpLists() {
        return ipLists;
    }

    public void setIpLists(String ipLists) {
        this.ipLists = ipLists;
    }
}
