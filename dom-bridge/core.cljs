(ns dom-brige.core
  (:require-macros [hiccups.core :as hiccups :refer [html]]
                   [taoensso.timbre :refer [error]])
  (:require [hiccups.runtime]
            [domina :refer [nodes by-id append! attrs set-attrs! styles set-styles!]]
            [domina.css :refer [sel]]))

(defn add-hiccup-fn [add-fn]
  (fn [elements hiccups]
    (doseq [hiccup hiccups]
           (add-fn elements (html hiccup)))))

(defn set-props! [elements props+values]
  (doseq [element elements
          [prop value] props+values]
         (aset element (name prop) value)))

(defn call! [elements methods+args]
  (doseq [element elements
          [method args] methods+args]
         (let [tag (.-tagName element)
               method-kw (keyword method)]
              (cond
                (and (= method-kw :play) (= tag "VIDEO"))
                  (.play element)
                (and (= method-kw :pause) (= tag "VIDEO"))
                  (.pause element)
                :else
                  (error (str "Unknown method " method " on " tag))))))

(defn dom-updates! [dom-updates]
  (doseq [[selector selection updates] (js->clj dom-updates)]
         (let [selector-fn ({:by-id by-id
                             :sel sel}
                            (keyword selector))
               elements (if selector-fn (nodes (selector-fn selection)))]
               (print elements)
              (if (not elements)
                  (error (str "Not found: " selector " for " selection))
                  (doseq [[updater arg] updates]
                       (let [update-fn ({:append! (add-hiccup-fn append!)  ;; hiccup dom-elements
                                         :set-attrs! set-attrs!            ;; dom(ina) attrs
                                         :set-props! set-props!            ;; dom props
                                         :set-styles! set-styles!          ;; dom(ina) css styles
                                         :call! call!}                     ;; dom method calls
                                         (keyword updater))]
                            (if (not update-fn)
                                (error (str "Not found: " update-fn))
                                (update-fn elements arg) )))))))
