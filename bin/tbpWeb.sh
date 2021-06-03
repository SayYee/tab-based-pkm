#!/bin/sh

# 保存当前完整工作目录（执行命令的地方）
saveddir=`pwd`

# 获取TBP目录
# 获取bin目录，然后向上调整一级获取TBP目录
TBP_BIN="${BASH_SOURCE-$0}"
TBP_HOME=`dirname "${TBP_BIN}"`/..
TBP_HOME=`cd "$TBP_HOME" && pwd`

APPLICATION_JAR="$TBP_HOME/web/tbp-web.jar"
APPLICATION_CONF="$TBP_HOME/conf/application.yml"

java -jar ${APPLICATION_JAR} --spring.config.location=file:${APPLICATION_CONF}