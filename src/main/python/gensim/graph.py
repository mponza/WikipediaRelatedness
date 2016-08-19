import numpy as np
from scipy import sparse
import gzip


# Same mapping used by Webgraph in the Java library.
wiki2node = {}


def load_wikipedia_linked_list(path):
	'''
	Loads Wikipedia Linked List from path by mapping the Wikipedia IDs
	to matrix indicies (see wiki2node).
	'''
	linked_list = {}

	print 'Loading Wikipedia invertex lists from {0}...'.format(path)
	with open(path, 'r') as f:
		for line in f.readlines():

			link = line.split(' ')
			srcWikiID, dstWikiID = int(link[0]), int(link[1])

			srcNodeID = wiki2NodeMapping(srcWikiID)
			dstNodeID = wiki2NodeMapping(dstWikiID)

			if srcNodeID not in linked_list:
				linked_list[srcNodeID] = []

			linked_list[srcNodeID].append(dstNodeID)

 	# print 'Sorting indicies...'
 	# for index in linked_list:
 	# 	linked_list[index] = sorted(linked_list[index])
 	# print 'Wikipedia Matrix loaded.'

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


 def load_wikipedia_sparse_matrix(path):
 	'''
 	Creates scipy sparse linked-list from path.
 	'''
 	linked_list = load_wikipedia_linked_list(path)

 	n = wiki2node.length,
 	matrix = sparse.lil_matrix((n, n))

 	print 'Creating Sparse Matrix from Linked List...'
 	for src in linked_list:
 		dsts = linked_list[src]
 		prob = 1 / float(len(dsts))

 		for dst in dsts:
 			matrix[src, dst] = prob

 	return matrix


def generate_eigenvectors(n_eigenvectors=10, wiki_path, eigenvectors_path):
	matrix = load_wikipedia_sparse_matrix(wiki_path)

	print 'Computing %d eigenvectors'.format(n_eigenvectors)
	eigenvectors = sparse.linalg.svds(matrix, )

	print 'Serializing eigenvectors...'
	gzip.open(eigenvectors_path, '') as f:
		for vec in eigenvectors:
			f.write(vec)  # tocheck
