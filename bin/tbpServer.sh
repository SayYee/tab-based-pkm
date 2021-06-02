#!/bin/sh

# 保存当前完整工作目录（执行命令的地方）
saveddir=`pwd`

# 获取TBP目录
# 获取bin目录，然后向上调整一级获取TBP目录
TBP_BIN="${BASH_SOURCE-$0}"
TBP_HOME=`dirname "${TBP_BIN}"`/..
TBP_HOME=`cd "$TBP_HOME" && pwd`

# 这里，需要把上一步的cd操作还原回来。还原工作目录
cd "$saveddir"

# 通过这种方式调用，tbpEnv中定义的变量，可以在当前文件中使用(shell 文件包含)
. "$TBP_HOME"/bin/tbpEnv.sh

LAUNCHER=com.sayyi.software.tbp.nio.server.TbpServer


exec "${JAVACMD}" \
  -classpath "${CLASSPATH}" \
  ${LAUNCHER} "$@"