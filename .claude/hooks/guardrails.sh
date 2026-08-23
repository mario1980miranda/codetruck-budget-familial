#!/bin/bash
input=$(cat)
path=$(echo "$input" | jq -r '.tool_input.file_path // empty')
[[ "$path" != *.java ]] && exit 0

content=$(echo "$input" | jq -r '.tool_input.content // .tool_input.new_string // empty')
[[ -z "$content" ]] && exit 0

v=""
grep -qE "^\s*@Autowired\s*$" <<< "$content" && v+="Injection par champ — utiliser l'injection par constructeur. "

[[ -n "$v" ]] && { echo "Violation de convention dans $path: $v" >&2; exit 2; }
exit 0
