#!/bin/bash

#
# Bash script for benchmarking all relatedness mea


GRAPHS=( "out" "in" "sym" )


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
#
# subNodes=( "esa"   "sg" "dwsg" )

#subNodes=( "esa" "mw.in" "corpus400" "dw10")
subNodes=( "mw.in" "mw.sym" ) # "mw.out")
subSizes=( 30 )

weighters=( "milnewitten --weighterGraph in" "w2v --weighterModel w2v.corpus400" "w2v --weighterModel deepwalk.dw10" )



simRankers=( "csr" ) # "ppr" "csr" )
iterations=( 1 )  # 2 3 )
pprAlphas=( 0.1 )
csrDecays=( 0.9 )

#subNodes=( "esa" )
#subSizes=( 10 50 100 )

#weighters=( "milnewitten" )
#weighterGraphs=( "in" )
#weighterModels=( "w2v.sg" )

#simRankers=( "csr" )
#iterations=( 30 )
#pprDampings=( 0.1 )
#csrDecays=( 0.9 )

# WTF re-write it with recursion...
# check http://superuser.com/questions/318067/how-to-iterate-over-all-pair-combinations-in-a-list-in-bash
for nodes in "${subNodes[@]}"
do
    for size in "${subSizes[@]}"
    do
        for weight in "${weighters[@]}"
        do
            for simRank in "${simRankers[@]}"
            do
                for iters in "${iterations[@]}"
                do
                    for ppr in "${pprAlphas[@]}"
                    do
                        for csr in "${csrDecays[@]}"
                        do

                            args="--name sparse --subNodes $nodes --subSize $size --weighter $weight "
                            args+="--simRanker $simRank --iterations $iters --pprAlpha $ppr --csrDecay $csr"

                            logging_info "Experimenting Subgraph Relatedness with parameters: $args\n"

                            run_sbt "$args"
                        done
                    done
                done
            done
        done
    done
done