#!/usr/bin/env bash

sbt/bin/sbt clean compile

WIKI_GRAPH_TSV="/home/ponza/Developer/WikipediaRelatedness/data/wikipedia/graph/wiki-links-sorted.gz"

WIKI_GRAPH_OUT_BIN="/home/ponza/Developer/WikipediaRelatedness/data/wikipedia/graph/bin/wiki-out-graph.bin"
WIKI_GRAPH_IN_BIN="/home/ponza/Developer/WikipediaRelatedness/data/wikipedia/graph/bin/wiki-in-graph.bin"
WIKI_GRAPH_SYM_BIN="/home/ponza/Developer/WikipediaRelatedness/data/wikipedia/graph/bin/wiki-sym-graph.bin"

sbt/bin/sbt "runMain it.unipi.di.acubelab.wikipediarelatedness.WikiGraphProcessing \
            $WIKI_GRAPH_TSV \
            $WIKI_GRAPH_OUT_BIN \
            $WIKI_GRAPH_IN_BIN \
            $WIKI_GRAPH_SYM_BIN"
