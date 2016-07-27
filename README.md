Wikipedia Relatedness Library
=============================


Setting Up
----------

Download from `http://law.di.unimi.it/software/download/` law-2.3 and its dependencies. Put them (unzipped) in the `lib` directory in `law-2.3` and `law-deps` directories, respectively.

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