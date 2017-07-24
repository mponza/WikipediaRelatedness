#!/usr/bin/env bash

#curl --data "{\"text\": \"silvio berlusconi matteo renzi\"}" http://relatedness.mkapp.it/text
#curl --data "{\"text\": \"data compression\"}" http://relatedness.mkapp.it/text

URL="http://localhost:7000"


METHODS=( "jaccard" "milnewitten" "2stage-mw" "2stage-mwdw" )

for method in "${METHODS[@]}"
do
    # Berlusconi 26909
    # Renzi 23388798
    # Buisness 39206
    # Bankruptcy 4695



    #curl --data "{\"srcWikiID\": 26909, \"dstWikiID\": 23388798, \"method\": \"$method\" }" http://relatedness.mkapp.it/rel
    #curl --data "{\"srcWikiID\": 39206, \"dstWikiID\": 4695, \"method\": \"$method\" }" http://relatedness.mkapp.it/rel

    curl --request POST "$URL/rel"  --data "srcWikiID=26909&dstWikiID=23388798&method=$method"
    curl --request POST "$URL/rel"  --data "srcWikiID=39206&dstWikiID=4695&method=$method"

    curl --request POST "$URL/rank"  --data "srcWikiID=26909&method=$method"
    curl --request POST "$URL/rank"  --data "srcWikiID=39206&method=$method"
    curl --request POST "$URL/rank"  --data "srcWikiID=4695&method=$method"
    curl --request POST "$URL/rank"  --data "srcWikiID=23388798&method=$method"

    #curl --data "{\"srcWikiID\": 26909, \"method\": \"$method\" }" http://relatedness.mkapp.it/rank
    #curl --data "{\"srcWikiID\": 39206, \"method\": \"$method\" }" http://relatedness.mkapp.it/rank
    #curl --data "{\"srcWikiID\": 4695, \"method\": \"$method\" }" http://relatedness.mkapp.it/rank
    #curl --data "{\"srcWikiID\": 23388798, \"method\": \"$method\" }" http://relatedness.mkapp.it/rank

done
