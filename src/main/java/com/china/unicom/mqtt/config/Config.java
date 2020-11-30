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

    public Topic topic;
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

        public Integer messageCount;

        public boolean subPubTopic;

        // 发布模式
        // 1: 事件上报1
        // 2: 事件上报2
        // 3: 属性上报3
        public Integer publishMode;
    }

    @Data
    public static class Server {
        public Integer port;

        public String ip;

        public boolean useTls;

        public Integer heartBeatInterval;
    }

}
