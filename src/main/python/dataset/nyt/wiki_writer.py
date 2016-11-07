import os
import logging


logger = logging.getLogger("WikiPairWriter")


class WikiPairWriter:

    def __init__(self, dirpath):
        self.dirpath = dirpath

        if not os.path.exists(dirpath):
            os.makedirs(dirpath)

    def write(self, wiki_pairs):
        logger.info('Writing WikiPairs...')

        logger.info("Sorting & writing ss...")
        ss = self.sorted_bucket(wiki_pairs.ss)
        self.write_to_file(ss[0:100] + self.middle(ss) + ss[-100:], "ss.csv")

        logger.info("Sorting & writing  ns...")
        ns = self.sorted_bucket(wiki_pairs.ns)
        self.write_to_file(ns[0:100] + self.middle(ns) + ns[-100:], "ns.csv")

        logger.info("Sorting & writing  nn...")
        nn = self.sorted_bucket(wiki_pairs.nn)
        self.write_to_file(nn[0:100] + self.middle(nn) + nn[-100:], "nn.csv")


    def middle(self, a):
        return filter(lambda x: x.co_occurrence >= 10 and x.co_occurrence <= 100, a)[0:100]


    def write_to_file(self, pairs, filename):
        with open(os.path.join(self.dirpath, filename), "w") as f:
            for w in pairs:
                f.write(str(w) + "\n")


    def sorted_bucket(self, bucket):
        return sorted(bucket.values(), key=lambda wiki_pair: wiki_pair.co_occurrence)[::-1]
