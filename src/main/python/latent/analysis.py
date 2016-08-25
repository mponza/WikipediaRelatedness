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
from json_wikicorpus import JsonWikiCorpus

from latent_utils import extract_json_pages
from latent_utils import WIKI_FILENAME
from latent_utils import WIKI_LDA_DIR
from latent_utils import WIKI_LDA_MODEL
from latent_utils import WIKI_CORPUS


lda = None
logger = logging.getLogger('LDA')


def wiki2LDA(wiki_doc):
    '''
    Writes in tmp directory a file where the name is the wiki_id and
    the content is the lda embedding in the following format (no spaces):

        topic_id \t topic_probability \n
    '''
    wiki_id = wiki_doc[0]
    text = wiki_doc[1]

    bow = lda.id2word.doc2bow(bow)
    wikiLDA = lda[bow]

    tmp = os.path.join(WIKI_LDA_DIR, 'tmp')
    filename = os.path.join(tmp, str(wiki_id))

    with gzip.open(filename, 'wb') as f:
        for topic_id, prob in wikiLDA:
            f.write(str(topic_id) + '\t' + str(prob) + '\n')


def load_wiki_documents():
    '''
    Returns [(wiki_id, processed_tokens)] as defined in JsonWikiCorpus.
    TODO: have a global to_lemmatize field.
    '''
    logger.info('Loading Wikipedia documents...')

    wiki_corpus = JsonWikiCorpus(WIKI_CORPUS, to_lemmatize=False)
    wiki_corpus.metadata = True

    wiki_docs = []
    for meta_texts in wiki_corpus.get_texts():
        wiki_id = meta_texts[1][0]
        text = meta_texts[0]
        wiki_docs.append((wiki_id, text))

    logger.info('Wikipedia documents loaded.')

    wiki_docs


def map_wikidocs2lda():
    global lda

    logger = logging.getLogger('Wiki2LDA')
    pool = Pool(cpu_count())

    if lda is None:
        logger.info('Loading LDA model...')
        lda = gensim.models.ldamodel.LdaState.load(WIKI_LDA_MODEL)

    wiki_docs = load_wiki_documents()
    logger.info('Mapping Wikipedia documents to LDA model...')
    pool.map(wiki2LDA, wiki_docs)

    logger.info('Reducing Wikipedia LDA document models to one single file...')


def generate_lda_model():
    global lda
    
    logger.info('Loading wordids file...')
    wordids_filename = WIKI_FILENAME + 'wordids.txt.bz2'
    id2word = gensim.corpora.Dictionary.load_from_text(wordids_filename)

    logger.info('Loading wordtfidf file...')
    wordtfidf_filename = WIKI_FILENAME + 'tfidf.mm'
    mm = gensim.corpora.MmCorpus(wordtfidf_filename)

    logger.info('Generating LDA model...')
    lda = gensim.models.LdaMulticore(corpus=mm, num_topics=num_topics,
                                     id2word=id2word, chunksize=10000)

    logger.info('Saving LDA model...')
    if not os.path.isdir(WIKI_LDA_DIR):
        os.makedirs(WIKI_LDA_DIR)
    lda.save(WIKI_LDA_MODEL)
