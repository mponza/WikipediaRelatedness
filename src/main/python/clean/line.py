import os
import smart_open
import logging

import pandas as pd

from pyhocon import ConfigFactory


#
# Utils

def absolute_path(path):
    WORKING_DIR = os.path.dirname(os.path.abspath(__file__))
    return os.path.join(WORKING_DIR, path)


def get_wiki_ids():
    '''
    :return:  set of WikiIDs which belongs to dataset files.
    '''

    conf = ConfigFactory.parse_file( absolute_path('../../resources/reference.conf') )

    # WikiSim
    filename = conf.get_string('wikipediarelatedness.dataset.wikisim.wat')
    df = pd.read_csv(filename)
    df.columns =  ['src_word', 'src_wiki_id', 'src_wiki_title', 'dst_word', 'dst_wiki_id', 'dst_wiki_title', 'rel']
    wiki_ids = df['src_wiki_id'].tolist() + df['dst_wiki_id'].tolist()

    # WiRe
    for wire_name in ['salient_salient', 'nonsalient_salient', 'nonsalient_nonsalient']:
        filename  = conf.get_string('wikipediarelatedness.dataset.wire.' + wire_name)
        df = pd.read_csv(filename)

        wiki_ids += df['srcWikiID'].tolist()
        wiki_ids += df['dstWikiID'].tolist()

    return set(wiki_ids)



#
# Cleaning Procedure

def keep_dataset_wiki_ids(filename):
    '''
    Overwrite filename by keeping only those Wikipedia IDs contained in the datasets.
    :param filename: absolute filename of the LINE embeddings.
    :return:
    '''
    logging.info('Retrieving unique Wikiedia IDs...')
    wiki_ids = get_wiki_ids()
    logging.info('{0} Unique Wikipedia IDs loaded.'.format(len(wiki_ids)))

    lines = []
    logging.info('Filtering Wikipedia IDs...')
    for index, line in enumerate(smart_open.smart_open(filename)):
        if index == 0:
            continue

        wiki_id = line.split(' ')[0]

        if int(wiki_id) in wiki_ids:
            lines.append(line)

        if len(lines) == len(wiki_ids):
            logging.info('All Wikipedia ID found.')
            break

    logging.info('Writing filtered lines into {0}.gz...'.format(filename))
    with smart_open.smart_open(filename + '.gz', 'wb') as fout:
        for line in lines:
            fout.write(line)

    logging.info('LINE cleaning ended.')