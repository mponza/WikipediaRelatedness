#!/bin/bash

#
# Bash script for benchmarking all relatedness mea


GRAPHS=( "in" )


#
# Bash printing.
#
function logging_info {
    printf "[Bash] $1"
}


#
# Runs sbt with the specified arguments.
#
function run_sbt {
    args=$1
    ./sbt/bin/sbt "run $args"
}





#
# Experiments based on clique graph generation.

subNodes=( "esa" )
subSizes=( 30 )

weighters=( "--firstname milnewitten --firstgraph in --secondname w2v --secondmodel w2v.corpus400"
            "--firstname milnewitten --firstgraph in --secondname w2v --secondmodel deepwalk.dw10"
            "--firstname w2v --firstmodel w2v.corpus400 --secondname w2v --secondmodel deepwalk.dw10"
            )

lambdas=( 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 )


for weight in "${weighters[@]}"
do

    args="--name sparse --subNodes $nodes --subSize $size --weighter mix $weight "
    args+="--simRanker csr --iterations 1 --pprAlpha 0.1 --csrDecay 0.9"

    logging_info "Experimenting Clique Relatedness with parameters: $args\n"

    run_sbt "$args"
done

