package com.china.unicom.mqtt.bean;

import com.china.unicom.mqtt.utils.JsonObjectMapper;
import com.china.unicom.mqtt.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lifei
 * @Description:
 * @Date: 2020/11/27
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertiesPubBean {
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

            private Object value;

            private String ts;
        }

    }

    public static String getPropertiesPub(String messageId) {
        Params.ObjectData info1 = new Params.ObjectData();
        info1.setKey("gateMachineSwitch");
        info1.setValue("0");
        info1.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info2 = new Params.ObjectData();
        info2.setKey("totalParkSpace");
        info2.setValue(5);
        info2.setTs(Long.toString(System.currentTimeMillis()));

        List<Params.ObjectData> objectData = new ArrayList<Params.ObjectData>() {
            {
                add(info1);
                add(info2);
            }
        };
        Params params = Params.builder().data(objectData).build();
        PropertiesPubBean propertiesPubBean = PropertiesPubBean.builder().messageId(messageId).params(params).build();
        try {
            return objectMapper.writeValueAsString(propertiesPubBean);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getPropertiesPub(Integer.toString(Utils.randomInteger())));
    }
}
