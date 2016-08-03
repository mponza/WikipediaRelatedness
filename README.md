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
        w2v/wikipedia-w2v-deepWalkMixed.e0.100.tr.bin


 * Language Model:

        languageModel/wiki.binary


Setting Up
----------

Download from `http://law.di.unimi.it/software/download/` law-2.3 and its dependencies. Put them (unzipped) in the `lib` directory in `law-2.3` and `law-deps` directories, respectively.
Remove `webgraph`, `fastutil` and `sux4j` from  the `law-deps` directory.    
    
Benchmarking
------------
  
    run """{"relatedness": "Milne-Witten"}"""
    
    run """{"relatedness": "Jaccard", "graph": inGraph}"""
    run """{"relatedness": "Jaccard", "graph": outGraph}"""
    run """{"relatedness": "Jaccard", "graph": symGraph}"""
    ...
    
    sbt "run-main it.unipi.di.acubelab.graphrel.MainClass"

See `it.unipi.di.acubelab.graphrel.wikipedia.relatedness` for the extended list of realtedness method avaiable.
LLP need the label generation (see `processing` package).


and then choose `Bench` class.


Analysis
--------

Run sbt analysis with parameters:

    run """{"analysis": "InRatio"}"""
    run """{"analysis": "OutRatio"}"""
    run """{"analysis": "Relatedness"}"""
    
[Enable](http://docs.python-guide.org/en/latest/dev/virtualenvs/) `virtualenv` and install the required dependencies:

    virtualenv venv
    source venv/bin/activate
    pip install -r src/main/python/charts/requirements.txt

Generate charts:
    
    python src/main/python/charts/