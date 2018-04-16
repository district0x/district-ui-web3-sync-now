(ns tests.all
  (:require [cljs-time.core :as t]
            [cljs.test :refer [deftest is testing run-tests async use-fixtures]]
            [day8.re-frame.test :refer [run-test-async run-test-sync wait-for]]
            [district.ui.now.events :as now-events]
            [district.ui.now.subs :as now-subs]
            [district.ui.now]
            [district.ui.web3-sync-now.events :as sync-now-events]
            [district.ui.web3-sync-now.utils :as sync-now-utils]
            [district.ui.web3-sync-now]
            [district.ui.web3.subs :as web3-subs]
            [district.ui.web3.events :as web3-events]
            [district.ui.web3]
            [mount.core :as mount]
            [re-frame.core :as re-frame]

            [cljs-web3.core :as web3]

            ))

(use-fixtures :each
  {:after (fn [] (mount/stop))})

(deftest tests
  (run-test-async
   (let [web3 (re-frame/subscribe [::web3-subs/web3])
         now (re-frame/subscribe [::now-subs/now])
         *prev-now* (atom nil)]

     (-> (mount/with-args {:web3 {:url "http://localhost:8549"}})
         (mount/start))

     (wait-for [::now-events/update-now]
       #_[::web3-events/web3-created]

       (prn "@B" @web3)

       (wait-for [::now-events/update-now]

         #_(prn "@A" @web3)
         (prn @now)

         (is (t/equal? @now (sync-now-utils/get-last-block-timestamp @web3)))
         (reset! *prev-now* @now)

         #_(re-frame/dispatch-sync [::sync-now-events/increment-now 8.64e+7])
         #_(wait-for [::now-events/update-now]
           (is (t/equal? @now (sync-now-utils/get-last-block-timestamp @web3)))
           (is (= 1 (t/in-days (t/interval @now @*prev-now*))))))))))
