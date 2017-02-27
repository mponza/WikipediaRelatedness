import baker
import logging

from doc2vec import generate_doc2vec_embeddings


@baker.command
def generate(size, train_algo):
    generate_doc2vec_embeddings(size, train_algo)


if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',
                        level=logging.INFO)
baker.run()
