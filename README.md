Wikipedia Relatedness Library
=============================

Requirements
------------

Resources files:

 * WikiSim dataset:

    dataset/wikiSim411.csv

 * Wikipedia graph:

    wikipedia/wiki-links-sorted.gz

 * Embeddings:

    w2v/wikipedia-w2v-linkCorpus.e0.100.tr.bin
    w2v/wikipedia-w2v-deepWalk.e0.100.tr.bin
    w2v/wikipedia-w2v-deepWalkMixed.e0.100.tr.b


 * Language Model:

    languageModel/wiki.binary


Setting Up
----------

Download from `http://law.di.unimi.it/software/download/` law-2.3 and its dependencies. Put them (unzipped) in the `lib` directory in `law-2.3` and `law-deps` directories, respectively.
Remove `webgraph`, `fastutil` and `sux4j` from  the `law-deps` directory.

Run `sbt` and then:

    compile
    
    
Benchmarking
------------
    
    run """{"relatedness": "Milne-Witten"}"""
    
    run """{"relatedness": "Jaccard", "graph": inGraph}"""
    run """{"relatedness": "Jaccard", "graph": outGraph}"""
    run """{"relatedness": "Jaccard", "graph": symGraph}"""
    run """{"relatedness": "Jaccard", "graph": noLoopSymGraph}"""
    
    sbt "run-main it.unipi.di.acubelab.graphrel.MainClass"


and then choose `Bench` class.