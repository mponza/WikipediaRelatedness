import baker
import logging

from line import from line import keep_dataset_wiki_ids


@baker.command
def line(filename):
    keep_dataset_wiki_ids(filename)


if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',
                        level=logging.INFO)

baker.run()
