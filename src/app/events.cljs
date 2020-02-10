(ns app.events
  (:require [re-frame.core :as re-frame]
            [app.db :as db]
            [ajax.core :as ajax]
            [clojure.pprint :refer [pprint]]))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
      (println "initialising db...")
      (println db/default-db)
      db/default-db))

(re-frame/reg-event-db
  ::toggle-drawer
  (fn [db _]
      (pprint (mapv #(into (sorted-map) %) (:visualisations db)))
      (let [old-state (:drawer-open? db)]
           (assoc db :drawer-open? (not old-state)))))


(re-frame/reg-event-db
  ::remove-viz
  (fn [db [_ idx]]
      (println "remove viz: " idx)
      (let [vizs (:visualisations db)
            updated (->>
                      vizs
                      (map vector (range))
                      (remove #(= (first %) idx))
                      (mapv second))]
           (assoc db :visualisations updated))))

(re-frame/reg-event-db
  ::card-state
  (fn [db [_ id state]]
      (assoc-in db [:visualisations id :state] state)))

(re-frame/reg-event-fx
  ::http-sql-data
  (fn [_world [_event query-form vis-id]]
      {:http-xhrio {:method :post
                    :uri "http://localhost:5000/data/applications"
                    :params {:query query-form}
                    :timeout 5000
                    :format (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success [::http-sql-data-success vis-id]
                    :on-failure [::http-sql-data-success vis-id]}}))

(re-frame/reg-event-db
  ::http-sql-data-success
  (fn [db [_ vis-id result]]
      (assoc-in db [:visualisations vis-id :data] result)))


(re-frame/reg-event-db
  ::merge-spec
  (fn [db [_ vis-id]]
      (->
        (assoc-in db [:visualisations vis-id :spec]
                  (merge
                    (get-in db [:visualisations vis-id :spec])
                    (get-in db [:visualisations vis-id :temp-spec])))
        (assoc-in [:visualisations vis-id :temp-spec] nil)
        (assoc-in [:visualisations vis-id :chart-type]
                  (or
                    (get-in db [:visualisations vis-id :temp-chart-type])
                    (get-in db [:visualisations vis-id :chart-type])))
        (assoc-in [:visualisations vis-id :temp-chart-type] nil))))

(re-frame/reg-event-db
  ::card-view-state
  (fn [db [_ vis-id]]
      (assoc-in db [:visualisations vis-id :state] "view")))

(re-frame/reg-event-db
  ::spec-text-change
  (fn [db [_ vis-id field value]]
      (println "num: " vis-id " id: " field " value: " value)
      (println (vec (flatten [:visualisations vis-id :temp-spec field])))
      (assoc-in db (vec (flatten [:visualisations vis-id :temp-spec field])) value)))

(re-frame/reg-event-db
  ::query-change
  (fn [db [_ vis-id query]]
      (assoc-in db [:visualisations vis-id :temp-query] query)))

(re-frame/reg-event-db
  ::chart-change
  (fn [db [_ vis-id _ chart]]
      (assoc-in db [:visualisations vis-id :temp-chart-type] (keyword chart))))

(re-frame/reg-event-db
  ::merge-query
  (fn [db [_ vis-id]]
      (->
        (assoc-in db [:visualisations vis-id :query]
                  (get-in db [:visualisations vis-id :temp-query]))
        (assoc-in [:visualisations vis-id :temp-query] nil))))


(re-frame/reg-event-db
  ::create-new-viz
  (fn [db _]
      (assoc db :visualisations
             (conj (:visualisations db) (:viz-template db)))))
