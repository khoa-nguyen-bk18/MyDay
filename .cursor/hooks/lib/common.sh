#!/usr/bin/env bash

STATE_DIR=".cursor/hooks/.state"
EDIT_MARKER="$STATE_DIR/edited-this-turn"
APP_MODULE="${APP_MODULE:-androidApp}"

find_project_root() {
  local dir="$PWD"
  while [[ "$dir" != "/" ]]; do
    if [[ -f "$dir/gradlew" ]]; then
      echo "$dir"
      return 0
    fi
    dir="$(dirname "$dir")"
  done
  echo "$PWD"
}

PROJECT_ROOT="$(find_project_root)"
cd "$PROJECT_ROOT"

load_project_env() {
  local env_file="$PROJECT_ROOT/.cursor/hooks/project.env"
  if [[ -f "$env_file" ]]; then
    # shellcheck source=/dev/null
    source "$env_file"
  fi
  APP_MODULE="${APP_MODULE:-androidApp}"
}

load_project_env

is_kotlin_file() {
  local path="$1"
  [[ "$path" == *.kt || "$path" == *.kts ]]
}

is_relevant_file() {
  local path="$1"
  is_kotlin_file "$path" || [[ "$path" == *.xml ]] || [[ "$(basename "$path")" == "AndroidManifest.xml" ]]
}

to_relative_path() {
  local path="$1"
  if [[ "$path" == "$PROJECT_ROOT"/* ]]; then
    echo "${path#"$PROJECT_ROOT"/}"
  elif [[ "$path" == /* ]]; then
    python3 -c "import os,sys; print(os.path.relpath(sys.argv[1], sys.argv[2]))" "$path" "$PROJECT_ROOT"
  else
    echo "$path"
  fi
}

extract_write_file_path() {
  local input="$1"
  local path
  path="$(echo "$input" | jq -r '.tool_input.path // .tool_input.file_path // .file_path // empty')"
  if [[ -z "$path" ]]; then
    return 0
  fi
  if [[ "$path" == /* ]]; then
    to_relative_path "$path"
  else
    echo "$path"
  fi
}

run_gradle() {
  local task="$1"
  local file_path="${2:-}"
  if [[ -n "$file_path" ]]; then
    local relative
    relative="$(to_relative_path "$file_path")"
    ./gradlew -q --no-daemon --no-configuration-cache "$task" \
      -PhookFile="$relative" \
      -PqualityAppModule="$APP_MODULE"
  else
    ./gradlew -q --no-daemon --no-configuration-cache "$task" \
      -PqualityAppModule="$APP_MODULE"
  fi
}

mark_edited() {
  mkdir -p "$STATE_DIR"
  touch "$EDIT_MARKER"
}

has_edits_this_turn() {
  [[ -f "$EDIT_MARKER" ]]
}

clear_edit_marker() {
  rm -f "$EDIT_MARKER"
}
