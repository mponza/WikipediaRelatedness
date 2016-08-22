import os
import sys
import baker
import logging
import gensim
import bz2

from multiprocessing import cpu_count
from multiprocessing import Pool

from json_wikicorpus import extract_json_pages

from corpus import process_corpus

from analysis import generate_lda_model
from analysis import map_wikidocs2lda

from eigen import generate_eigenvectors


@baker.command
def corpus_lda():
    '''
    Generates LDA model from Wikipedia.
    Maps every Wikipedia document to the corresponding
    latent model.
    '''
    generate_lda_model()
    map_wikidocs2lda()


@baker.command
def process_wiki_corpus():
    '''
    Generates Wikipedia statisitcs that are subsequently used
    to generate the LDA model.
    '''
    process_corpus(to_lemmatize=False)


@baker.command
def wiki_lda():
    process_corpus(to_lemmatize=False)
    generate_lda_model()
    map_wikidocs2lda()


@baker.command
def graph_svd():
    '''
    Generates left and right eigenvectors of Wikipedia graph.
    '''
    generate_eigenvectors()


if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',
                        level=logging.INFO)
    baker.run()
