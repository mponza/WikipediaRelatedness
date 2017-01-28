#!/bin/bash


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
# Experiments CosineLocalClustering relatedness with different type of graphs.
#
function run_cosinelocalclustering {
    for graph in "${GRAPHS[@]}"
    do

        args="--name cosinelocalclustering --graph $graph"
        logging_info "Experimenting CosineLocalClustering with paramters: $args\n"

        run_sbt "$args"

    done

}

# ======================================================================================================================
#
# Main
#
# ======================================================================================================================

# run_milnewitten
#run_jaccard

run_jaccardlocalclustering
run_cosinelocalclustering