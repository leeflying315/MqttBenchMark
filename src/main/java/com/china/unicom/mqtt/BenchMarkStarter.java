package com.china.unicom.mqtt;

import com.china.unicom.mqtt.bean.MqttSessionBean;
import com.china.unicom.mqtt.config.Config;
import com.china.unicom.mqtt.utils.Utils;
import com.china.unicom.mqtt.verticle.MqttClientBindNetworkVerticle;
import com.china.unicom.mqtt.verticle.MqttClientVerticle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.List;

public class BenchMarkStarter {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(BenchMarkStarter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {

        Config configBean = initConfig();
        int totalConnection = configBean.getTotalConnection();
        String str = null;
        try {
            str = objectMapper.writeValueAsString(configBean);
        } catch (JsonProcessingException e) {
            LOGGER.error("", e);
        }

        JsonObject config = new JsonObject().put("configBean", str);

        Vertx vertx = Vertx.vertx();

        String[] sourceIps = configBean.getIpLists().split(",");

        if (configBean.ipLists == null) {
            LOGGER.error(" no source ip input in config.yaml, system exit");
            return;
        }
        String src = "F:\\workspace\\MqttBenchMark\\src\\main\\resources\\UPCT.txt";
        List<List<MqttSessionBean>> sortSessionGroup = getSessionInfo(src, totalConnection, sourceIps.length);
        int currentIps = 0;
        LOGGER.info("sort gourp is {}",sortSessionGroup.size());
        for (List<MqttSessionBean> sessionBeanList : sortSessionGroup) {
            config.put("localIp", sourceIps[currentIps]);
            String jsonArray = objectMapper.writeValueAsString(sessionBeanList);
            config.put("sessionList", jsonArray);
            vertx.deployVerticle(MqttClientBindNetworkVerticle.class.getName(),
                new DeploymentOptions().setConfig(config));
            currentIps++;
        }
    }

    public static Config initConfig() {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        InputStream inputStream = BenchMarkStarter.class.getClassLoader().getResourceAsStream("config.yaml");
        return yaml.load(inputStream);
    }

    public static List<List<MqttSessionBean>> getSessionInfo(String src, int totalConnection, int networkCards) {
        List<MqttSessionBean> mqttSessionBeanList = Utils.readCSVFileData(src);
        if (mqttSessionBeanList.size() < totalConnection) {
            LOGGER.error("input mock session data counts {}, less than target {}", mqttSessionBeanList.size(),
                totalConnection);
        }
        mqttSessionBeanList = mqttSessionBeanList.subList(0,totalConnection);
        return Utils.splitList(mqttSessionBeanList, networkCards);

    }
}
