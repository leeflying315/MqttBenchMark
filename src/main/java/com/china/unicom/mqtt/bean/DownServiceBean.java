package com.china.unicom.mqtt.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lifei
 * @Date: 2020/12/29 11:43
 * @Description: 下行服务返回消息体，只替换MessageId
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownServiceBean {
    public String code;

    public String message;

    public String messageId;

    public Params data;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {
        public String modifyResult;
    }

    public DownServiceBean(String messageId) {
        this.messageId = messageId;
        this.code ="000000";
        this.message = "";
        this.data = Params.builder().modifyResult("1").build();
    }
}
