#!/usr/bin/env bash

# Script for building the Two-Stage Framework (fastest and most effective configuration, i.e. Milne&Witten for
# top-k and weighting computation) on a graph and applying it over a set of nodes of the original graph.


# The graph with all nodes (e.g. Wikipedia graph) in tsv format. Every line represent an out-edge between two nodes.
# e.g. the line "src\tdst\n"  == src -> dst in the graph, where src and dst are two integers
WIKI_GRAPH_TSV=$1


# Directory where save pre-processed data of the Two-Stage Framework
DATA=$2

# Pre-processed graphs
WIKI_GRAPH_OUT_BIN="$DATA/graph/bin/out-graph.bin"
WIKI_GRAPH_IN_BIN="$DATA/graph/bin/in-graph.bin"
WIKI_GRAPH_SYM_BIN="$DATA/graph/bin/sym-graph.bin"

# Caches
TOP_CACHE="$DATA/cache/out-mw-top-nodes.bin"
WEIGHT_CACHE="$DATA/cache/mw-weights.bin"


# Methods

function processing() {
sbt/bin/sbt "runMain it.unipi.di.acubelab.wikipediarelatedness.WikiGraphProcessing \
            $WIKI_GRAPH_TSV \
            $WIKI_GRAPH_OUT_BIN \
            $WIKI_GRAPH_IN_BIN \
            $WIKI_GRAPH_SYM_BIN"
}

function topNodesCaching() {
    sbt/bin/sbt "runMain it.unipi.di.acubelab.wikipediarelatedness.TopNodesCaching \
    $WIKI_GRAPH_OUT_BIN \
    $WIKI_GRAPH_IN_BIN \
    $TOP_CACHE"
}

function weightCaching() {
    sbt/bin/sbt "runMain it.unipi.di.acubelab.wikipediarelatedness.WeightCaching \
    $WIKI_GRAPH_OUT_BIN \
    $WIKI_GRAPH_IN_BIN \
    $WIKI_GRAPH_SYM_BIN \
    $WEIGHT_CACHE"
}

# Main

processing
topNodesCaching
weightCaching
