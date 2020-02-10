(ns app.views
  (:require
    [app.events :as events]
    [app.subs :as subs]

    [re-frame.core :as re-frame]
    [reagent.core :as r]

    [aerial.hanami.core :as hmi]
    [aerial.hanami.common :as hc]
    [aerial.hanami.templates :as ht]
    ["@material-ui/core" :as mc]
    ["@material-ui/icons" :as mi]))

(def r> r/adapt-react-class)

(defn visualise [{:keys [data chart-type spec query]} num]
  (let [chart-types @(re-frame/subscribe [::subs/chart-types])]
      [hmi/vgl
       (->>
         (assoc spec :DATA data)
         (hc/xform (chart-type chart-types)))]))


(defn edit [q num]
      [:div
       [:textarea {:rows "15"
                   :cols "65"
                   :name "description"
                   :defaultValue q
                   :on-change #(re-frame/dispatch
                                 [::events/query-change num (-> % .-target .-value)])}]

       [(r/adapt-react-class mc/Button) {:variant "contained"
                                         :color "primary"
                                         :onClick #(do
                                                     (re-frame/dispatch [::events/merge-query num])
                                                     (re-frame/dispatch [::events/card-view-state num]))}
        "Submit"]])


(def buttons
  [["edit" (r/adapt-react-class mi/Edit)]
   ["view" (r/adapt-react-class mi/Timeline)]
   ["spec" (r/adapt-react-class mi/CropFree)]])


(defn view-spec [v s c num]
      [(r> mc/Grid) {:container true :spacing 2}
       [(r> mc/Grid) {:item true :md 12}
        [(r> mc/Grid) {:container true :spacing 2}
         (let [chart-types (vec (keys @(re-frame/subscribe [::subs/chart-types])))
               agg-types (vec (keys @(re-frame/subscribe [::subs/agg-types])))
               field-types (vec (keys @(re-frame/subscribe [::subs/field-types])))
               fields (vec (keys (first (:data v))))
               sel (fn ([num s k opts size event]
                        [(r> mc/Grid) {:item true
                                       :md   size}
                         [(r> mc/FormControl) {:style {:width         "100%"
                                                       :padding-right "30px"}}
                          [(r> mc/InputLabel) k]
                          [(r> mc/Select) {:value    (k s)
                                           :style    {:width         "100%"
                                                      :padding-right "30px"}
                                           :onChange #(re-frame/dispatch
                                                        [event num k (-> % .-target .-value)])}
                           (map
                             (fn [kw] [(r> mc/MenuItem) {:value kw} (name kw)])
                             opts)]]]))

               tf (fn [num s k size]
                      [(r> mc/Grid) {:item true
                                     :md   size}
                       [(r> mc/TextField) {:label        k
                                           :defaultValue (k s)
                                           :id           k
                                           :style        {:width         "100%"
                                                          :padding-right "30px"}
                                           :on-change    #(re-frame/dispatch
                                                            [::events/spec-text-change num k (-> % .-target .-value)])}]])]
              [:<>
               (tf num s :TITLE 12)
               (tf num s :BACKGROUND 4)
               (tf num s :WIDTH 4)
               (sel num c :chart-type chart-types 4 ::events/chart-change)
               (sel num s :X fields 6 ::events/spec-text-change)
               (sel num s :Y fields 6 ::events/spec-text-change)
               (sel num s :XTYPE field-types 6 ::events/spec-text-change)
               (sel num s :YTYPE field-types 6 ::events/spec-text-change)
               (sel num s :XAGG agg-types 6 ::events/spec-text-change)
               (sel num s :YAGG agg-types 6 ::events/spec-text-change)

               [(r> mc/Grid {:item true :md 12 } [(r> mc/Divider {:component "div"})])]

               ;; Colour field
               [(r> mc/Grid) {:item true
                              :md 6}
                [(r> mc/FormControl) {:style {:width "100%"
                                              :padding-right "30px"}}
                 [(r> mc/InputLabel) "Colour Field"]
                 [(r> mc/Select) {:value (get-in s [:COLOR :field])
                                  :onChange #(do
                                               (println "cheanged!!" num)
                                               (re-frame/dispatch
                                                 [::events/spec-text-change num [:COLOR :field] (-> % .-target .-value)]))}
                  (map
                    (fn [kw] [(r> mc/MenuItem) {:value kw} (name kw)])
                    fields)]]]

               ;; Colour Type
               [(r> mc/Grid) {:item true
                              :md 6}
                [(r> mc/FormControl) {:style {:width "100%"
                                              :padding-right "30px"}}
                 [(r> mc/InputLabel) "Field Colour Type"]
                 [(r> mc/Select) {:value (get-in s [:COLOR :type])
                                  :onChange #(re-frame/dispatch
                                               [::events/spec-text-change num [:COLOR :type] (-> % .-target .-value)])}
                  (map
                    (fn [kw] [(r> mc/MenuItem) {:value kw} (name kw)])
                    field-types)]]]])]]











       [(r/adapt-react-class mc/Grid) {:item true :md 12}
        [:div {:style {:padding-bottom "15px"}}
         [(r/adapt-react-class mc/Button) {:variant "contained"
                                           :color   "primary"
                                           :onClick #(do
                                                       (re-frame/dispatch [::events/merge-spec num])
                                                       (re-frame/dispatch [::events/card-view-state num]))}
          "Submit"]]]])


