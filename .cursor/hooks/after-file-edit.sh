#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
source "$SCRIPT_DIR/lib/common.sh"

input="$(cat)"
file_path="$(echo "$input" | jq -r '.file_path // empty')"

if [[ -z "$file_path" ]]; then
  exit 0
fi

if ! is_relevant_file "$file_path"; then
  exit 0
fi

mark_edited "$file_path"

if is_kotlin_file "$file_path"; then
  run_gradle qualityFormatFile "$file_path" || true
fi

exit 0
