(ns raspi-media.impl.camuv4l
  (:use [raspi-media.protocol])
  (:require [clojure.spec.alpha :as s]
            [raspi-media.protocol :refer [Media Cam]]
            [raspi-media.timeout :refer [timeout->callback]]
            [clojure.java.shell :refer [sh]]))

(defrecord CamUv4l+preview [state])

(extend-type CamUv4l+preview
  Media

    (help [self]
      (->> (concat (:sigs raspi-media.protocol/Media)
                   (:sigs raspi-media.protocol/Cam))
           vals
           (map :name)))

    (play+show [self]
      (stop+hide self)
      (->> (future (sh "curl" "http://localhost:1141/stream/video.mjpeg"))
           (swap! (:state self) assoc :process))
      (swap! (:state self) assoc :recording? true :visible? true)
      (set-timeout self))

    (stop+hide [self]
      (if-let [p (:process @(:state self))]
              (future-cancel p))
      (swap! (:state self) assoc :process nil :recording? false :visible? false))

    (set-timeout [self]
      (when (and (:on-timeout @(:state self)) (:ms-timeout @(:state self)))
            (timeout->callback :camuv4l
                               (:ms-timeout @(:state self))
                               (:on-timeout @(:state self)))))

  Cam

    (get-singleframe [self]
      "frame"))

(defn ->camUv4l+preview
  "constructor"
  [config]
  (let [state (atom (merge config {:process nil :visible? false :recording? false}))]
       (set-validator! state (partial s/valid? :raspi-media.protocol/cam-state)) 
       (CamUv4l+preview. state)))
