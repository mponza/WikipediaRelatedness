import logging
import os
import gzip

import numpy as np

from scipy import sparse
from gensim import utils

from eigen import wiki2node
from eigen import wiki2NodeMapping

from latent_utils import WIKI_LINKS
from latent_utils import LAPLACIAN_PINV




logger = logging.getLogger('Average Commute Time')

normalized_laplacian = True


def load_symmetric_wikipedia_linked_list(path):
    '''
    Loads Symmetric Wikipedia Linked List from path by mapping the Wikipedia IDs
    to matrix indicies (see wiki2node).
    '''
    linked_list = {}

    logger.info('Loading Wikipedia inverted lists from {0}...'.format(path))
    with utils.smart_open(path) as f:
        for line in f:
            link = line.strip().split('\t')
            srcWikiID, dstWikiID = int(link[0]), int(link[1])

            srcNodeID = wiki2NodeMapping(srcWikiID)
            dstNodeID = wiki2NodeMapping(dstWikiID)

            if srcNodeID not in linked_list:
                linked_list[srcNodeID] = []
            if dstNodeID not in linked_list:
                linked_list[dstNodeID] = []

            linked_list[srcNodeID].append(dstNodeID)
            linked_list[dstNodeID].append(srcNodeID)

    logger.info('Removing duplicated...')
    for index in linked_list:
        linked_list[index] = set(linked_list[index])

    return linked_list


def generate_wikipedia_laplacian_matrix(path):
    '''
    Creates scipy sparse laplacian matrix from path. Volume of the graph is returned as well.
    '''
    linked_list = load_symmetric_wikipedia_linked_list(path)

    n = len(wiki2node)
    adj_matrix = sparse.lil_matrix((n, n))

    logger.info('Creating Sparse Adjacent Matrix from Linked List...')
    volume = 0
    for src in linked_list:
        dsts = linked_list[src]

        for dst in dsts:
            adj_matrix[src, dst] = 1
            volume += 1

    logger.info('Generating csr_matrix...') # for efficiency constraints
    csr_adj_matrix = sparse.csr_matrix(adj_matrix)

    logger.info("Generating Laplacian matrix...")
    laplacian_matrix = sparse.csgraph.laplacian(csr_adj_matrix, normalized_laplacian)

    return (laplacian_matrix, volume)


def serialize_laplacian_pseudoinverse(file_path, volume, row_matrix):
    '''
    Seriazlies a matrix of rows in a csv file where each
    row is a eigvenvector.
    File: 4M of rows.
          Each row is tab separated and contains 100 floats.
    '''
    with gzip.open(file_path, 'w') as f:
        f.write("Volume:%d\n".format(volume))

        for column in row_matrix:
            # from here, to be checked!
            f.write('\t'.join(['%d:%1.10f'.format(index, column[index]) for index in np.nonzero(column)]))
            f.write('\n')



def generate_laplacian_pseudoinverse(wiki_path=WIKI_LINKS, pinv_filename=LAPLACIAN_PINV):
    (laplacian_matrix, volume) = generate_wikipedia_laplacian_matrix(wiki_path)

    logger.info('Computing the Moore-Penrose pseudo-inverse of the Laplacian matrix...')
    laplacian_pinv = np.linalg.pinv(laplacian_matrix)

    if(not os.path.isdir(pinv_filename)):
        os.makedirs(pinv_filename)

    logger.info('Serializing Laplacian pseudoinverse...')
    serialize_laplacian_pseudoinverse(LAPLACIAN_PINV, volume, laplacian_pinv)
    logger.info('Laplacian pseudoinverse computation ended.')
