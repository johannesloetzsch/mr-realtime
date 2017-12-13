start:	update
	make install
	make run

update:
	git pull origin master

install:	install_rsync install_python_gpio downloads

install_rsync:
	sudo rsync -av -P rsync-root/* /
	rsync -av -P rsync-home/.config ~/

install_python_gpio:
	sudo apt install python-rpi.gpio python-termcolor

install_uv4l:
	curl http://www.linux-projects.org/listing/uv4l_repo/lpkey.asc | sudo apt-key add -
	sudo apt update
	sudo apt install uv4l uv4l-raspicam uv4l-raspicam-extras uv4l-webrtc uv4l-demos
	sudo service uv4l_raspicam restart

install_clojure:
	sudo wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -O /usr/local/bin/lein
	sudo chmod +x /usr/local/bin/lein


# sudo apt-get install libgstreamer1.0-0 liborc-0.4-0 gir1.2-gst-plugins-base-1.0 gir1.2-gstreamer-1.0 gstreamer1.0-alsa gstreamer1.0-omx gstreamer1.0-plugins-bad gstreamer1.0-plugins-base gstreamer1.0-plugins-base-apps gstreamer1.0-plugins-good gstreamer1.0-plugins-ugly gstreamer1.0-pulseaudio gstreamer1.0-tools gstreamer1.0-x libgstreamer-plugins-bad1.0-0 libgstreamer-plugins-base1.0-0

# avconv -i dunson\ wsp\ 2.mov -s 1280x720 -c:v libx264 test.mp

downloads:
	downloader/downloader.sh 

run:
	(cd ~/src/raspi-media ; lein trampoline run :headless)
