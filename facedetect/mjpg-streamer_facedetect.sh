#!/bin/bash

while true; do
       	time raspistill --nopreview -w 800 -h 600 -o /tmp/stream/pic.jpg
	time facedetect -d -o /tmp/stream/pic.facedetect.jpg /tmp/stream/pic.jpg
done
