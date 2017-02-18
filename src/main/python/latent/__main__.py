import baker
import logging

from corpus import process_corpus

from analysis import generate_lda_model
from analysis import map_wikidocs2lda

from eigen import generate_eigenvectors

from commute_time import generate_laplacian_pseudoinverse



@baker.command
def corpus_lda(num_topics=100):
    '''
    Generates LDA model from Wikipedia.
    Maps every Wikipedia document to the corresponding
    latent model.
    '''
    #generate_lda_model(num_topics)
    map_wikidocs2lda(num_topics)


@baker.command
def process_wiki_corpus():
    '''
    Generates Wikipedia statisitcs that are subsequently used
    to generate the LDA model.
    '''
    process_corpus()


@baker.command
def wiki_lda(num_topics):
    process_corpus(num_topics)
    generate_lda_model(num_topics)
    map_wikidocs2lda(num_topics)


@baker.command
def graph_svd():
    '''
    Generates left and right eigenvectors of Wikipedia graph.
    '''
    generate_eigenvectors()


@baker.command
def laplacian_pinv():
    generate_laplacian_pseudoinverse()


if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',
                        level=logging.INFO)
    baker.run()