(defn vis-card [num
                {:keys
                 [data
                  chart-type
                  spec
                  temp-spec
                  temp-query
                  query
                  state] :as v}]
      [(r/adapt-react-class mc/Grid) {:item true
                                      :xs   6}
       [(r/adapt-react-class mc/Paper) {:square true}
        [:div {:style {:padding-left "25px"
                       :padding-top  "25px"
                       :display "flex"}}
         [:div {:style {:flexGrow 1}}
          [(r/adapt-react-class mc/Typography) {:variant "h4"} state]]
         [:div {:style {:display         "flex"
                        :justify-content "right"
                        :padding-right   "25px"}}
          (map
            (fn [[to-state button]]
                [(r/adapt-react-class mc/IconButton)
                 {:edge    "end"
                  :onClick #(re-frame/dispatch [::events/card-state num to-state])
                  :disabled (= state to-state)}
                 [button]])
            buttons)



          [(r/adapt-react-class mc/IconButton)
           {:edge    "end"
            :onClick #(re-frame/dispatch [::events/remove-viz num])}
           [(r/adapt-react-class mi/Delete)]]
          [(r/adapt-react-class mc/IconButton)
           {:edge    "end"
            :onClick #(re-frame/dispatch [::events/http-sql-data
                                          query num])}
           [(r/adapt-react-class mi/ArrowUpward)]]]]


        [:div {:style {:display      "flex"
                       :padding-left "30px"
                       :min-height   "250px"}}
         (case state
               "view" (visualise v num)
               "edit" (edit query num)
               "spec" (view-spec
                        v
                        (merge spec temp-spec)
                        {:chart-type (or (:temp-chart-type v) (:chart-type v))}
                        num))]]])


(defn cards []
      (let [v @(re-frame/subscribe [::subs/vizs])]
           [:> mc/Grid {:container true
                        :spacing   3}
            (map
              vis-card
              (range (count v))
              v)]))


(defn view-skeleton []
       [:div
        [:> mc/AppBar {:position "sticky"
                       :color    "inherit"}
         [:> mc/Toolbar
          [:> mc/IconButton {:edge    "start"
                             :onClick #(re-frame/dispatch [::events/toggle-drawer])}
           [:> mi/Menu]]
          [:div {:style {:flexGrow 1 :padding-left "25px"}}
           [:> mc/Typography {:variant "h5"} "Experiment Dashboard"]]
          [:> mc/IconButton {:edge "end"
                             :onClick #(re-frame/dispatch [::events/create-new-viz])}
           [:> mi/AddBox]]]]

        [:div {:style {:padding "25px 25px 25px 25px"}}
         (cards)]])


(def app-theme
  (mc/createMuiTheme
    (clj->js
      {:palette {:type "light"}
       :overrides
                {:MuiSelect
                 {:select
                  {"&:focus" {:backgroundColor "$labelcolor"}}}}})))


(defn main-view []
       [:<>
        [:> mc/CssBaseline
         [:> mc/MuiThemeProvider
          {:theme app-theme}
          (view-skeleton)]]])



