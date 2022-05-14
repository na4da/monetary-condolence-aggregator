(ns funeral-offering-list.core
  (:require [clj-yaml.core :as yaml]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(def funeral-offering-header
  ["氏名" "団体名" "郵便番号" "住所" "電話番号" "関係者" "関係性" "金額" "備考"])

(defn parse-funearal_offering-yaml-data
  [f]
  (->> f
       io/resource
       slurp
       yaml/parse-string
       (mapv (juxt :name :company :zipcode :address :tel :whos_relationship :relationship :amount :note))
       (into [funeral-offering-header])))

(defn output-funeral-offering-data
  [f output]
  (with-open [w (io/writer output)]
    (->> f
         parse-funearal_offering-yaml-data
         (csv/write-csv w))))

(comment
  (output-funeral-offering-data "funeral_offering_relative.yml" "output.csv")
  )
