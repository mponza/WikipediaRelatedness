#!/bin/bash

# Bash script for benchmarking all relatedness mea
#



# ======================================================================================================================
#
# Constants.
#
# ======================================================================================================================

GRAPHS=( "out" "in" "sym" )


# ======================================================================================================================
#
# Utility functions.
#
# ======================================================================================================================


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


# ======================================================================================================================
#
# Benchmarking functions.
#
# ======================================================================================================================


#
# Experiments Milne&Witten relatedness with different type of graphs.
#
function run_milnewitten {
    for graph in "${GRAPHS[@]}"
    do

        args="--name milnewitten --graph $graph"
        logging_info "Experimenting MilneWitten with paramters: $args\n"

        run_sbt "$args"

    done
}


#
# Experiments Jaccard relatedness with different type of graphs.
#
function run_jaccard {
    for graph in "${GRAPHS[@]}"
    do

        args="--name jaccard --graph $graph"
        logging_info "Experimenting Jaccard with paramters: $args\n"

        run_sbt "$args"

    done
}


#
# Experiments JaccardLocalClustering relatedness with different type of graphs.
#
function run_jaccardlocalclustering {
    for graph in "${GRAPHS[@]}"
    do

        args="--name jaccardlocalclustering --graph $graph"
        logging_info "Experimenting JaccardLocalClustering with paramters: $args\n"

        run_sbt "$args"

    done

}


#
# Experiments CosineLocalClustering relatedness with different types of graphs and thresholds.
#
function run_cosinelocalclustering {
    thresholds=( 0 10 50 100 500 1000 2000 5000 )

    for graph in "${GRAPHS[@]}"
    do
        for threshold in "${thresholds[@]}"
        do

            args="--name cosinelocalclustering --graph $graph --threshold $threshold"
            logging_info "Experimenting CosineLocalClustering with paramters: $args\n"

            run_sbt "$args"

        done
    done
}


#
# Experiments ESA relatedness with different thresholds.
#

function run_esa {
    thresholds=( 100 200 500 1000 1500 2000 2500 3000 5000 10000 )

    for threshold in "${thresholds[@]}"
    do

        args="--name esa --threshold $threshold"
        logging_info "Experimenting ESA with paramters: $args\n"

        run_sbt "$args"

    done
}


#
# Experiments SVD relatedness with different thresholds.
#

function run_esa {
    thresholds=( 10 50 100 150 200 )

    for threshold in "${thresholds[@]}"
    do

        args="--name svd --threshold $threshold"
        logging_info "Experimenting SVD with paramters: $args\n"

        run_sbt "$args"

    done
}


# ======================================================================================================================
#
# Main
#
# ======================================================================================================================

#run_milnewitten
#run_jaccard

#run_jaccardlocalclustering
#run_cosinelocalclustering

run_svd

# run_esa

