#!/usr/bin/env bash

sbt/bin/sbt clean compile

WIKI_GRAPH_TSV="/home/ponza/Developer/WikipediaRelatedness/data/wikipedia/graph/wiki-links-sorted.gz"

WIKI_GRAPH_OUT_BIN="/home/ponza/Developer/WikipediaRelatedness/data/wikipedia/graph/bin/wiki-out-graph.bin"
WIKI_GRAPH_IN_BIN="/home/ponza/Developer/WikipediaRelatedness/data/wikipedia/graph/bin/wiki-in-graph.bin"
WIKI_GRAPH_SYM_BIN="/home/ponza/Developer/WikipediaRelatedness/data/wikipedia/graph/bin/wiki-sym-graph.bin"

TOP_CACHE="/home/ponza/Developer/WikipediaRelatedness/data/wikipedia/topk/wiki-out-mw-top-nodes.bin"
WEIGHT_CACHE="/home/ponza/Developer/WikipediaRelatedness/data/wikipedia/weights/mw-weights.bin"


function processing() {
sbt/bin/sbt "runMain it.unipi.di.acubelab.wikipediarelatedness.WikiGraphProcessing \
            $WIKI_GRAPH_TSV \
            $WIKI_GRAPH_OUT_BIN \
            $WIKI_GRAPH_IN_BIN \
            $WIKI_GRAPH_SYM_BIN"
}

function milneWittenBenchmarking() {
    sbt/bin/sbt "runMain it.unipi.di.acubelab.wikipediarelatedness.WikiRelBenchmarking $WIKI_GRAPH_IN_BIN"
}

function twoStageFrameworkBenchmarking() {
    sbt/bin/sbt "runMain it.unipi.di.acubelab.wikipediarelatedness.TwoStageFrameworkBenchmarking \
    $WIKI_GRAPH_OUT_BIN\
    $WIKI_GRAPH_IN_BIN\
    30\
    $TOP_CACHE
    "
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
    $WIKI_GRAPH_OUT_BIN \
    $WEIGHT_CACHE"
}


