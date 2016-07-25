Wikipedia Relatedness Library
=============================


Setting Up
----------

Download from `http://law.di.unimi.it/software/download/` law-2.3 and its dependencies. Put them (unzipped) in the `lib` directory in `law-2.3` and `law-deps` directories, respectively.




Running
-------

    sbt compile
    sbt run
    
    
Benchmarking Examples
---------------------

Milne-Witten
    
    run """{"relatedness": "Milne-Witten"}"""
    
and then choose `Bench` class.