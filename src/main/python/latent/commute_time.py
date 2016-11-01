import logging
import os
import gzip

import numpy as np

from scipy import sparse
from gensim import utils

from eigen import wiki2node
from eigen import wiki2NodeMapping

from latent_utils import WIKI_LINKS
from latent_utils import LAPLACIAN_PINV_DIR




logger = logging.getLogger('Average Commute Time')

normalize_laplacian = True


def load_symmetric_wikipedia_linked_list(path):
    '''
    Loads Symmetric Wikipedia Linked List from path by mapping the Wikipedia IDs
    to matrix indicies (see wiki2node).
    '''
    linked_list = {}

    logger.info('Loading Wikipedia inverted lists from {0}...'.format(path))
    i = 0
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

            i += 1

            if i > 10000:
                break

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
    laplacian_matrix = sparse.csgraph.laplacian(csr_adj_matrix, normalize_laplacian)

    return (laplacian_matrix, volume)


def compute_pseduoinverse(laplacian_matrix):
    logger.info('Computing the pseudo-inverse of the Laplacian matrix...')

    logger.info('Computing SVD...')
    n = len(wiki2node) - 1
    print n
    print laplacian_matrix.shape
    diagonal = sparse.linalg.svds(laplacian_matrix, k=n, return_singular_vectors=False)

    print diagonal.shape
    return None
    logger.info('Inverting diagonal elements...')
    for i in range(0, len(wiki2node)):
        print i
        d = diagonal[i]
        if np.nonzero(d):
            diagonal[i] = 1 / d

    # moltiplicare per le altre due scambiate cosi da ottenere la pseudoinversa e poi salvarla
    return diagonal


def serialize_laplacian_pseudoinverse(filename, volume, diagonal):
    '''
    Seriazlies diagonal of laplacian pseudoinverse.
    The i-th row is the (i, i) element of the pseudoinverse matrix.
    '''
    with gzip.open(filename, 'w') as f:
        f.write("%d\n\n".format(volume))

        for element in diagonal:
            if np.nonzero(element):
                f.write(element)
            else:
                f.write(0.0)

            f.write('\n')


def generate_laplacian_pseudoinverse(wiki_path=WIKI_LINKS, pinv_dir=LAPLACIAN_PINV_DIR):
    (laplacian_matrix, volume) = generate_wikipedia_laplacian_matrix(wiki_path)

    laplacian_pinv_diagonal = compute_pseduoinverse(laplacian_matrix)

    if(not os.path.isdir(pinv_dir)):
        os.makedirs(pinv_dir)

    logger.info('Serializing Laplacian pseudoinverse...')
    serialize_laplacian_pseudoinverse(os.path.join(pinv_dir, 'laplacian_pinv.gz'), volume, laplacian_pinv_diagonal)
    logger.info('Laplacian pseudoinverse computation ended.')
