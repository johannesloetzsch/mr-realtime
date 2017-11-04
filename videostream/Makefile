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
