'''
Rewriting of gensim.corpora.wikicorpus.py where Wikipedia
is a json.gz file. Each line of the json file is a
Wikipedia page in the format:

    {
        'wikiTitle':    str
        'wikiId':       int
        'sentences':    [str]
    }

'''

from gensim.corpora import wikicorpus
from gensim import utils

import gzip
import multiprocessing
import json
import logging

logger = logging.getLogger('gensim.make_json_wiki.json')

def extract_json_pages(filename, filter_namespaces=False):
     with utils.smart_open(filename) as fin:
            for line in fin:
                document = json.loads(line.strip())

                title = document['wikiTitle']
                text = ' '.join(document['sentences'])
                wiki_id = str(document['wikiId'])

                yield title, text, wiki_id


class JsonWikiCorpus(wikicorpus.WikiCorpus):

    def __init__(self, fname, processes=None, lemmatize=utils.has_pattern(), dictionary=None, filter_namespaces=('0',)):
        super(JsonWikiCorpus, self).__init__(fname, processes, lemmatize, dictionary, filter_namespaces)

    def get_texts(self):
        articles, articles_all = 0, 0
        positions, positions_all = 0, 0
        texts = ((text, self.lemmatize, title, pageid) for title, text, pageid in extract_json_pages(self.fname, self.filter_namespaces))
        pool = multiprocessing.Pool(self.processes)

        for group in utils.chunkize(texts, chunksize=10 * self.processes, maxsize=1):
            for tokens, title, pageid in pool.imap(wikicorpus.process_article, group):  # chunksize=10):
                articles_all += 1
                positions_all += len(tokens)
                # article redirects and short stubs are pruned here
                if len(tokens) < wikicorpus.ARTICLE_MIN_WORDS or any(title.startswith(ignore + ':') for ignore in wikicorpus.IGNORED_NAMESPACES):
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
