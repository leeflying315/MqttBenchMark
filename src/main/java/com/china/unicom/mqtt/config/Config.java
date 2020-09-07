package com.china.unicom.mqtt.config;

import lombok.Data;

/**
 * @Author: lifei
 * @Description: 配置类文件
 * @Date: 2020/9/7
 */
@Data
public class Config {

    public Server server;

    public Server getServer() {
        return server;
    }

    public Topic topic;



    public void setServer(Server server) {
        this.server = server;
    }

    // 总连接数
    public int totalConnection;
    // 单个实例的连接间隔
    public Integer interval;


    // 源IP列表，使用逗号分隔
    public String ipLists;

    @Data
    public static class Topic {
        public boolean publishMessage;

        public Integer publishInterval;

        public Integer qos;

    }

    @Data
    public static class Server {
        public Integer port;

        public String ip;

        public boolean useTls;
    }

}
