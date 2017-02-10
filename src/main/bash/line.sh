#!/bin/bash


# Constants
train='data/wikipedia/dump/wiki-links-sorted' # checkme
outdir='data/wikipedia/neural/line'
threads=20


#
# Bash printing.
#
function logging_info {
    printf "[Bash] $1"
}


if [ ! -f "$train" ];  then
   logging_info 'Unzipping Wikipedia graph...'
   gunzip -k data/wikipedia/dump/wiki-links-sorted.gz
fi


# Pasted from https://github.com/tangjianpku/LINE
#
# -train, the input file of a network;
# -output, the output file of the embedding;
# -size, the dimension of the embedding; the default is 100;
# -order, the order of the proximity used; 1 for first order, 2 for second order; the default is 2;
# -negative, the number of negative samples used in negative sampling; the deault is 5;
# -samples, the total number of training samples (*Million);
# -rho, the starting value of the learning rate; the default is 0.025;
# -threads, the total number of threads used; the default is 1.


# Hyper-parameters
sizes=( 100 200 500 )
orders=( 1 2 )
negatives=( 1 2 5 10 )
samples=( 50 100 200 500 )
rhos=( 0.010 0.025 0.050 0.100 )


if [ ! -d "$outdir" ]; then
    logging_info 'Creating LINE output directory...'
    mkdir outdir # same path in reference.conf ('wikipediarelatedness.wikipedia.neural.line')
fi



for size in "${sizes[@]}"
do
    for order in "${orders[@]}"
    do
        for neg in "${negatives[@]}"
        do
            for sample in "${samples[@]}"
            do
                for rho in "${rhos[@]}"
                do

                    outfile="$outdir/embeddings_size:$size,order:$order,neg:$neg,sample:$sample,rho:$rho"

                    args="-train $train -output $outfile -binary 0 -size $size -order $order -negative $neg -sample $sample -rho $rho -threads $threads"

                    logging_info "Running LINE with parameters $args"
                    ./lib/LINE/linux/line $args

                    logging_info "Compressing LINE embeddings..."
                    gzip "$outfile"

                done
            done
        done
    done
done