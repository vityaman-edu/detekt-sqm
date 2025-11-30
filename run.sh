#!/usr/bin/env sh

set -e
cd "$(dirname "$0")" || exit

gradle build
detekt --input "$1" --plugins build/libs/detekt-sqm-0.1.0.jar --all-rules | grep "WeightedMethodsPerClass"
