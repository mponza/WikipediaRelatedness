#!/bin/bash

sizes=(200 300 400 500 1000)
for size in "${sizes[@]}"
do
    printf "[Bash] Running LDA with size $size..."
    python2.7 src/main/python/latent corpus_lda "$size"
done