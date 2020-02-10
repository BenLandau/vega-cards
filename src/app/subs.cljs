(ns app.subs
  (:require
    [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::drawer-state
  (fn [db]
      (:drawer-open? db)))

(re-frame/reg-sub
  ::vizs
  (fn [db]
      (:visualisations db)))

(re-frame/reg-sub
  ::chart-types
  (fn [db]
      (:chart-types db)))

(re-frame/reg-sub
  ::agg-types
  (fn [db]
      (:agg-types db)))

(re-frame/reg-sub
  ::field-types
  (fn [db]
      (:field-types db)))
