(require '[reduce-fsm :as fsm])

(defmacro defsm+macros 
  "allows macros to be used in the definition of states"
  [defsm-type name states]
  (list defsm-type name (for [s states]
                             (cons (first s)
                                   (map macroexpand (rest s))))))



;; example guard + macro

(defn since-video-blau? [& args]
  (> (get-last-playback video :blau) 300))

(defmacro video-blau? []
  '("within-distance" :guard since-video-blau?))


;; example state machine + usage

(defsm+macros fsm/defsm-inc fsm-decide-action
  [[:video-default
     "movement" -> :video-default+sound
     "video-finished" -> :webcam
     (video-blau?) -> :video-blau]
   [:webcam
     "movement" -> :webcam+sound
     "webcam-timeout" -> :video-default
     (video-blau?) -> :video-blau]
   [:video-default+sound
     "no-movement" -> :video-default
     "video-finished" -> :webcam+sound
     (video-blau?) -> :video-blau]
   [:webcam+sound
     "no-movement" -> :webcam
     "webcam-timeout" -> :video-default+sound
     (video-blau?) -> :video-blau]
   [:video-blau
     "video-finished-pre" -> :trans-video-default]
   [:trans-video-default
     "webcam-timeout" -> :video-defaulti]])

(fsm/save-fsm-image fsm-decide-action "resources/fsm-decide-action.png")

(def fsm-state (atom (fsm-decide-action nil)))

(swap! fsm-state fsm-event "within-distance")
