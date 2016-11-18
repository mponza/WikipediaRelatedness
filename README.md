Wikipedia Relatedness Library
=============================

Requirements
------------

Resources files:

 * WikiSim dataset:

        dataset/wikiSim411.csv

 * Wikipedia graph:

        wikipedia/wiki-links-sorted.gz
        wikipedia/wikipedia-w2v-linkCorpus.json.gz

 * Embeddings:

        w2v/wikipedia-w2v-linkCorpus.e0.100.tr.bin
        w2v/wikipedia-w2v-deepWalk.e0.100.tr.bin
        w2v/wikipedia-w2v-deepWalkMixed.e0.100.tr.bin

 * Language Model:

        languageModel/wiki.binary
        
 * Freebase-Wikipedia mapping:
 
        src/main/resources/wikipedia/freebase-mapping.tsv


Setting Up
----------

Download from `http://law.di.unimi.it/software/download/` law-2.3 and its dependencies. Put them (unzipped) in the `lib` directory in `law-2.3` and `law-deps` directories, respectively.
Remove `webgraph`, `fastutil` and `sux4j` from  the `law-deps` directory.

If you are planning to use `CoSimRank`, please download [CoSimRankServer](https://github.com/mponza/CoSimRankServer) in the `lib` directory.
   
   
   
Python Configuration
--------------------

Some parts require Python processing, so I recommend to install some virtualization tool, like [virtualenv](http://docs.python-guide.org/en/latest/dev/virtualenvs/), before installing the corresponding dependencies.

Enable virtualenv and install requirements:
    
    virtualenv venv
    source venv/bin/activate
    
    pip install -r src/main/python/latent/requirements.txt
    
Warning: 'python' has to invoke the 2.7 interpreter.



NYT Dataset Generation
----------------------

Download the Google's salience annotation of the NYT documents:

    git clone https://github.com/dmorr-google/nyt-salience.git data/dataset/
    
Generates pairs according with the NYT sampling:

    python src/python/dataset nyt_salience_dataset
    
This will generate into `data/dataset/nyt-salience` `ss.csv, ns.csv, nn.csv` files.



Download Wikipedia types:

    wget http://downloads.dbpedia.org/2015-10/core-i18n/en/instance_types_en.ttl.bz2 src/ src/main/resources/wikipedia

And rename/compress it as `enwiki-20160305-instance-types-transitive-en.ttl.bz2`.


Enhance the dataset with distance and type information using the utils you can find into the `wikipediarelatedness/mapping` package.

After this step you have the `nyt-salience/*.csv` file with the following columns:

    srcWikiID,srcWikiTitle,srcWikiType,srcNYTFreq,dstWikiID,dstWikiTitle,dstWikiType,dstNYTFreq,coocc,class,outDist,symDist
        int        str         str         int       int       str          str         int      int   str   int     int

where the label `class` is assigned accorded to the cooccurrence frequency (`head` >= 25, `middle` in [15, 25) and `tail` in (10, 15)).
For balancing purpose, rows with cooccurrence <= 10 need to be removed.







WebGraph Processing
-------------------

Run `WikiWebGraph.generateBVGraphs` in order to process the Wikipedia graph with WebGraph and generatre the corresponding BVGraphs (in, out and symmetric).

LLP: tobedone



Latent Semantic Processing
--------------------------


Generates statistical information from the Wikipedia corpus:
    
    python src/main/python/latent process_wiki_corpus
    
Generate LDA model and process each Wikipedia page:

    python src/main/python/latent corpus_lda


For the eigenvector SVD generation on the Wikipedia graph just type:
    
    python src/main/python/latent graph_svd
    
For the Laplacian Moore-Penrose pseudoinverse:

    python src/main/python/latent laplacian_pinv
    
    

LINE Embeddings
---------------
    
Unzip Wikipedia graph and create the corresponding line directory:

    gunzip -k src/main/resources/wikipedia/wiki-links-sorted.gz
    mkdir src/main/resources/wikipedia/line

Install the `GSL` package:

    sudo apt-get install libgsl0ldbl

Go to `lib` directory, download LINE and run it: 
    
    cd lib
    git clone https://github.com/tangjianpku/LINE.git
    cd LINE/linux
    ./line -train ../../../src/main/resources/wikipedia/wiki-links-sorted -output  ../../../src/main/resources/wikipedia/line/line_size100_o2_n5 -binary 0 -size 100 -order 2 -negative 5
    
    



Explicit Semantic Processing
----------------------------

    
    

Clustering Coefficient Processing
---------------------------------

Download `triangles-1.1.jar` from `LAW` into `lib/triangle` folder.

    


    
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
    
    ...
    
    sbt "run-main it.unipi.di.acubelab.wikipediarelatedness.MainClass"

See `it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness` for the extended list of relatedness method avaiable.
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
    
Enable `virtualenv` and install the dependencies:

    virtualenv venv
    source venv/bin/activate
    pip install -r src/main/python/charts/requirements.txt

Generate charts:
    
    python src/main/python/charts/
    

(Running Jupyter on Mac OS X type `export PATH=/Users/marco/anaconda/bin:$PATH`)