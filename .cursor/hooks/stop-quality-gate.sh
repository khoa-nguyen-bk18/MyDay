#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib/common.sh
source "$SCRIPT_DIR/lib/common.sh"

if ! has_edits_this_turn; then
  exit 0
fi

output_file="$(mktemp)"
if run_gradle qualityCheckAll >"$output_file" 2>&1; then
  clear_edit_marker
  rm -f "$output_file"
  exit 0
fi

summary="$(tail -n 80 "$output_file")"
rm -f "$output_file"
clear_edit_marker

message="Static analyzer checks failed at the end of this agent turn. Summary:

$summary

Run ./gradlew qualityCheckAll -PqualityAppModule=\"$APP_MODULE\" locally for full output and fix reported issues."

jq -n --arg msg "$message" '{ "followup_message": $msg }'
exit 0
