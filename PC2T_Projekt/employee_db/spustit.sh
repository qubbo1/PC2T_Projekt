#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC="$SCRIPT_DIR/src"
OUT="$SCRIPT_DIR/out"
LIB="$SCRIPT_DIR/lib"

if [ ! -d "$OUT" ] || [ -z "$(ls -A "$OUT")" ]; then
    echo "Kompiluji zdrojové soubory..."
    mkdir -p "$OUT"
    find "$SRC" -name "*.java" > /tmp/sources.txt
    if ls "$LIB"/*.jar 1>/dev/null 2>&1; then
        javac --release 17 -cp "$LIB/*" -d "$OUT" @/tmp/sources.txt
    else
        javac --release 17 -d "$OUT" @/tmp/sources.txt
    fi
    echo "Kompilace dokončena."
fi

if ls "$LIB"/*.jar 1>/dev/null 2>&1; then
    java -cp "$OUT:$LIB/*" -Dfile.encoding=UTF-8 employeedb.Main
else
    java -cp "$OUT" -Dfile.encoding=UTF-8 employeedb.Main
fi
