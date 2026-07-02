#!/usr/bin/env bash
# 一键前端部署：构建 front/ 并同步到部署目录 /opt/ad-manager/dist/
#   ./deploy.sh          构建并同步
#   ./deploy.sh --no-build   跳过构建，仅同步已存在的 front/dist/
#   ./deploy.sh --serve   同步后启动本地预览服务器 (8082)
set -euo pipefail

FRONT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DIST_SRC="$FRONT_DIR/dist"
DEPLOY_DIR="/opt/ad-manager/dist"
PREVIEW_PORT=8082

NO_BUILD=0
SERVE=0
for arg in "$@"; do
  case "$arg" in
    --no-build) NO_BUILD=1 ;;
    --serve) SERVE=1 ;;
    *) echo "未知参数: $arg"; exit 1 ;;
  esac
done

cd "$FRONT_DIR"

if [[ "$NO_BUILD" -eq 0 ]]; then
  echo "==> 构建前端..."
  npm run build
fi

if [[ ! -d "$DIST_SRC" ]]; then
  echo "找不到构建产物: $DIST_SRC (请先运行 ./deploy.sh)"; exit 1
fi

echo "==> 同步到 $DEPLOY_DIR ..."
mkdir -p "$DEPLOY_DIR"
rm -rf "${DEPLOY_DIR:?}/"*
cp -r "$DIST_SRC/." "$DEPLOY_DIR/"

echo "==> 部署完成:"
echo "    源    : $DIST_SRC"
echo "    目标  : $DEPLOY_DIR"
echo "    文件数: $(find "$DEPLOY_DIR" -type f | wc -l)"

if [[ "$SERVE" -eq 1 ]]; then
  echo "==> 启动预览 (端口 $PREVIEW_PORT)..."
  cd "$DEPLOY_DIR"
  python3 -m http.server "$PREVIEW_PORT" --bind 0.0.0.0
fi
