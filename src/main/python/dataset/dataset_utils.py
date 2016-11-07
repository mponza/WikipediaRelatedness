import os


def absolute_path(relative_path):
    WORKING_DIR = os.path.dirname(os.path.abspath(__file__))
    return os.path.join(WORKING_DIR, relative_path)


DATASET_DIR = absolute_path('../../../../data/dataset/')
RESOURCES = absolute_path('../../../../src/main/resources/')

NYT_DIR =  os.path.join(DATASET_DIR, 'nyt')
NYT_PAIRS_DIR = os.path.join(NYT_DIR, 'wiki_pairs')  # directory which will contains ss.csv, ns.csv, nn.csv

FB_WIKI =  os.path.join(RESOURCES, 'freebase-wikipedia.tsv')

