(ns monetary-condolence-aggregator.core
  (:require [clj-yaml.core :as yaml]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(def funeral-offering-header
  ["氏名" "会社名・団体名" "郵便番号" "住所" "電話番号" "関係者" "関係性" "備考" "金額"])

(def funeral-offering-target
  [:name :company :zipcode :address :tel :whos_relationship :relationship :note :amount])

(defn calculate-total-amount
  [m]
  (->> m
       (mapcat (juxt :amount))
       (apply +)))

(defn get-table-footer
  [m]
  (let [total-mount (calculate-total-amount m)]
    (->> [(take (- (count funeral-offering-target) 2) (repeat nil)) "合計" total-mount]
         flatten
         (into []))))

(defn parse-funearal-offering
  [f]
  (let [parsed-data (->> f
                         io/resource
                         slurp
                         yaml/parse-string)
        table-body (mapv (apply juxt funeral-offering-target) parsed-data)
        table-footer (get-table-footer parsed-data)]
    (into
     (into [funeral-offering-business-header] table-body)
     [table-footer])))

(defn output-funeral-offering
  [f output]
  (with-open [w (io/writer output :encoding "MS932")]
    (->> f
         parse-funearal-offering
         (csv/write-csv w))))

;;;

(comment
  (output-funeral-offering "monetary_condolence_relative.yml" "monetary_condolence_relative.csv")
  )

;;;
