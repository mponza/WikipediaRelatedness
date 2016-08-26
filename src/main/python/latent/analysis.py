'''
Generates LDA model after processing Wikipedia Corpus with Gensim (see corpus.py).
'''

import os
import logging
import gensim
from smart_open import smart_open
import gzip

from gensim import utils

from multiprocessing import cpu_count
from multiprocessing import Pool

from corpus import process_corpus

from json_wikicorpus import lemmatize
from json_wikicorpus import JsonWikiCorpus

from latent_utils import WIKI_FILENAME
from latent_utils import WIKI_LDA_DIR
from latent_utils import WIKI_LDA_MODEL
from latent_utils import WIKI_CORPUS
from latent_utils import WIKI_LDA_DOCS


logger = logging.getLogger('LDA')


def wiki2LDA(lda_task):
    '''
    Writes in tmp directory a file where the name is the wiki_id and
    the content is the lda embedding in the following format (no spaces):

        topic_id \t topic_probability \n
    '''
    lda, wiki_doc = lda_task

    wiki_id = wiki_doc[0]
    text = wiki_doc[1]

    bow = lda.id2word.doc2bow(text)
    wikiLDA = lda[bow]

    return (wiki_id, wikiLDA)

    tmp = os.path.join(WIKI_LDA_DIR, 'tmp')
    if not os.path.exists(tmp):
        os.makedirs(tmp)
    filename = os.path.join(tmp, str(wiki_id))

    with gzip.open(filename, 'wb') as f:
        for topic_id, prob in wikiLDA:
            f.write(str(topic_id) + '\t' + str(prob) + '\n')



def wiki_document_generator():
    '''
    Returns [(wiki_id, processed_tokens)] as defined in JsonWikiCorpus.
    TODO: have a global to_lemmatize field.
    '''

    wiki_corpus = JsonWikiCorpus(WIKI_CORPUS, to_lemmatize=False, dictionary={})
    wiki_corpus.metadata = True

    wiki_docs = []
    for meta_texts in wiki_corpus.get_texts():

        wiki_id = meta_texts[1][0]
        text = meta_texts[0]

        yield (wiki_id, text)


def map_wikidocs2lda():

    logger = logging.getLogger('Wiki2LDA')
    pool = Pool(cpu_count())

    logger.info('Loading LDA model...')
    lda = gensim.models.ldamodel.LdaState.load(WIKI_LDA_MODEL)

    logger.info('Mapping Wikipedia documents to LDA model...')
    with smart_open(WIKI_LDA_DOCS, 'wb') as f:
        n = 0

        for wiki_docs in utils.chunkize(wiki_document_generator(), 1000):

            lda_wiki_docs = pool.map(wiki2LDA, [(lda, wiki_doc) for wiki_doc in wiki_docs])

            # Writes to file as 'wiki_id \t index:value \n' format.
            for wiki_id, lda_text in lda_wiki_docs:
                f.write(str(wiki_id) + '\t')

                embedding = ['{0}:{1:.8f}'.format(index, value) 
                             for index, value in lda_text]
                f.write('\t'.join(embedding) + '\n')

            n += len(wiki_docs)
            logger.info('{0} documents mapped to LDA embedding.'.format(n))


def generate_lda_model():
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
