simple-java-example-for-testing:	#localrepo
	javac -cp ~/.m2/repository/opencv/opencv/2.4.13/opencv-2.4.13.jar Test.java
	LD_LIBRARY_PATH=/home/pi/Downloads/opencv/build/lib/ java -cp ~/.m2/repository/opencv/opencv/2.4.13/opencv-2.4.13.jar:. Test

localrepo:
	## -> https://docs.opencv.org/2.4/doc/tutorials/introduction/clojure_dev_intro/clojure_dev_intro.html
	#cd build

	mkdir -p native/linux/arm
	cp lib/libopencv_java2413.so native/linux/arm/
	jar -cMf opencv-native-2413.jar native

	lein localrepo install bin/opencv-2413.jar opencv/opencv 2.4.13
	lein localrepo install opencv-native-2413.jar opencv/opencv-native 2.4.13
	
	## now the project.clj can use:
	## [opencv/opencv "2.4.13"]
	## [opencv/opencv-native "2.4.13"]
	## :injections [(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)]

build-opencv:
	## try install from repository first; building on pi was needed because of: https://github.com/opencv/opencv/issues/10080

	## build opencv -> http://bigdinotech.com/tutorials/beaglebone-black-tutorials/building-opencv-on-the-beaglebone-black-or-raspberry-pi/
	#wget …
	#mkdir build; cd build
	export JAVA_HOME=/usr/lib/jvm/java-1.9.0-openjdk-armhf/
	sudo ln -s  /usr/lib/jvm/java-9-openjdk-armhf/include/linux/jni_md.h /usr/lib/jvm/java-9-openjdk-armhf/include
	
	## WITH_FFMPEG=OFF -> https://github.com/opencv/opencv/issues/10080
	cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/local -D BUILD_EXAMPLES=OFF -DWITH_FFMPEG=OFF -D JAVA_AWT_INCLUDE_PATH=${JAVA_HOME}/include ..
	sudo make install
