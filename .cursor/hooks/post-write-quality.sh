#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
source "$SCRIPT_DIR/lib/common.sh"

input="$(cat)"
tool_name="$(echo "$input" | jq -r '.tool_name // empty')"
file_path="$(extract_write_file_path "$input")"

if [[ "$tool_name" != "Write" ]] || [[ -z "$file_path" ]]; then
  exit 0
fi

if ! is_relevant_file "$file_path"; then
  exit 0
fi

mark_edited "$file_path"

if ! is_kotlin_file "$file_path"; then
  exit 0
fi

output_file="$(mktemp)"
if run_gradle qualityCheckFile "$file_path" >"$output_file" 2>&1; then
  rm -f "$output_file"
  exit 0
fi

violations="$(cat "$output_file")"
rm -f "$output_file"

if [[ -z "$violations" ]]; then
  violations="Code quality check failed for $file_path. Run ./gradlew qualityCheckFile -PhookFile=\"$file_path\" -PqualityAppModule=\"$APP_MODULE\" for details."
fi

jq -n --arg context "$violations" '{ "additional_context": $context }'
exit 0
