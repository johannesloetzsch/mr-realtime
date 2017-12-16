(ns raspi-media.impl.omxplayer
  (:use [raspi-media.protocol])
  (:require [clojure.spec.alpha :as s]
            [raspi-media.config :refer [config]]
            [raspi-media.timeout :refer [timeout->callback]]
            [clojure.java.shell :refer [sh]]
            [cheshire.core :refer [parse-string]]
            [clojure.core.async :refer [go-loop <! timeout]]
            [clj-time.local :as l]
            [clj-time.coerce :as tc]))


(defn omxplayer-web-start [source]
  (future (sh "omxplayer" "--no-osd" "--loop" (get-in @config [:video-sources source]))))  ;; for now we always want to loop

(defn omxplayer-web-dbus [& cmd+args]
  (apply sh "resources/omxwebgui-v2/dbus.sh" (map str cmd+args)))

(defn omxplayer-web-dbus-status []
  (-> (omxplayer-web-dbus "status")
      :out
      (parse-string true)))


(defrecord OmxPlayer [state])

(extend-type OmxPlayer
  Media

    (help [self]
      (->> (concat (:sigs raspi-media.protocol/Media)
                  (:sigs raspi-media.protocol/Video))
           vals
           (map :name)))

    (play+show [self]
      (stop+hide self)
      (->> (omxplayer-web-start (:source @(:state self)))
           (swap! (:state self) assoc :process))
      (swap! (:state self) assoc :playing? :playing :visible? true)
      (set-timeout self))

    (stop+hide [self]
      (if-let [p (:process @(:state self))]
              (future-cancel p))
      (omxplayer-web-dbus "stop")  ;; we assure there is no otherwise started / not stopped omxplayer instance; otherwise we can't control via dbus
      (swap! (:state self) assoc :process nil :playing? :stopped :visible? false)
      (swap! (:state self) assoc-in [:videos-last-playback (:source @(:state self))] (tc/to-epoch (l/local-now))))

    (set-timeout [self]
      ;; Duration and position are only available when player is running. This is why wait&retry in a go-loop.
      (go-loop []
        (<! (timeout 100))
        (let [duration (get-duration self)
              position (get-position self)
              offset_ns 1000]
             (if (and duration position)
                 ;(when (:on-timeout @(:state self))
                 (do
                       (timeout->callback :omxplayer-finished-pre
                                          (quot (- duration position (* 20 offset_ns)) 1000)
                                          (:on-timeout-finished-pre @(:state self)))
                       (timeout->callback :omxplayer-finished
                                          (quot (- duration position offset_ns) 1000)
                                          (:on-timeout-finished @(:state self))))
                 (recur)))))

  Video

    (pause [self]
      (if (= "Playing" (:status (omxplayer-web-dbus-status)))
          (omxplayer-web-dbus "pause")
          :already-paused)
      (swap! (:state self) assoc :playing? :paused))

    (pause+hide [self]
      (pause self)
      (omxplayer-web-dbus "hidevideo")
      (swap! (:state self) assoc :visible? false))

    (continue+show [self]
      (if (= "Paused" (:status (omxplayer-web-dbus-status)))
          (omxplayer-web-dbus "pause")
          :already-playing)
      (omxplayer-web-dbus "unhidevideo")
      (swap! (:state self) assoc :playing? :playing :visible? true))

    (get-duration [self]
      (-> (omxplayer-web-dbus-status)
          :duration
          (#(if % (Integer/parseInt %)))))

    (get-position [self]
      (-> (omxplayer-web-dbus-status)
          :position
          (#(if % (Integer/parseInt %)))))

    (set-position [self pos-microseconds]
      (omxplayer-web-dbus "setposition" pos-microseconds)
      (set-timeout self)
      (comment "when paused, we need unpause+pause to update the screen"))
    
    (set-alpha [self alpha]
      (omxplayer-web-dbus "setalpha" alpha))

    (change-source [self new-source]
      (if-let [p (get-position self)]
              (swap! (:state self) assoc-in [:videos-position-when-paused (:source @(:state self))] p))
      (swap! (:state self) assoc :source new-source)
      (play+show self)))

(defn ->omxPlayer
  "constructor"
  [config]
  (let [state (atom (merge config {:process nil
                                   :visible? false
                                   :playing? :stopped
                                   :videos-position-when-paused {}
                                   :videos-loop-count {}
                                   :videos-last-playback {}}))]
       (set-validator! state (partial s/valid? :raspi-media.protocol/video-state)) 
       (OmxPlayer. state)))


(defn get-last-playback [player source]
  (- (tc/to-epoch (l/local-now))
     (get-in @(:state player) [:videos-last-playback source] ##-Inf)))
