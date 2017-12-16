(ns raspi-media.motion
  "a working sample of motion detection in clojure"
  (:require [clojure.java.shell :refer [sh]]
            [clojure.core.async :refer [go-loop <! timeout]])
  (:import [org.opencv.core Core Mat MatOfDouble Algorithm Size]
           [org.opencv.highgui Highgui VideoCapture]
           [org.opencv.imgproc Imgproc]
           [org.opencv.video BackgroundSubtractorMOG BackgroundSubtractorMOG2]))
(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)

(defn preview [img w h]
  (let [out (Mat.)]
       (Imgproc/resize img out (Size. w h))
       (let [ret (.dump out)]
            (.release out)
            ret)))

(defn meanStdDev [img]
  (let [m (MatOfDouble.)
        d (MatOfDouble.)]
       (Core/meanStdDev img m d)
       (let [ret [(into [] (.toArray m))
                  (into [] (.toArray d))]]
            (.release m) (.release d)
            ret)))

(defn black+noise?
  "some captured pictures have been only black(+noise), so we want sort them out"
  [img]
  (let [[[m] [d]] (meanStdDev img)] ;; assuming (= 1 (.dimmensions img))
       ;(println d)
       (< d 1)))  ;; small deviation))

(defn snapshot
  "in this setup we get our samples from uv4l"
  []
  (let [r (sh "wget" "http://localhost:1141/stream/snapshot.jpeg" "-O" "/tmp/snapshot.jpeg")]
       (= 0 (:exit r))))

(defn reset []
  (def bsm1 (BackgroundSubtractorMOG.))
  (def bsm2 (BackgroundSubtractorMOG2.))
  (def fgm1 (Mat.))
  (def fgm2 (Mat.)))
(reset)

(defn snapshot->learn->fgm->motion?
  "this is the main part of motion detection"
  []
  (snapshot)
  (let [img (Highgui/imread "/tmp/snapshot.jpeg")
        img-gray (Mat.)]
       (Imgproc/resize img img (Size. 800 600))
       (Imgproc/cvtColor img img-gray Imgproc/COLOR_RGB2GRAY)
       (when-not (black+noise? img-gray)
         (Imgproc/GaussianBlur img img (Size. 11 11) 11)
         (Highgui/imwrite "/tmp/img.jpg" img)
         (.apply bsm1 img fgm1)
         (.apply bsm2 img fgm2)
         ;(println (preview fgm1 8 6))
         ;(println (preview fgm2 8 6))
         ;(Highgui/imwrite "/tmp/fgm1.jpg" fgm1)
         (Highgui/imwrite "/tmp/fgm2.jpg" fgm2)
         (let [size1 (.size fgm1)
               ret {:fgm-size [(.width size1) (.height size1)]
                    :fgm1-nonzero (Core/countNonZero fgm1)
                    :fgm1-percent (/ (* 100.0 (Core/countNonZero fgm1))
                                       (.area size1))
                    :fgm2-nonzero (Core/countNonZero fgm2)
                    :fgm2-percent (quot (* 100 (Core/countNonZero fgm2))
                                       (.area size1))}
               ret (assoc ret :motion? (and (< 10 (:fgm1-nonzero ret))
                                            (< 20 (:fgm2-percent ret) 80)))] 
              (.release img) (.release img-gray)
              ret))))

(defn motion-detection-future
 "does a motion detection step every t_ms"
 ([t_ms]
  (motion-detection-future t_ms println))
 ([t_ms f_out]
  (future (loop [] (f_out (snapshot->learn->fgm->motion?))
                   (Thread/sleep t_ms)
                   (recur)))))
