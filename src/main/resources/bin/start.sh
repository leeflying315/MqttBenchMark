name="MqttBenchMark-1.0-SNAPSHOT.jar"
base_dir=$(cd `dirname $0`;cd ..; pwd)
sh ${base_dir}/bin/stop.sh

JAVA_OPTS="-Xms10G -Xmx10G -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xloggc:logs/gc.log -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"

#java ${JAVA_OPTS} -Dlog4j.configurationFile=${base_dir}/log4j2.xml -Dbase.dir=${base_dir} -jar ${name} >> 1.log  2>&1 &
nohup java ${JAVA_OPTS} -Dlog4j.configurationFile=${base_dir}/log4j2.xml -Dbase.dir=${base_dir} -jar ${name} >> /dev/null 2>&1 &

pid=`ps -ef | grep ${name} | grep -v grep |awk '{print $2}'`
if [ $pid ]; then
    echo  ${name}  is running pid=$pid start success
   else
         echo mqtt client failed
   exit
fi