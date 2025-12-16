#!/usr/bin/env sh

set -e
cd "$(dirname "$0")" || exit

clear
gradle build
clear
detekt \
  --input "$1" \
  --plugins build/libs/detekt-sqm-0.1.0.jar \
  --disable-default-rulesets \
  --all-rules \
  --report SQMMarkdownReport:/tmp/sqm.md
