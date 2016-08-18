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

If you are planning to use `CoSimRank`, please download [CoSimRankServer](https://github.com/mponza/CoSimRankServer) in the `lib` directory.
   
   
   
Processing
----------

Run webgraph processing classes in order to generate the graph and the LLP labels.
    
    
Benchmarking
------------
  
Run the `Bench` class with one following parameter:
  
    """{"relatedness": "Milne-Witten"}"""
    
    """{"relatedness": "Jaccard", "graph": "inGraph"}"""
    """{"relatedness": "Jaccard", "graph": "outGraph"}"""
    """{"relatedness": "Jaccard", "graph": "symGraph"}"""
    
    """{"relatedness": "w2v", "model": "corpus"}"""
    """{"relatedness": "w2v", "model": "deepWalk"}"""
    """{"relatedness": "w2v", "model": "deepCorpus"}"""

    """{"relatedness": "LocalClustering"}"""
    
    """{"relatedness": "MultiLLP"}"""
    
    """{"relatedness": "LMModel"}"""
    
    """{"relatedness": "CoSimRank", "graph": "deepCorpus"}"""
    """{"relatedness": "PPRCos", "graph": "deepCorpus"}"""  
    
    ...
    
    sbt "run-main it.unipi.di.acubelab.graphrel.MainClass"

See `it.unipi.di.acubelab.graphrel.wikipedia.relatedness` for the extended list of realtedness method avaiable.
LLP need the label generation (see `processing` package).


Finally, select the `Bench` option.


Analysis
--------

The analysis works in two step. First we generate the bucketized statistics and then we generate the plots.

Run sbt analysis with parameters:

    run """{
            "analysis": "Relatedness,InRatio,OutRatio,SymDistance,JaccardIn,JaccardOut,PageRank",
            "eval":"correlation,classification"
        }"""
    
[Enable](http://docs.python-guide.org/en/latest/dev/virtualenvs/) `virtualenv` and install the required dependencies:

    virtualenv venv
    source venv/bin/activate
    pip install -r src/main/python/charts/requirements.txt

Generate charts:
    
    python src/main/python/charts/
    

(Running Jupyter on Mac OS X type `export PATH=/Users/marco/anaconda/bin:$PATH`)