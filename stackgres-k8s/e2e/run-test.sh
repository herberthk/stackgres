#!/bin/sh

. "$(dirname "$0")/e2e"
echo "Preparing environment"

setup_images
setup_k8s
setup_cache
setup_helm
setup_operator
setup_logs

echo "Functional tests results" > "$TARGET_PATH/logs/results.log"

if [ -z "$1" ]
then
  >&2 echo "Must specify a test to run"
  exit 1
fi

SPEC_TO_RUN="${1#spec/}"
SPEC_TO_RUN="${SPEC_TO_RUN#*/spec/}"

if [ ! -f "$SPEC_PATH/$SPEC_TO_RUN" ]
then
  >&2 echo "Spec $SPEC_PATH/$SPEC_TO_RUN not found"
  exit 1
fi

try_function spec "$SPEC_PATH/$SPEC_TO_RUN"
if "$RESULT"
then
  cat "$TARGET_PATH/logs/results.log"
else
  cat "$TARGET_PATH/logs/results.log"
  exit "$EXIT_CODE"
fi