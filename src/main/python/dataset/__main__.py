import baker
import logging

from nyt import generate_nyt_dataset
from sampling import generate_nyt_sampling


@baker.command
def nyt_salience_dataset():
    generate_nyt_dataset()


@baker.command
def nyt_sampling():
    generate_nyt_sampling()


if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',
                        level=logging.INFO)
    baker.run()