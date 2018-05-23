#!/usr/bin/env bash

# Script for building the Two-Stage Framework (fastest and most effective configuration, i.e. Milne&Witten for
# top-k and weighting computation) on a graph and applying it over a set of nodes of the original graph.

# Size of the subgraph that is created around two query entities for the Two-Stage relatedness computation
K=$1  # suggested value: 30

# Directory where save pre-processed data of the Two-Stage Framework
DATA=$2


# TSV file (same format of GRAPH) of a subset of nodes for which we want to compute the relatedness
QUERY_PAIRS=$3

# Output file where save the computed relatedness
OUTPUT=$4


# Pre-processed graphs
WIKI_GRAPH_OUT_BIN="$DATA/graph/bin/out-graph.bin"
WIKI_GRAPH_IN_BIN="$DATA/graph/bin/in-graph.bin"
WIKI_GRAPH_SYM_BIN="$DATA/graph/bin/sym-graph.bin"

# Caches
TOP_CACHE="$DATA/cache/out-mw-top-nodes.bin"
WEIGHT_CACHE="$DATA/cache/mw-weights.bin"


sbt/bin/sbt clean compile
sbt/bin/sbt "runMain it.unipi.di.acubelab.wikipediarelatedness.ApplyTwoStageFramework \
            --outgraph $WIKI_GRAPH_OUT_BIN \
            --ingraph $WIKI_GRAPH_IN_BIN \
            --cachetopnodes $TOP_CACHE \
            --cacheweights $WEIGHT_CACHE \
            --k $K \
            --querypairs $QUERY_PAIRS \
            --output $OUTPUT"


