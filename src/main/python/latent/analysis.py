'''
Generates LDA model after processing Wikipedia Corpus with Gensim (see corpus.py).
'''

import os
import logging
import gensim
import bz2
import gzip

from multiprocessing import cpu_count
from multiprocessing import Pool

from corpus import process_corpus

from json_wikicorpus import lemmatize

from latent_utils import extract_json_pages
from latent_utils import WIKI_FILENAME
from latent_utils import GENSIM_DIR


lda = None


def wiki2LDA(title, text, wiki_id):
    lemmatized_text = lemmatize(text)
    wikiLDA = lda[lemmatized_text]

    filename = os.path.join(WIKI_LDA_DIR, str(wiki_id))
    with gzip.open(filename, 'wb') as f:
        f.write(wiki_id)
        f.write('\t')
        print wikiLDA
        print str(wikiLDA)
        exit(1)
        f.write(wikiLDA)


def map_wikidocs2lda():
    logger = logging.getLogger('Wiki2LDA')
    pool = Pool(cpu_count())

    if lda is None:
        logger.info('Loading LDA model...')
        lda = gensim.models.loadqualcosa

    logger.info('Reading Wikipedia filename...')
    wiki_docs = extract_json_pages(WIKIPEDIA_FILENAME)

    logger.info('Mapping Wikipedia documents to LDA model...')
    pool.map(wiki2LDA, wiki_docs)

    logger.info('Reducing Wikipedia LDA document models to one single file...')


def generate_lda_model():
    logger = logging.getLogger('LDA')
    
    logger.info('Loading wordids file...')
    wordids_filename = WIKI_FILENAME + 'wordids.txt.bz2'
    id2word = gensim.corpora.Dictionary.load_from_text(wordids_filename)

    logger.info('Loading wordtfidf file...')
    wordtfidf_filename = WIKI_FILENAME + 'tfidf.mm'
    mm = gensim.corpora.MmCorpus(wordtfidf_filename)

    logger.info('Generating LDA model...')
    lda = gensim.models.LdaMulticore(corpus=mm, num_topics=100, id2word=id2word, chunksize=10000)

    logger.info('Saving LDA model...')
    lda_filename = os.path.join(GENSIM_DIR, 'lda.model')
    os.mkdirs(lda_filename)
    lda.save(lda_filename)
