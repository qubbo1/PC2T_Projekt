#!/bin/bash
cd "$(dirname "$0")"
bash ./spustit.sh
echo
echo "Program skoncil. Toto okno mozes zavriet."
read -n 1 -s -r -p "Stlac lubovolnu klavesu..."
