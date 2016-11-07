'''
Generates nyt/nyt-wikipairs data according to the salience sampling of NYT Google documents.
'''

import logging

from google import get_google_dataset

from wiki_pair import build_wiki_pairs
from wiki_writer import WikiPairWriter

from dataset_utils import NYT_PAIRS_DIR


logger = logging.getLogger('NYT-WikiPairs')


def generate_nyt_dataset():
    logger.info('NYTWP Dataset generation...')

    google_dataset = get_google_dataset()
    google_docs = google_dataset.train + google_dataset.eval

    wiki_pairs = build_wiki_pairs(google_docs)

    writer = WikiPairWriter(NYT_PAIRS_DIR)
    writer.write(wiki_pairs)

    logger.info('NYTWP Dataset ended.')
