package com.china.unicom.mqtt.bean;

import com.china.unicom.mqtt.utils.JsonObjectMapper;
import com.china.unicom.mqtt.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // 停车场属性上报
    public static String getPropertiesPub(String messageId) {
        Params.ObjectData info1 = new Params.ObjectData();
        info1.setKey("gateMachineSwitch");
        info1.setValue(true);
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
    // 温控柜属性上报
    public static String getPropertiesPub1() {
        String messageId = Integer.toString(Utils.randomInteger());

        Params.ObjectData info1 = new Params.ObjectData();
        info1.setKey("currentTemperature");
        info1.setValue(Utils.rangeInteger(50,-10));
        info1.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info2 = new Params.ObjectData();
        info2.setKey("workMode");
        info2.setValue(2);
        info2.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info3 = new Params.ObjectData();
        info3.setKey("windSpeed");
        info3.setValue(1);
        info3.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info4 = new Params.ObjectData();
        info4.setKey("targetTemperature");
        info4.setValue(Utils.rangeInteger(40,10));
        info4.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info5 = new Params.ObjectData();
        info5.setKey("switch");
        info5.setValue(1);
        info5.setTs(Long.toString(System.currentTimeMillis()));

        List<Params.ObjectData> objectData = new ArrayList<Params.ObjectData>() {
            {
                add(info1);
                add(info2);
                add(info3);
                add(info4);
                add(info5);
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

    // 土壤监测器
    public static String getPropertiesPub2() {
        String messageId = Integer.toString(Utils.randomInteger());

        Params.ObjectData info1 = new Params.ObjectData();
        info1.setKey("AIR_TEMP");
        info1.setValue(Utils.rangeInteger(120,-60));
        info1.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info2 = new Params.ObjectData();
        info2.setKey("AIR_HUMI");
        info2.setValue(Utils.rangeInteger(100,0));
        info2.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info3 = new Params.ObjectData();
        info3.setKey("SOIL_TEMP_L");
        info3.setValue(Utils.rangeInteger(120,-60));
        info3.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info4 = new Params.ObjectData();
        info4.setKey("SOIL_TEMP_M");
        info4.setValue(Utils.rangeInteger(120,-60));
        info4.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info5 = new Params.ObjectData();
        info5.setKey("SOIL_TEMP_D");
        info5.setValue(Utils.rangeInteger(120,-60));
        info5.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info6 = new Params.ObjectData();
        info6.setKey("BATT_VOLT");
        info6.setValue(Utils.rangeInteger(100,0));
        info6.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info7 = new Params.ObjectData();
        info7.setKey("SOIL_HUMI");
        info7.setValue(Utils.rangeInteger(100,0));
        info7.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info8 = new Params.ObjectData();
        info8.setKey("SOLARCELL_VOLT");
        info8.setValue(Utils.rangeInteger(100,0));
        info8.setTs(Long.toString(System.currentTimeMillis()));

        List<Params.ObjectData> objectData = new ArrayList<Params.ObjectData>() {
            {
                add(info1);
                add(info2);
                add(info3);
                add(info4);
                add(info5);
                add(info6);
                add(info7);
                add(info8);

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
    // 路灯属性上报
    public static String getLightPropertiesPub(String messageId) {
        Params.ObjectData info1 = new Params.ObjectData();
        info1.setKey("lightVoltage");
        info1.setValue(26.4);
        info1.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info2 = new Params.ObjectData();
        info2.setKey("lightCurrent");
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

    // 停车场属性上报
    public static String getPropertiesPub3() {
        String messageId = Integer.toString(Utils.randomInteger());

        Params.ObjectData info1 = new Params.ObjectData();
        info1.setKey("remnantParkSpace");
        info1.setValue(Utils.rangeInteger(9999,0));
        info1.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info2 = new Params.ObjectData();
        info2.setKey("totalParkSpace");
        info2.setValue(Utils.rangeInteger(9999,0));
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

    // 水质检测器
    public static String getPropertiesPub4() {
        String messageId = Integer.toString(Utils.randomInteger());

        Params.ObjectData info1 = new Params.ObjectData();
        info1.setKey("Voltage");
        info1.setValue(Utils.rangeInteger(100,0));
        info1.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info2 = new Params.ObjectData();
        info2.setKey("BatteryLevel");
        info2.setValue(Utils.rangeInteger(100,0));
        info2.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info3 = new Params.ObjectData();
        info3.setKey("PH");
        info3.setValue(Utils.rangeInteger(14,0));
        info3.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info4 = new Params.ObjectData();
        info4.setKey("Temperature");
        info4.setValue(Utils.rangeInteger(80,-40));
        info4.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info5 = new Params.ObjectData();
        info5.setKey("DissOxygen");
        info5.setValue(Utils.rangeInteger(20,0));
        info5.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info6 = new Params.ObjectData();
        info6.setKey("AmmoNitr");
        info6.setValue(Utils.rangeInteger(100,0));
        info6.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info7 = new Params.ObjectData();
        info7.setKey("Potential");
        info7.setValue(Utils.rangeInteger(1500,-1500));
        info7.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info8 = new Params.ObjectData();
        info8.setKey("Transparency");
        info8.setValue(Utils.rangeInteger(100,0));
        info8.setTs(Long.toString(System.currentTimeMillis()));

        List<Params.ObjectData> objectData = new ArrayList<Params.ObjectData>() {
            {
                add(info1);
                add(info2);
                add(info3);
                add(info4);
                add(info5);
                add(info6);
                add(info7);
                add(info8);

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

    // 变频器
    public static String getPropertiesPub5() {
        String messageId = Integer.toString(Utils.randomInteger());

        Params.ObjectData info1 = new Params.ObjectData();
        info1.setKey("invertStatus_1");
        info1.setValue(Utils.rangeInteger(100,0));
        info1.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info2 = new Params.ObjectData();
        info2.setKey("invertStatus_2");
        info2.setValue(Utils.rangeInteger(100,0));
        info2.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info3 = new Params.ObjectData();
        info3.setKey("invertStatus_3");
        info3.setValue(Utils.rangeInteger(100,0));
        info3.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info4 = new Params.ObjectData();
        info4.setKey("fanRunTime");
        info4.setValue(Utils.rangeInteger(100,0));
        info4.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info5 = new Params.ObjectData();
        info5.setKey("inverterRunTime");
        info5.setValue(Utils.rangeInteger(100,0));
        info5.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info6 = new Params.ObjectData();
        info6.setKey("pwOnTime");
        info6.setValue(Utils.rangeInteger(100,0));
        info6.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info7 = new Params.ObjectData();
        info7.setKey("outputVoltage");
        info7.setValue(Utils.rangeInteger(3000,0));
        info7.setTs(Long.toString(System.currentTimeMillis()));


        List<Params.ObjectData> objectData = new ArrayList<Params.ObjectData>() {
            {
//                add(info1);
//                add(info2);
//                add(info3);
                add(info4);
                add(info5);
                add(info6);
//                add(info7);
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

    // 电器火灾检测
    public static String getPropertiesPub6() {
        String messageId = Integer.toString(Utils.randomInteger());

        Params.ObjectData info1 = new Params.ObjectData();
        info1.setKey("Ua");
        info1.setValue(Utils.rangeInteger(32000,0));
        info1.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info2 = new Params.ObjectData();
        info2.setKey("Ub");
        info2.setValue(Utils.rangeInteger(32000,0));
        info2.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info3 = new Params.ObjectData();
        info3.setKey("Uc");
        info3.setValue(Utils.rangeInteger(32000,0));
        info3.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info4 = new Params.ObjectData();
        info4.setKey("Ia");
        info4.setValue(Utils.rangeInteger(32000,0));
        info4.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info5 = new Params.ObjectData();
        info5.setKey("Ib");
        info5.setValue(Utils.rangeInteger(32000,0));
        info5.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info6 = new Params.ObjectData();
        info6.setKey("Ic");
        info6.setValue(Utils.rangeInteger(32000,0));
        info6.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info7 = new Params.ObjectData();
        info7.setKey("Pa");
        info7.setValue(Utils.rangeInteger(32000,0));
        info7.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info8 = new Params.ObjectData();
        info8.setKey("Pb");
        info8.setValue(Utils.rangeInteger(32000,0));
        info8.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info9 = new Params.ObjectData();
        info9.setKey("Pc");
        info9.setValue(Utils.rangeInteger(32000,0));
        info9.setTs(Long.toString(System.currentTimeMillis()));

        List<Params.ObjectData> objectData = new ArrayList<Params.ObjectData>() {
            {
                add(info1);
                add(info2);
                add(info3);
                add(info4);
                add(info5);
                add(info6);
                add(info7);
                add(info8);
                add(info9);
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

    // 空调
    public static String getPropertiesPub7() {
        String messageId = Integer.toString(Utils.randomInteger());

        Params.ObjectData info1 = new Params.ObjectData();
        info1.setKey("currentTemperature");
        info1.setValue(Utils.rangeInteger(50,-10));
        info1.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info2 = new Params.ObjectData();
        info2.setKey("workMode");
        info2.setValue(2);
        info2.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info3 = new Params.ObjectData();
        info3.setKey("airSpeed");
        info3.setValue(1);
        info3.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info4 = new Params.ObjectData();
        info4.setKey("setTemperature");
        info4.setValue(Utils.rangeInteger(40,10));
        info4.setTs(Long.toString(System.currentTimeMillis()));

        Params.ObjectData info5 = new Params.ObjectData();
        info5.setKey("powerSwitch");
        info5.setValue(1);
        info5.setTs(Long.toString(System.currentTimeMillis()));


        List<Params.ObjectData> objectData = new ArrayList<Params.ObjectData>() {
            {
                add(info1);
                add(info2);
                add(info3);
//                add(info4);
//                add(info5);
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
