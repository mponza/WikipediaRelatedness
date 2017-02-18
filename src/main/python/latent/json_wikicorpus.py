'''
Rewriting of gensim.corpora.wikicorpus.py where Wikipedia
is a json.gz file. Each line of the json file is a
Wikipedia page in the format:

    {
        'wikiTitle':    str
        'wikiId':       int
        'sentences':    [str]
    }

Furthermore, tokenize and lemmatize and process_article functions are changed
by removing some processing (our Wikipedia dump is already cleaned).
'''

# Copyright (C) 2010 Radim Rehurek <radimrehurek@seznam.cz>
# Copyright (C) 2012 Lars Buitinck <larsmans@gmail.com>
# Licensed under the GNU LGPL v2.1 - http://www.gnu.org/licenses/lgpl.html

from gensim.corpora import wikicorpus
from gensim import utils

import re
import gzip
import multiprocessing
import json
import logging

from pattern.en import parse

from latent_utils import extract_json_pages
from latent_utils import LEMMING


logger = logging.getLogger('JsonWikiCorpus')


def tokenize(text, lowercase=False, deacc=False, errors="strict", to_lower=False, lower=False):
    # In wikipedia-w2v-linkCorpus.json.gz text is already tokenized by spaces.

    # remove numbers and co.
    regexped = {match.group() for match in utils.PAT_ALPHABETIC.finditer(text)}

    # alwatys keep ent_wikiID tokens.
    return [word for word in text.split(' ')
            if word.startswith('ent_') or word in regexped]


def lemmatize(content, allowed_tags=re.compile('(NN|VB|JJ|RB)'), light=False,
        stopwords=frozenset(), min_length=2, max_length=15):
    '''
    Lemmatizes content where ent_wiki_ids are never removed. 
    '''
    content = (' ').join(tokenize(content, lower=True, errors='ignore'))

    parsed = parse(content, lemmata=True, collapse=False)
    result = []
    for sentence in parsed:
        for token, tag, _, _, lemma in sentence:

            if lemma.startswith('ent_') and lemma not in stopwords:
                # Wikipedia entity
                result.append(lemma.encode('utf8'))
                continue

            if min_length <= len(lemma) <= max_length and not lemma.startswith('_') and lemma not in stopwords:
                if allowed_tags.match(tag):
                    lemma += "/" + tag[:2]
                    result.append(lemma.encode('utf8'))
    return result


def process_article(args):
    """
    Parse a wikipedia article, returning its content as a list of tokens
    (utf8-encoded strings).

    @params args (text, to_lemmatize, title, pageid)
    """
    text, to_lemmatize, title, pageid = args
    if to_lemmatize:
        result = lemmatize(text)
    else:
        result = tokenize(text)

    return result, title, pageid


class JsonWikiCorpus(wikicorpus.WikiCorpus):

    def __init__(self, fname, processes=None, to_lemmatize=LEMMING, dictionary=None, filter_namespaces=('0',)):
        self.to_lemmatize = to_lemmatize  # avoid confusion between function and variable
        super(JsonWikiCorpus, self).__init__(fname, processes, to_lemmatize, dictionary, filter_namespaces)

    def get_texts(self, keep_wiki_ids=None):
        articles, articles_all = 0, 0
        positions, positions_all = 0, 0
        texts = ((text, self.to_lemmatize, title, pageid) for title, text, pageid in extract_json_pages(self.fname, self.filter_namespaces, keep_wiki_ids))
        pool = multiprocessing.Pool(self.processes)

        for group in utils.chunkize(texts, chunksize=10 * self.processes, maxsize=1):
            for tokens, title, pageid in pool.imap(process_article, group):  # chunksize=10):
                articles_all += 1
                positions_all += len(tokens)
                # article redirects and short stubs are pruned here
                if keep_wiki_ids is None and len(tokens) < wikicorpus.ARTICLE_MIN_WORDS or any(title.startswith(ignore + ':') for ignore in wikicorpus.IGNORED_NAMESPACES):
                    continue
                articles += 1
                positions += len(tokens)
                if self.metadata:
                    yield (tokens, (pageid, title))
                else:
                    yield tokens
        pool.terminate()

        logger.info(
            "finished iterating over Wikipedia corpus of %i documents with %i positions"
            " (total %i articles, %i positions before pruning articles shorter than %i words)",
            articles, positions, articles_all, positions_all, wikicorpus.ARTICLE_MIN_WORDS)
        self.length = articles  # cache corpus length
# endclass WikiCorpus
