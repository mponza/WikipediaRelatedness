![WikipediaRelatedness](http://pages.di.unipi.it/ponza/public/images/wikipediarelatedness/logo.png)

Branch for an immediate application of the Two-Stage Framework over every kind of graph. Typing only two commands.


## Setting Up

In your working directory, just type:

    git clone https://github.com/mponza/WikipediaRelatedness.git
    cd WikipediaRelatedness
    wget https://piccolo.link/sbt-0.13.17.zip; unzip sbt-0.13.17.zip; rm sbt-0.13.17.zip

for downloading this repository and [sbt](https://www.scala-sbt.org/).


## Indexing

First, you need to index and pre-process several resources for running the Two-Stage Framework. This can be automatically done with:

    src/main/bash/build.sh path/to/graph.tsv path/to/two-stage-data
    
where `graph.tsv` is the graph in tsv format (or tsv.gz) and `two-stage-data` is the directory that will host all resources that will be indexed for running the Two-Stage Framework.


## Running

You can compute the Two-Stage Framework relatedness over a set of query nodes by simply typing:

    src/main/bash/apply.sh k path/to/two-stage-data path/to/queries.tsv path/to/queries2rel.csv
    
where `k` is the size of the subgraph (the value used in the paper was fixed to 30), `two-stage-data` is the same directory provided in the Indexing step and `queries.tsv` are the list of query nodes in tsv format of which the relatedness needs to be computed and saved in `queries2rel.csv`.


## Examples

For a toy-example on a very small graph please check `src/main/bash/example.sh`.

For using the Two-Stage Framework in your own code just check `Main` class for examples.


## Datasets of Entity Relatedness Pairs

You can find the datasets `WikiSim` and `WiRe` in `src/main/resources/datasets/WikiSim.csv` and `src/main/resources/datasets/WiRe.csv`, respectively. 



## Citation and Further Reading

If you find any resource (code or data) of this repository useful, please cite our [paper](https://doi.org/10.1145/3132847.3132890):

> [Marco Ponza](http://pages.di.unipi.it/ponza), [Paolo Ferragina](http://pages.di.unipi.it/ferragina/), [Soumen Chakrabarti](https://www.cse.iitb.ac.in/~soumen/)  
> A Two-Stage Framework for Computing Entity Relatedness in Wikipedia  
> *In Proceedings of 26th International Conference on Information & Knowledge Management (CIKM 2017)*


## License

The code in this repository has been released under Apache License 2.0.
