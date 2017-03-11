#!/bin/bash

sizes=(100 200 300 400)
for size in "${sizes[@]}"
do
    printf "[Bash] Running LDA with size $size..."
    python2.7 src/main/python/latent corpus_lda "$size"
done