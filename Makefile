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

downloads:
	downloader/downloader.sh 

run:
	#QUALITY=2 make -C videostream
	omxplayer /home/pi/Videos/* --loop --vol -12

