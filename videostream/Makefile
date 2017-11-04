## This file shows different attempts for getting streaming to work…

QUALITY ?= 5

## could try:
## * https://www.linux-projects.org/uv4l/ -> webrtc or mjpeg
## * https://www.rs-online.com/designspark/building-a-raspberry-pi-2-webrtc-camera (gstreamer)

run:
	(sleep 5; epiphany http://raspberrypi.local:8080/javascript_simple.html) &
	make mjpg_streamer


mjpg_streamer:
	[ -d /tmp/stream/ ] || mkdir /tmp/stream/
	LD_LIBRARY_PATH=/usr/local/lib mjpg_streamer -i "input_file.so -f /tmp/stream -n pic.jpg" -o "output_http.so -w /usr/local/www" &
	#raspistill --nopreview -w 640 -h 480 -q 5 -o /tmp/stream/pic.jpg -tl 100 -t 9999999 -th 0:0:0
	raspistill --nopreview -q ${QUALITY} -o /tmp/stream/pic.jpg -tl 500 -t 9999999 -th 0:0:0
	## small delay

raspivid-netcat:
	raspivid --verbose --nopreview -t 0 -o - | netcat -l -p 5001
	## works fine
		## nc raspberrypi.local 5001 | mplayer -fps 31 -cache 1024 -

raspivid-cvlc:
	raspivid --verbose --nopreview -t 0 -o - | cvlc -vvv stream:///dev/stdin --sout '#rtp{sdp=rtsp://:8554/}' :demux=h264
		## vlc rtsp://raspberrypi.local:8554/
	## limited by rtsp@clvc or network
	## cpu is fine

raspivid-mplayer:
	raspivid -w 800 -h 600 -fps 31 --verbose --nopreview -t 0 -o - | mplayer - -cache 512 -fps 31
	## limited by cpu (by mplayer)

raspivid-omxplayer:
	[ -p /tmp/fifo ] || mkfifo /tmp/fifo
	raspivid --verbose --nopreview -t 0 -o /tmp/fifo & omxplayer /tmp/fifo
	## delayed

raspivid:
	raspivid -t 0
	## works fine
