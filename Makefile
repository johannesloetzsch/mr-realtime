start:	update
	make install
	make run

update:
	git pull origin master

install:	install_rsync install_python_gpio downloads

install_rsync:
	rsync -av -P rsync-home/.config ~/

install_python_gpio:
	sudo apt install python-rpi.gpio python-termcolor

# sudo apt-get install libgstreamer1.0-0 liborc-0.4-0 gir1.2-gst-plugins-base-1.0 gir1.2-gstreamer-1.0 gstreamer1.0-alsa gstreamer1.0-omx gstreamer1.0-plugins-bad gstreamer1.0-plugins-base gstreamer1.0-plugins-base-apps gstreamer1.0-plugins-good gstreamer1.0-plugins-ugly gstreamer1.0-pulseaudio gstreamer1.0-tools gstreamer1.0-x libgstreamer-plugins-bad1.0-0 libgstreamer-plugins-base1.0-0

downloads:
	downloader/downloader.sh 

run:
	#QUALITY=2 make -C videostream
	#omxplayer /home/pi/Videos/* --loop --vol -12

