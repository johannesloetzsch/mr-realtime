(ns raspi-media.protocol
  (:require [clojure.spec.alpha :as s]))


(defprotocol Media
  "media rendered directly into the framebuffer"
  (help [self])
  (play+show [self])
  (stop+hide [self])
  (set-timeout [self]))

(s/def ::visible? boolean?)


(defprotocol Video
  (pause [self])
  (pause+hide [self])
  (continue+show [self] "continues when paused; in any case it will unhide")
  (get-duration [self])
  (get-position [self])
  (set-position [self pos-microseconds])
  (set-alpha [self alpha])
  (change-source [self new-source]))

(s/def ::playing? #{:playing :paused :stopped})
(s/def ::source keyword?)
(s/def ::video-state (s/keys :req-un [::visible? ::playing? ::source]))


(s/def ::audio-state (s/keys :req-un [::playing? ::source]))


(defprotocol Cam
  (get-singleframe [self]))

(s/def ::recording? boolean?)
(s/def ::cam-state (s/keys :req-un [::visible? ::recording?]))
