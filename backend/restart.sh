#!/usr/bin/env bash
# 一键重启后端。
#   ./restart.sh          仅重启(改了 .env / 配置时用)
#   ./restart.sh --build  先重新编译再重启(改了 Java 代码时用)
set -euo pipefail

JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk-amd64}"
export JAVA_HOME
BACKEND_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR="$BACKEND_DIR/target/ad-manager-server-1.0.0-SNAPSHOT.jar"
LOG="/tmp/admanager-backend.log"
PORT=8081

cd "$BACKEND_DIR"

if [[ "${1:-}" == "--build" ]]; then
  echo "==> 编译中..."
  "$JAVA_HOME/bin/java" -version 2>/dev/null || { echo "JAVA_HOME 无效: $JAVA_HOME"; exit 1; }
  mvn -q clean package -DskipTests
fi

if [[ ! -f "$JAR" ]]; then
  echo "找不到 jar: $JAR (先跑 ./restart.sh --build)"; exit 1
fi

echo "==> 停止旧进程..."
pkill -f "ad-manager-server-1.0.0-SNAPSHOT.jar" 2>/dev/null || true
sleep 2

echo "==> 启动后端..."
setsid java -jar "$JAR" > "$LOG" 2>&1 < /dev/null &
disown

echo "==> 等待端口 $PORT ..."
for _ in $(seq 1 60); do
  if ss -tln | grep -q ":$PORT "; then
    echo "✓ 后端已就绪 (端口 $PORT),日志: $LOG"
    exit 0
  fi
  sleep 1
done

echo "✗ 60s 内未监听 $PORT,请查看日志: $LOG"
tail -20 "$LOG"
exit 1
