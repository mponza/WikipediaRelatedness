import logging
import numpy as np
from scipy import sparse

from gensim import utils
import os
import gzip

from latent_utils import WIKI_LINKS
from latent_utils import WIKI_SVD_DIR


logger = logging.getLogger('WikiGraphSVD')


# Same mapping used by Webgraph in the src/main/scala/ code.
wiki2node = {}


def load_wikipedia_linked_list(path):
    '''
    Loads Wikipedia Linked List from path by mapping the Wikipedia IDs
    to matrix indicies (see wiki2node).
    '''
    linked_list = {}

    logger.info('Loading Wikipedia invertex lists from {0}...'.format(path))
    with utils.smart_open(path) as f:
        for line in f:
            link = line.strip().split('\t')
            srcWikiID, dstWikiID = int(link[0]), int(link[1])

            srcNodeID = wiki2NodeMapping(srcWikiID)
            dstNodeID = wiki2NodeMapping(dstWikiID)

            if srcNodeID not in linked_list:
                linked_list[srcNodeID] = []

            linked_list[srcNodeID].append(dstNodeID)

    logger.info('Removing duplicated...')
    for index in linked_list:
         linked_list[index] = set(linked_list[index])

    return linked_list


def wiki2NodeMapping(wikiID):
    '''
    Maps wikiID to the matrix index nodeID by updating wiki2node.
    '''
    if wikiID in wiki2node:
        return wiki2node[wikiID]

    nextNodeID = len(wiki2node)
    wiki2node[wikiID] = nextNodeID

    return nextNodeID


def generate_wikipedia_matrix(path):
    '''
    Creates scipy sparse linked-list from path.
    '''
    linked_list = load_wikipedia_linked_list(path)

    n = len(wiki2node)
    matrix = sparse.lil_matrix((n, n))

    logger.info('Creating Sparse Matrix from Linked List...')
    for src in linked_list:
        dsts = linked_list[src]
        prob = 1 / float(len(dsts))

        for dst in dsts:
            matrix[src, dst] = prob

    return sparse.csr_matrix(matrix)


def serialize_matrix(file_path, row_matrix):
    '''
    Seriazlies a matrix of rows in a csv file where each 
    row is a eigvenvector.
    File: 4M of rows.
          Each row is tab separated and contains 100 floats.
    '''
    with gzip.open(file_path, 'w') as f:
        for column in row_matrix:
            f.write('\t'.join([str(v) for v in column]))
            f.write('\n')


def generate_eigenvectors(wiki_path=WIKI_LINKS, eigen_dir=WIKI_SVD_DIR,
                          n_eigenvectors=200):
    matrix = generate_wikipedia_matrix(wiki_path)

    logger.info('Computing SVD with {0} eigenvectors'.format(n_eigenvectors))
    eigenvectors = sparse.linalg.svds(matrix, k=n_eigenvectors)

    if(not os.path.isdir(eigen_dir)):
        os.makedirs(eigen_dir)

    #logger.info('Serializing left eigenvectors...')
    #serialize_matrix(os.path.join(eigen_dir, 'eigen_left.csv.gz'), eigenvectors[0])

    logger.info('Serializing right eigenvectors...')
    serialize_matrix(os.path.join(eigen_dir, 'eigen_right.csv.gz'), eigenvectors[2].transpose())

    logger.info('SVD computation ended.')
