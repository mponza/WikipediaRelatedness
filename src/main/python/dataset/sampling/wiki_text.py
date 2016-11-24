import logging
import requests


from multiprocessing import Pool

logger = logging.getLogger('WikiSent')


def get_first_n_sentence(title, n=1, attempts=3):
    for i in range(0, attempts):
        try:

            data = requests.get('http://dev.wat.mkapp.it/wikidocs?', {'title': title}).json()
            return ' '.join([ paragraph['text'] for paragraph in data['sections'][0]['paragraphs'][0:n]])

        except:
            pass

    logger.err('Error with WikiTitle {0}'.format(title))
    return ''


def get_first_sentence(wikiIDs):
    pool = Pool(12)
    wiki2sent = pool.map(get_first_n_sentence, wikiIDs)
    pool.close
    return wiki2sent