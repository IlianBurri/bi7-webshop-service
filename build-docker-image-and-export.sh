#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage: ./build-and-export.sh [-t TAG] [-o ARCHIVE]

Builds the backend locally, prepares runtime dependencies, builds the container image,
and optionally exports it as a tar archive for manual transfer.

Options:
  -t TAG       Image tag to build (default: bi7-webshop-service:1.0)
  -o ARCHIVE   Optional output file for `podman save` (for example: bi7-webshop-service_1.0.tar)
  -h           Show this help text
EOF
}

TAG="bi7-webshop-service:1.0"
ARCHIVE=""

while getopts ":t:o:h" opt; do
  case "$opt" in
    t) TAG="$OPTARG" ;;
    o) ARCHIVE="$OPTARG" ;;
    h)
      usage
      exit 0
      ;;
    \?)
      echo "Unknown option: -$OPTARG" >&2
      usage >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      usage >&2
      exit 1
      ;;
  esac
done

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if ! command -v mvn >/dev/null 2>&1; then
  echo "mvn is not installed or not on PATH" >&2
  exit 1
fi

if ! command -v podman >/dev/null 2>&1; then
  echo "podman is not installed or not on PATH" >&2
  exit 1
fi

echo "[1/3] Building application classes and runtime dependencies..."
mvn -q -DskipTests package dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/dependency

echo "[2/3] Building container image: $TAG"
podman build -t "$TAG" .

if [[ -n "$ARCHIVE" ]]; then
  echo "[3/3] Exporting image to: $ARCHIVE"
  podman save -o "$ARCHIVE" "$TAG"
else
  echo "[3/3] Skipping export (no archive path provided)"
fi

echo "Done."

