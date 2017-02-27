'''
Original code kindly provided by Francesco.
'''

import logging

from gensim.utils import RULE_KEEP, RULE_DEFAULT, smart_open, to_unicode
from gensim.models.word2vec import LineSentence
from gensim.models.doc2vec import Doc2Vec, TaggedDocument

from w2v_utils import d2v_filename
from w2v_utils import wikicorpus_filename



class JsonCorpus(LineSentence):
    def __iter__(self):
        with smart_open(self.source) as fin:
            for line in itertools.islice(fin, self.limit):
                document = json.loads(line.strip())

                tokens = [token for sentence in document['sentences'] for token in sentence.split()]
                title = document['wikiTitle']

                yield TaggedDocument(tokens, [title])


def trim_rule(word, count, min_count):
    if word.lower().startswith("ent_"):
        return RULE_KEEP
    else:
        return RULE_DEFAULT


def train_doc2vec(size, dm, outfilename):
    wiki_filename = wikicorpus_filename()

    model = Doc2Vec(
        size=size,
        workers=multiprocessing.cpu_count(),
        documents=JsonCorpus(wiki_filename, limit=None),  # check limit default
        window=window,
        trim_rule=trim_rule,
        dm=dm
    )

    model.save(outfilename, binary=True)


def generate_doc2vec_embeddings(size, train_algo):
    dm = {
        'pv-dm': 1,
        'pv-dbow': 0
    }[train_algo.lower()]

    outfilename = d2v_filename(size, train_algo)

    train_doc2vec(size, dm, outfilename)

