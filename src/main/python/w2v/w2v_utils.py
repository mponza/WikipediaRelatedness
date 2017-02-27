import os
import json
import pandas as pd

from gensim import utils
from pyhocon import ConfigFactory


def absolute_path(filename):
    '''
    :param filename:
    :return: Absolute path of filename
    '''
    WORKING_DIR = os.path.dirname(os.path.abspath(__file__))
    return os.path.join(WORKING_DIR, filename)


def configuration():
    """
    :return: Dictionary of the reference.conf file.
    """
    reference = absolute_path('../../resources/reference.conf')
    return ConfigFactory.parse_file(reference).get('wikipediarelatedness')


def wikicorpus_filename():
    return configuration().get('wikipedia.corpus')


def d2v_filename(size, train_algo):
    '''
    Returns absolute filename to d2v embeddings with parameters size and train_algo.
    '''

    config = configuration()
    d2v_dir = config.get('wikipedia.neural.doc2vec.gensim')

    if not os.path.isdir(d2v_dir):
        os.makedirs(d2v_dir)

    filename = 'd2v_size:%d_model:%s.bin'.format(size, train_algo)

    return os.path.join(d2v_dir, filename)