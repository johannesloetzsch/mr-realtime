mjpg_streamer_facedetect:
	[ -d /tmp/stream/ ] || mkdir /tmp/stream/
	LD_LIBRARY_PATH=/usr/local/lib mjpg_streamer -i "input_file.so -f /tmp/stream -n pic.facedetect.jpg" -o "output_http.so -w /usr/local/www" &
	./mjpg-streamer_facedetect.sh

