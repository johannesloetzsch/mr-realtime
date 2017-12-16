(ns raspi-media.impl.audio
  (:use [raspi-media.protocol])
  (:require [clojure.spec.alpha :as s]
            [raspi-media.config :refer [config]]
            [raspi-media.protocol :refer [Media]]
            [clojure.java.shell :refer [sh]]))

(defrecord Audio [state])

(extend-type Audio
  Media

    (help [self]
      (->> (concat (:sigs raspi-media.protocol/Media))
           vals
           (map :name)))

    (play+show [self]
      (stop+hide self)
      ;; works the same way with mpg321 or play (sox)
      (->> (future (sh "mpv" (get-in @config [:audio-sources (:source @(:state self))])))
           (swap! (:state self) assoc :process))
      (swap! (:state self) assoc :playing? :playing))

    (stop+hide [self]
      (if-let [p (:process @(:state self))]
              (future-cancel p))
      (swap! (:state self) assoc :process nil :playing? :stopped)))

(defn ->audio
  "constructor"
  [config]
  (let [state (atom (merge config {:process nil :playing? :stopped}))]
       (set-validator! state (partial s/valid? :raspi-media.protocol/audio-state)) 
       (Audio. state)))
