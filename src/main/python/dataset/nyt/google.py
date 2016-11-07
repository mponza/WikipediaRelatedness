'''
Python module with utils used to load Google's NYT annotations (downloadable at https://github.com/dmorr-google/nyt-salience).
Just invoke get_google_dataset method.
'''

import os
import logging
import csv

from dataset.dataset_utils import NYT_DIR


logger = logging.getLogger('Google')


class GoogleDataset:
    def __init__(self):
        self.train = None
        self.eval = None


class GoogleEntity:
    def __init__(self):
        self.index = None # int
        self.salience = None # string
        self.mention_counts = None
        self.text = None # string
        self.start_byte_offset = None # int
        self.end_byte_offset = None # int
        self.freebase_mid = None # string


class GoogleDocument:
    def __init__(self):
        self.id = None # int
        self.title = None # string
        self.entities = []


def fill_with_first_row(nyt_doc, row):
    nyt_doc = GoogleDocument()
    nyt_doc.id = int(row[0])
    nyt_doc.title = row[1]


def row_google_entity(row):
    google_entity = GoogleEntity()
    google_entity.index = int(row[0])
    google_entity.salience = row[1]
    google_entity.mention_counts = int(row[2])
    google_entity.start_byte_offset = int(row[4])
    google_entity.end_byte_offset = int(row[5])
    google_entity.freebase_mid = row[6]

    return google_entity


def get_google_documents(path):
    logger.info('Loading {0} file...'.format(path))
    google_docs = []
    with open(path, 'r') as f:

        r = csv.reader(f, delimiter='\t')
        first_line = True
        google_doc = GoogleDocument()
        for row in r:

            if len(row):
                if first_line:

                    fill_with_first_row(google_doc, row)
                    first_line = False

                else:

                    google_entity = row_google_entity(row)
                    google_doc.entities.append(google_entity)
            else:

                google_docs.append(google_doc)
                google_doc = GoogleDocument()
                first_line = True

    logger.info('{0} file loaded.'.format(path))
    return google_docs


def get_google_dataset():
    logger.info('Loading Google annotations')

    google_dataset = GoogleDataset()
    google_dataset.train = get_google_documents(os.path.join(NYT_DIR, 'nyt-train'))
    google_dataset.eval = get_google_documents(os.path.join(NYT_DIR, 'nyt-eval'))

    logger.info('Google annotations loaded.')

    return google_dataset