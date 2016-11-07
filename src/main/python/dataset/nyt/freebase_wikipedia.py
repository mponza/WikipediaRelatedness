import csv
import logging

from dataset.dataset_utils import FB_WIKI


logger = logging.getLogger('Freebase-Wikipedia Mapping')


def load_freebase_wikipedia_mapping():
    logger.info('Loading FB-Wiki mapping...')

    fb_wiki_title = {}
    fb_wiki_id = {}

    with open(FB_WIKI, "r") as f:
        cf = csv.reader(f, delimiter="\t")
        for row in cf:
            (wiki_title, wiki_id, mid) = row
            fb_wiki_title[mid] = wiki_title
            fb_wiki_id[mid] = wiki_id

    logger.info('FB-Wiki mapping loaded.')

    return (fb_wiki_title, fb_wiki_id)


fb_wiki_title, fb_wiki_id = load_freebase_wikipedia_mapping()