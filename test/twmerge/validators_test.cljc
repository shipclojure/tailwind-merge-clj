(ns twmerge.validators-test
  (:require
   [clojure.test :refer :all]
   [twmerge.validators :as v]))

(deftest validators
  (testing "isLength"
    (is (= (v/length? "1") true))
    (is (= (v/length? "1023713") true))
    (is (= (v/length? "1.5") true))
    (is (= (v/length? "1231.503761") true))
    (is (= (v/length? "px") true))
    (is (= (v/length? "full") true))
    (is (= (v/length? "screen") true))
    (is (= (v/length? "1/2") true))
    (is (= (v/length? "123/345") true))

    (is (= (v/length? "[3.7%]") false))
    (is (= (v/length? "481px") false))
    (is (= (v/length? "[481px]") false))

    (is (= (v/length? "[19.1rem]") false))
    (is (= (v/length? "[50vw]") false))
    (is (= (v/length? "[56vh]") false))
    (is (= (v/length? "[length:var(--arbitrary)]") false))
    (is (= (v/length? "1d5") false))
    (is (= (v/length? "[1]") false))
    (is (= (v/length? "[12px") false))
    (is (= (v/length? "12px]") false))
    (is (= (v/length? "one") false)))

  (testing "isArbitraryLength"
    (is (= (v/arbitrary-length? "[3.7%]") true))
    (is (= (v/arbitrary-length? "[481px]") true))
    (is (= (v/arbitrary-length? "[0.5px]") true))
    (is (= (v/arbitrary-length? "[19.1rem]") true))
    (is (= (v/arbitrary-length? "[50vw]") true))
    (is (= (v/arbitrary-length? "[56vh]") true))
    (is (= (v/arbitrary-length? "[length:var(--arbitrary)]") true))

    (is (= (v/arbitrary-length? "1") false))
    (is (= (v/arbitrary-length? "3px") false))
    (is (= (v/arbitrary-length? "1d5") false))
    (is (= (v/arbitrary-length? "[1]") false))
    (is (= (v/arbitrary-length? "[12px") false))
    (is (= (v/arbitrary-length? "12px]") false))
    (is (= (v/arbitrary-length? "one") false)))

  (testing "isInteger"
    (is (= (v/tw-integer? "1") true))
    (is (= (v/tw-integer? "123") true))
    (is (= (v/tw-integer? "8312") true))

    (is (= (v/tw-integer? "[8312]") false))
    (is (= (v/tw-integer? "[2]") false))
    (is (= (v/tw-integer? "[8312px]") false))
    (is (= (v/tw-integer? "[8312%]") false))
    (is (= (v/tw-integer? "[8312rem]") false))
    (is (= (v/tw-integer? "8312.2") false))
    (is (= (v/tw-integer? "1.2") false))
    (is (= (v/tw-integer? "one") false))
    (is (= (v/tw-integer? "1/2") false))
    (is (= (v/tw-integer? "1%") false))
    (is (= (v/tw-integer? "1px") false)))

  (testing "isArbitraryValue"
    (is (= (v/arbitrary-value? "[1]") true))
    (is (= (v/arbitrary-value? "[bla]") true))
    (is (= (v/arbitrary-value? "[not-an-arbitrary-value?]") true))
    (is (= (v/arbitrary-value? "[auto,auto,minmax(0,1fr),calc(100vw-50%)]") true))

    (is (= (v/arbitrary-value? "[]") false))
    (is (= (v/arbitrary-value? "[1") false))
    (is (= (v/arbitrary-value? "1]") false))
    (is (= (v/arbitrary-value? "1") false))
    (is (= (v/arbitrary-value? "one") false))
    (is (= (v/arbitrary-value? "o[n]e") false)))

  (testing "isTshirtSize"
    (is (= (v/tshirt-size? "xs") true))
    (is (= (v/tshirt-size? "sm") true))
    (is (= (v/tshirt-size? "md") true))
    (is (= (v/tshirt-size? "lg") true))
    (is (= (v/tshirt-size? "xl") true))
    (is (= (v/tshirt-size? "2xl") true))
    (is (= (v/tshirt-size? "2.5xl") true))
    (is (= (v/tshirt-size? "10xl") true))
    (is (= (v/tshirt-size? "2xs") true))
    (is (= (v/tshirt-size? "2lg") true))

    (is (= (v/tshirt-size? "") false))
    (is (= (v/tshirt-size? "hello") false))
    (is (= (v/tshirt-size? "1") false))
    (is (= (v/tshirt-size? "xl3") false))
    (is (= (v/tshirt-size? "2xl3") false))
    (is (= (v/tshirt-size? "-xl") false))
    (is (= (v/tshirt-size? "[sm]") false)))

  (testing "isArbitrarySize"
    (is (= (v/arbitrary-size? "[size:2px]") true))
    (is (= (v/arbitrary-size? "[size:bla]") true))
    (is (= (v/arbitrary-size? "[length:bla]") true))
    (is (= (v/arbitrary-size? "[length:200px_100px]") true))
    (is (= (v/arbitrary-size? "[percentage:bla]") true))

    (is (= (v/arbitrary-size? "[2px]") false))
    (is (= (v/arbitrary-size? "[bla]") false))
    (is (= (v/arbitrary-size? "size:2px") false)))

  (testing "isArbitraryPosition"
    (is (= (v/arbitrary-position? "[position:2px]") true))
    (is (= (v/arbitrary-position? "[position:bla]") true))

    (is (= (v/arbitrary-position? "[2px]") false))
    (is (= (v/arbitrary-position? "[bla]") false))
    (is (= (v/arbitrary-position? "position:2px") false)))

  (testing "isArbitraryImage"
    (is (= (v/arbitrary-image? "[url:var(--my-url)]") true))
    (is (= (v/arbitrary-image? "[url(something)]") true))
    (is (= (v/arbitrary-image? "[url:bla]") true))
    (is (= (v/arbitrary-image? "[image:bla]") true))
    (is (= (v/arbitrary-image? "[linear-gradient(something)]") true))
    (is (= (v/arbitrary-image? "[repeating-conic-gradient(something)]") true))

    (is (= (v/arbitrary-image? "[var(--my-url)]") false))
    (is (= (v/arbitrary-image? "[bla]") false))
    (is (= (v/arbitrary-image? "url:2px") false))
    (is (= (v/arbitrary-image? "url(2px)") false)))

  (testing "isArbitraryNumber"
    (is (= (v/arbitrary-number? "[number:black]") true))
    (is (= (v/arbitrary-number? "[number:bla]") true))
    (is (= (v/arbitrary-number? "[number:230]") true))
    (is (= (v/arbitrary-number? "[450]") true))

    (is (= (v/arbitrary-number? "[2px]") false))
    (is (= (v/arbitrary-number? "[bla]") false))
    (is (= (v/arbitrary-number? "[black]") false))
    (is (= (v/arbitrary-number? "black") false))
    (is (= (v/arbitrary-number? "450") false)))

  (testing "isArbitraryShadow"
    (is (= (v/arbitrary-shadow? "[0_35px_60px_-15px_rgba(0,0,0,0.3)]") true))
    (is (= (v/arbitrary-shadow? "[inset_0_1px_0,inset_0_-1px_0]") true))
    (is (= (v/arbitrary-shadow? "[0_0_#00f]") true))
    (is (= (v/arbitrary-shadow? "[.5rem_0_rgba(5,5,5,5)]") true))
    (is (= (v/arbitrary-shadow? "[-.5rem_0_#123456]") true))
    (is (= (v/arbitrary-shadow? "[0.5rem_-0_#123456]") true))
    (is (= (v/arbitrary-shadow? "[0.5rem_-0.005vh_#123456]") true))
    (is (= (v/arbitrary-shadow? "[0.5rem_-0.005vh]") true))

    (is (= (v/arbitrary-shadow? "[rgba(5,5,5,5)]") false))
    (is (= (v/arbitrary-shadow? "[#00f]") false))
    (is (= (v/arbitrary-shadow? "[something-else]") false)))

  (testing "isPercent"
    (is (= (v/percent? "1%") true))
    (is (= (v/percent? "100.001%") true))
    (is (= (v/percent? ".01%") true))
    (is (= (v/percent? "0%") true))

    (is (= (v/percent? "0") false))
    (is (= (v/percent? "one%") false))))
