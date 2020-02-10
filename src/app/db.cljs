(ns app.db
  (:require [aerial.hanami.templates :as ht]
            [aerial.hanami.common :as hc]))

(def default-db
  {:state "Home"
   :drawer-open? false
   :agg-types {:sum "sum"
               :average "average"
               :count "count"
               :minimum "min"
               :maximum "max"
               :none hc/RMV}
   :field-types {:quantitative "quantitative"
                 :temporal "temporal"
                 :ordinal "ordinal"
                 :nominal "nominal"}



   :chart-types {:bar-chart ht/bar-chart
                 :line-chart ht/line-chart}

   :viz-template {:spec {:BACKGROUND "white"
                         :TITLE "New Chart"
                         :X          :label :XTYPE "ordinal"      :XAGG :none
                         :Y          :apps  :YTYPE "quantitative" :YAGG :none
                         :WIDTH      500}
                  :data  nil
                  :chart-type :bar-chart
                  :query nil
                  :state "view"}

   :visualisations [{:spec {:BACKGROUND "white"
                            :TITLE "Number of Applications by Experiment Group"
                            :X          :label :XTYPE "ordinal"      :XAGG :none
                            :Y          :apps  :YTYPE "quantitative" :YAGG :none
                            :WIDTH      500}
                     :data [{:label "test" :apps 99}
                            {:label "control" :apps 91}]
                     :chart-type :bar-chart
                     :query nil
                     :state "view"}
                    {:spec {:BACKGROUND "white"
                            :TITLE "Number of Applications by Experiment Group"
                            :X          :label :XTYPE "ordinal"      :XAGG :none
                            :Y          :apps  :YTYPE "quantitative" :YAGG :none
                            :WIDTH      500}
                     :data [{:label "test" :apps 99}
                            {:label "control" :apps 91}]
                     :chart-type :bar-chart
                     :query "select * from dummy_table;"
                     :state "view"}]})

