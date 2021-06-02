#!/bin/sh


# 获取java命令所在。
# 如果javahome没有配置（长度为0），尝试通过 which java获取位置
if [ -z "$JAVA_HOME" ] ; then
  JAVACMD=`which java`
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

# 配置文件目录
TBPCFGDIR="${TBP_HOME}/conf"
# 这里，拼接 classpath
CLASSPATH="$TBPCFGDIR:$CLASSPATH"
for i in "${TBP_HOME}"/lib/*.jar
do
    CLASSPATH="$i:$CLASSPATH"
done