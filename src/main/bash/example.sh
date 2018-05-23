#!/usr/bin/env bash


GRAPH=src/main/resources/example/toy-graph.tsv
DATA=/tmp/two-stage-data

K=2
QUERY_PAIRS=src/main/resources/example/toy-queries.tsv
RELS=/tmp/toy-queries2rels.tsv


echo "Indexing graph and cache generation..."
bash src/main/bash/build.sh $GRAPH $DATA

echo "Querying Two-Stage Framework..."
bash src/main/bash/query.sh $K $DATA $QUERY_PAIRS $RELS


echo "Head of the computed relatedness file is"
head $RELS