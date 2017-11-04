start:	update install run

update:
	git pull origin master

install:	install_rsync install_python_gpio

install_rsync:
	rsync -av -P rsync-home/.config ~/

install_python_gpio:
	sudo apt install python-rpi.gpio python-termcolor

run:
	#QUALITY=2 make -C videostream
	omxplayer /home/pi/Videos/*

