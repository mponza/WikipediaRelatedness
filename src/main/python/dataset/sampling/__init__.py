import os
import random
import logging

import pandas as pd

from itertools import combinations

from dataset_utils import ENHANCED_NYT_DIR
from dataset_utils import SAMPLE_NYT_DIR

from .wiki_text import get_first_sentence

logger = logging.getLogger('NYTSampling')



# Configuration Parameters
sample_size = 3  # 3 pairs for each typeDistanced (i.e. Location-Pearson at distance)
dist = 'outDist'

wikiTypes = ['Location', 'Organisation', 'Person', 'Object']
distances = [1, 2, 3]
labels = ['head', 'middle', 'tail']


def type_combinations():
    return list(combinations(wikiTypes, 2)) + [(t, t) for t in wikiTypes]


def sample(df, num=sample_size):
    return df.loc[random.sample(list(df.index), min(num, len(df)))].copy()


def dir2filepaths(dirpath, ext='csv'):
    return [os.path.join(dirpath, p) for p in os.listdir(dirpath) if p.endswith(ext)]


'''
:return {path: SampledDataFrame}
'''
def get_samples():
    samples = {}

    logger.info('New York Time Sampling...')

    for path in dir2filepaths(ENHANCED_NYT_DIR):
        data = pd.read_csv(path, sep=",")

        logger.info('Sample generation for file {0}...'.format(path))
        outdatas = []

        for tt, d, c in [(tt, d, c) for tt in type_combinations() for d in distances for c in labels]:
            # Dataframe with only those pairs which respect (tt, d, c) constraints
            pairs = data[( ( (data['srcWikiType'] == tt[0]) & (data['dstWikiType'] == tt[1]) ) | ( (data['dstWikiType'] == tt[0]) & (data['srcWikiType'] == tt[1]) ) ) & (data[dist] == d) & (data['label'] == c)].copy()

            if pairs.empty:
                logger.warn('{0} empty pairs for {1} {2} ({3} file).'.format(tt, d, c, path))
                continue

            outdatas.append(sample(pairs))

        samples[path] = pd.concat(outdatas).sort(dist).sort('label').drop_duplicates()
        logger.info('Samples for {0} generated: Size {1}.'.format(path, len(samples[path])))

    return  samples


def wiki_sent_mapping(samples):

    logger.info('Mapping WikiIDs to their first sentence...')
    data = pd.concat([samples[path] for path in dir2filepaths(ENHANCED_NYT_DIR)])
    wikiIDs = list(set(set(data['srcWikiID']) + set(data['dstWikiID'])))

    return get_first_sentence(wikiIDs)


def writes_samples(samples, wiki2sent):
    logger.info('Finalizing sampling and writing to file....')

    if not os.isdir(SAMPLE_NYT_DIR):
        os.makedirs(SAMPLE_NYT_DIR)


    for path in dir2filepaths(ENHANCED_NYT_DIR):
        data = samples[path]



        text_mapper = lambda wikiID: wiki2sent[wikiID]
        data['srcWikiText'] = data['srcWikiID'].map(text_mapper)
        data['dstWikiText'] = data['dstWikiID'].map(text_mapper)

        filename = os.path.join(SAMPLE_NYT_DIR, os.path.basename(path))
        data.to_csv(filename)


def generate_nyt_sampling():
    # Generate samples
    samples = get_samples()

    # Retrieve first sentnece for each WikiID
    wiki2sent = wiki_sent_mapping(samples)

    # Enhance samples with the first sentence and write them to file
    writes_samples(samples, wiki2sent)