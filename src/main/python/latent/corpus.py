'''
Rewriting of gensim.scripts.make_wikicorpus.py for managing
Wikipedia as json.gz file. More information can be found 
in json_wikicorpus.py.
'''

# Copyright (C) 2010 Radim Rehurek <radimrehurek@seznam.cz>
# Copyright (C) 2012 Lars Buitinck <larsmans@gmail.com>
# Licensed under the GNU LGPL v2.1 - http://www.gnu.org/licenses/lgpl.html

import logging
import os.path
import sys

from gensim.corpora import Dictionary, HashDictionary, MmCorpus, WikiCorpus
from gensim.models import TfidfModel

from json_wikicorpus import JsonWikiCorpus

from latent_utils import WIKI_CORPUS
#from latent_utils import GENSIM_DIR to conclude refactoring
from latent_utils import WIKI_STATS
from latent_utils import LEMMING


DEFAULT_DICT_SIZE = 100000
GENSIM_DIR = 'tbd...' # fix and refactor me


def process_corpus(input_filename=WIKI_CORPUS, output_dir=GENSIM_DIR,
                         online=False, to_lemmatize=LEMMING, debug=True):
    program = 'GensimWikiCorpus'
    logger = logging.getLogger(program)

    inp = input_filename
    # twice because model will be saved into directory/prefixfilenames
    outp = os.path.join(output_dir, WIKI_STATS + '/' + WIKI_STATS)

    if not os.path.isdir(os.path.dirname(outp)):
        os.makedirs(outp)

    keep_words = DEFAULT_DICT_SIZE

    if online:
        dictionary = HashDictionary(id_range=keep_words, debug=debug)
        dictionary.allow_update = True # start collecting document frequencies
        wiki = JsonWikiCorpus(inp, to_lemmatize=to_lemmatize, dictionary=dictionary)
        MmCorpus.serialize(outp + '_bow.mm', wiki, progress_cnt=10000) # ~4h on my macbook pro without lemmatization, 3.1m articles (august 2012)
        # with HashDictionary, the token->id mapping is only fully instantiated now, after `serialize`
        dictionary.filter_extremes(no_below=20, no_above=0.1, keep_n=DEFAULT_DICT_SIZE)
        dictionary.save_as_text(outp + '_wordids.txt.bz2')
        wiki.save(outp + '_corpus.pkl.bz2')
        dictionary.allow_update = False
    else:
        wiki = JsonWikiCorpus(inp, to_lemmatize=to_lemmatize) # takes about 9h on a macbook pro, for 3.5m articles (june 2011)
        # only keep the most frequent words (out of total ~8.2m unique tokens)
        wiki.dictionary.filter_extremes(no_below=20, no_above=0.1, keep_n=DEFAULT_DICT_SIZE)
        # save dictionary and bag-of-words (term-document frequency matrix)
        MmCorpus.serialize(outp + '_bow.mm', wiki, progress_cnt=10000) # another ~9h
        wiki.dictionary.save_as_text(outp + '_wordids.txt.bz2')
        # load back the id->word mapping directly from file
        # this seems to save more memory, compared to keeping the wiki.dictionary object from above
        dictionary = Dictionary.load_from_text(outp + '_wordids.txt.bz2')
    del wiki

    # initialize corpus reader and word->id mapping
    mm = MmCorpus(outp + '_bow.mm')

    # build tfidf, ~50min
    tfidf = TfidfModel(mm, id2word=dictionary, normalize=True)
    tfidf.save(outp + '.tfidf_model')

    # save tfidf vectors in matrix market format
    # ~4h; result file is 15GB! bzip2'ed down to 4.5GB
    MmCorpus.serialize(outp + '_tfidf.mm', tfidf[mm], progress_cnt=10000)

    logger.info("finished running %s" % program)
