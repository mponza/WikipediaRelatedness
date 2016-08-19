import logging, gensim, bz2
import baker

@lda
def generate_wikipedia_corpus_LDA():
	logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',
						level=logging.INFO)

	id2word_path = ''
	id2word = gensim.corpora.Dictionary.load_from_text('wiki_en_wordids.txt')

	mm = gensim.corpora.MmCorpus('wiki_en_tfidf.mm')

	lda = gensim.models.ldamodel.LdaModel(
		corpus=mm, id2word=id2word, num_topics=100,
		update_every=1, chunksize=10000, passes=1)

@graphLDA
def generate_wikipedia_graph_LDA():
