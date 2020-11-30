package com.china.unicom.mqtt.bean;

import java.util.ArrayList;
import java.util.List;

import com.china.unicom.mqtt.utils.JsonObjectMapper;
import com.china.unicom.mqtt.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lifei
 * @Description: 事件上报bean
 * @Date: 2020/11/27
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventPubBean {
    private static final ObjectMapper objectMapper = JsonObjectMapper.getInstance();

    private String messageId;
    private Params params;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {

        private List<ObjectData> data;

        @Builder
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ObjectData {
            private String key;

            private List<Info> info;
        }

        @Builder
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Info {
            private String key;

            private String value;

            private String ts;
        }

    }

    public static String getPropertiesPub1(String messageId) {
        Params.Info info1 = new Params.Info();
        info1.setKey("userId");
        info1.setTs("12a");
        info1.setValue(Long.toString(Utils.randomTime()));

        Params.Info info2 = new Params.Info();
        info2.setKey("carNumber");
        info2.setValue("aa2660");
        info2.setTs(Long.toString(Utils.randomTime()));

        Params.Info info3 = new Params.Info();
        info3.setKey("goOutTime");
        info3.setValue("1600131935000");
        info3.setTs(Long.toString(Utils.randomTime()));

        Params.ObjectData objectData = new Params.ObjectData();
        objectData.setInfo(new ArrayList<Params.Info>() {
            {
                add(info1);
                add(info2);
                add(info3);
            }
        });
        objectData.setKey("goOutEvent");

        Params.Info info4 = new Params.Info();
        info4.setKey("alertType");
        info4.setValue("1");
        info4.setTs(Long.toString(Utils.randomTime()));

        Params.ObjectData objectData2 = new Params.ObjectData();
        objectData2.setInfo(new ArrayList<Params.Info>() {
            {
                add(info4);
            }
        });
        objectData2.setKey("tamperAlarm");

        List<Params.ObjectData> list = new ArrayList<Params.ObjectData>() {
            {
                add(objectData);
                add(objectData2);
            }
        };
        Params params = Params.builder().data(list).build();
        EventPubBean propertiesPubBean = EventPubBean.builder().messageId(messageId).params(params).build();
        try {
            return objectMapper.writeValueAsString(propertiesPubBean);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPropertiesPub2(String messageId) {
        Params.Info info1 = new Params.Info();
        info1.setKey("alertType");
        info1.setTs("1");
        info1.setValue(Long.toString(Utils.randomTime()));

        Params.ObjectData objectData = new Params.ObjectData();
        objectData.setInfo(new ArrayList<Params.Info>() {
            {
                add(info1);
            }
        });
        objectData.setKey("tamperAlarm");
        List<Params.ObjectData> list = new ArrayList<Params.ObjectData>() {
            {
                add(objectData);
            }
        };
        Params params = Params.builder().data(list).build();
        EventPubBean propertiesPubBean = EventPubBean.builder().messageId(messageId).params(params).build();
        try {
            return objectMapper.writeValueAsString(propertiesPubBean);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
