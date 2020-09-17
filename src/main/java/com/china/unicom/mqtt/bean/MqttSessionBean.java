package com.china.unicom.mqtt.bean;

import lombok.*;

/**
 * @Author: lifei
 * @Description:
 * @Date: 2020/9/7
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqttSessionBean {

    public String userName;
    public String passwd;
    public String clientId;
    public String topic;

    public String subTopic;

}
