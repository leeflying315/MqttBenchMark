name="MqttBenchMark-1.0-SNAPSHOT.jar"
pid=`ps -ef | grep ${name} | grep -v grep |awk '{print $2}'`
if [ $pid ]; then
    echo  ${name}  is  running pid=$pid start to kill
    kill -9 $pid
   else
         echo no mqtt client start
   exit
fi
pid=`ps -ef | grep ${name} | grep -v grep |awk '{print $2}'`
if [ $pid ]; then
    echo kill failed, please check
   else
     echo kill successed
fi
