# MqttBenchMark
 MQTT 性能压测工具，
 - 支持虚拟网卡，解决单机65536端口总数限制
 - 支持连接数总量设置
 - 支持定时发送
 - 支持读取配置文件获取clientId、UserName、PassWord
 - 支持迭代发送，压测服务端极限速度（Starter中修改启动的verticle）
 - 支持心跳自定义时间间隔

## 使用方式
1. 使用mvn clean package 打包
2. 运行 java -jar **.fat.jar -conf src/main/conf/conf.json

根据配置访问不同端口号

## 注意
实际性能压测中需要根据机器配置调整启动参数，如使用G1垃圾回收器，指定固定堆栈大小。设置GC最大期望时间等。
