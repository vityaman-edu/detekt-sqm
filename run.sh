#!/usr/bin/env sh

set -e
cd "$(dirname "$0")" || exit

clear
gradle assemble
clear
(cd "$1"; gradle assemble)
clear
detekt \
  --input "$1/src" \
  --classpath "$1/build/classes/kotlin/main" \
  --plugins build/libs/detekt-sqm-0.1.0.jar \
  --disable-default-rulesets \
  --all-rules \
  --report SQMMarkdownReport:/tmp/sqm.md
