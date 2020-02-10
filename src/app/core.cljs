(ns app.core
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [day8.re-frame.http-fx]


    [app.events :as events]
    [app.views :as views]
    [app.config :as config]
    
    ["@material-ui/core" :as mc]))



(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-view]
                  (.getElementById js/document "app")))

(defn main []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
