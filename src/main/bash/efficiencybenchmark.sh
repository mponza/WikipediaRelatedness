#!/usr/bin/env bash

function run_sbt {
    args=$1
    ./sbt/bin/sbt "run $args"
}


mws=( "uncom.mw" "com.mw" )
dws=( "uncom.dw" "com.dw" )

mwdws=("${mws[@]}" "${dws[@]}")


for method in "${mwdws[@]}"
# Single Method Evaluation
     do
         printf "[Bash] Experimenting $method..."
         run_sbt "--name $method"
     done


# Algorithmic Scheme Evaluation
for mw in "${mws[@]}"
do
    for dw in "${dws[@]}"
    do
        printf "[Bash] $mw\n"
        printf "[Bash] $dw\n"
        printf "[Bash] Experimenting algo:${mw}+${dw}...\n"
        run_sbt "--name algo:${mw}+${dw}"
    done
done
