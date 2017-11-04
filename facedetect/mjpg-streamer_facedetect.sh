#!/bin/bash

raspistill --nopreview -w 800 -h 600 -o /tmp/stream/pic.jpg -tl 1000 -t 9999999 -th 0:0:0 &

while true; do
	inotifywait /tmp/stream/pic.jpg; time facedetect -d -o /tmp/stream/pic.facedetect.jpg /tmp/stream/pic.jpg
done
