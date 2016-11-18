import os


def absolute_path(relative_path):
    WORKING_DIR = os.path.dirname(os.path.abspath(__file__))
    return os.path.join(WORKING_DIR, relative_path)


RESOURCES = absolute_path('../../../../src/main/resources/')
DATASET_DIR = absolute_path('../../../../data/dataset')

NYT_DIR =  os.path.join(RESOURCES, 'nyt-salience') # directory with nyt-train and nyt-eval
NYT_PAIRS_DIR = os.path.join(DATASET_DIR, 'nyt_wiki_pairs')  # directory which will contains ss.csv, ns.csv, nn.csv

FB_WIKI =  os.path.join(RESOURCES, 'wikipedia/freebase-mapping.tsv')

ENHANCED_NYT_DIR = os.path.join(NYT_PAIRS_DIR, 'enhanced')
SAMPLE_NYT_DIR = os.path.join(DATASET_DIR, 'sample')