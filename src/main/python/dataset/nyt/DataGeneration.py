
# coding: utf-8

# In[1]:

import json
import csv
import logging
import csv 
import os


class GoogleDataset:
    def __init__(self):
        self.train = None
        self.eval = None

class GoogleEntity:
    def __init__(self):
        self.index = None
        self.salience = None
        self.mention_counts = None
        self.text = None
        self.start_byte_offset = None
        self.end_byte_offset = None
        self.freebase_mid = None
    
class GoogleDocument:
    def __init__(self):
        self.id = None
        self.title = None
        self.entities = []
    

def fill_with_first_row(nyt_doc, row):
    nyt_doc = GoogleDocument()
    nyt_doc.id = int(row[0])
    nyt_doc.title = row[1]
    
def row_google_entity(row):
    google_entity = GoogleEntity()
    google_entity.index = row[0]
    google_entity.salience = row[1]
    google_entity.mention_counts = row[2]
    google_entity.start_byte_offset = int(row[4])
    google_entity.end_byte_offset = int(row[5])
    google_entity.freebase_mid = row[6]
    
    return google_entity

def get_google_documents(path):
    logging.info('Loading {} file...'.format(path))
    google_docs = []
    with open(path, 'r') as f:
        
        r = csv.reader(f, delimiter='\t')
        first_line = True
        google_doc = GoogleDocument()
        for row in r:
            
            if len(row):
                if first_line:
                    
                    fill_with_first_row(google_doc, row)
                    first_line = False
                    
                else:
                    
                    google_entity = row_google_entity(row)
                    google_doc.entities.append(google_entity)
            else:
                
                google_docs.append(google_doc)
                google_doc = GoogleDocument()
                first_line = True
                
    logging.info('{} file loaded.'.format(path))
    return google_docs

def get_google_dataset():
    logging.info('Loading Google annotations')
    
    google_dataset = GoogleDataset()
    google_dataset.train = get_google_documents('/data/ponza/nyt/nyt-train')
    google_dataset.eval = get_google_documents('/data/ponza/nyt/nyt-eval')
    
    logging.info('Google annotations loaded.')
    
    return google_dataset

def stats(data):
    sal = 0
    non = 0
    
    for d in data:
        for e in d.entities:
            
            if e.salience == '1':
                sal += 1
            elif e.salience == '0':
                non += 1
    
    return (sal, non)


# In[4]:

fb_wiki = {}



def load_freebase_wikipedia_mapping():
    
    with open("/data/ponza/freebase-mapping.tsv", "r") as f:
        cf = csv.reader(f, delimiter="\t")
        for row in cf:
            (wiki_title, wiki_id, mid) = row
            fb_wiki[mid] = [wiki_title, wiki_id]




class WikiEntity:
    
    def __init__(self, google_entity):
        self.wiki_title = fb_wiki[google_entity.freebase_mid][0]
        self.wiki_id = fb_wiki[google_entity.freebase_mid][1]
        self.frequency = 0
        
    def __hash__(self):
        return hash(self.wiki_title)

    def __eq__(self, other):
        return self.wiki_title == other.wiki_title

    def __ne__(self, other):
        return not(self == other)

    def __str__(self):
        return '{0},"{1}",{2}'.format(self.wiki_id,self.wiki_title,self.frequency)



class WikiPair:
    
    def __init__(self, src_entity, dst_entity):
        self.src_entity = src_entity        
        self.dst_entity = dst_entity
        self.co_occurrence = 0
    
    def ordered_tuple(self):
        mn = min(self.src_entity.wiki_title, self.dst_entity.wiki_title)
        mx = max(self.src_entity.wiki_title, self.dst_entity.wiki_title)
        return (mn, mx)

    def __hash__(self):
        return hash(self.ordered_tuple())

    def __eq__(self, other):
        return (self.src_entity == other.src_entity and self.dst_entity == other.dst_entity) or                 (self.src_entity == other.dst_entity and self.dst_entity == other.src_entity)

    def __ne__(self, other):
        return not(self == other)

    def __str__(self):
        return "{0},{1},{2}".format(str(self.src_entity), str(self.dst_entity), self.co_occurrence)
 

class WikiPairs:
    
    def __init__(self):
        self.ss = {}
        self.ns = {}
        self.nn = {}
        self.entities = {}
    
    def __str__(self):
        return "SS: {0}, NS: {1}, NN: {2}".format(len(self.ss), len(self.ns), len(self.nn))

    def get_entity(self, google_entity):
        wiki_entity = WikiEntity(google_entity)
        if wiki_entity in self.entities:
           return self.entities[wiki_entity]
        
        self.entities[wiki_entity] = wiki_entity
        return wiki_entity 

        
    def get_pair(self, src_entity, dst_entity, bucket):
        wiki_pair = WikiPair(src_entity, dst_entity)
        if wiki_pair in bucket:
            return bucket[wiki_pair]
    
        bucket[wiki_pair] = wiki_pair    
        return wiki_pair
    
    
    def update_bucket(self, bucket, wiki_pair):
        if wiki_pair not in bucket:
            bucket[wiki_pair] = wiki_pair
        bucket[wiki_pair].co_occurrence += 1
        
        
    def add_pair(self, src_google_entity, dst_google_entity):
        src_salience = src_google_entity.salience
        dst_salience = dst_google_entity.salience
        
        # Wiki Entity + increasing frequency

        src_entity = self.get_entity(src_google_entity)
        dst_entity = self.get_entity(dst_google_entity)
        src_entity.frequency += 1
        dst_entity.frequency += 1
              
        bucket = None
        if src_salience == "1" and dst_salience == "1":
            bucket = self.ss
            
        elif src_salience == "1" and dst_salience == "0" or src_salience == "0" and dst_salience == "1":
            bucket = self.ns
        else:
            bucket = self.nn

        wiki_pair = self.get_pair(src_entity, dst_entity, bucket)
        self.update_bucket(bucket, wiki_pair)



class WikiPairWriter:

    def __init__(self, dirpath, wiki_pairs):
        self.dirpath = dirpath
        if not os.path.exists(dirpath):
            os.makedirs(dirpath)
        self.wiki_pairs = wiki_pairs


    def write(self):
        print("Sorting buckets...")

        print("Sorting ss...")
        ss = self.sorted_bucket(self.wiki_pairs.ss)
        self.write_to_file(ss[0:100] + self.middle(ss) + ss[-100:], "ss.csv")

        print("Sorting ns...")
        ns = self.sorted_bucket(self.wiki_pairs.ns)
        self.write_to_file(ns[0:100] + self.middle(ns) + ns[-100:], "ns.csv")

        print("Sorting nn...")
        nn = self.sorted_bucket(self.wiki_pairs.nn)
        self.write_to_file(nn[0:100] + self.middle(nn) + nn[-100:], "nn.csv")


    def middle(self, a):
        return filter(lambda x: x.co_occurrence >= 10 and x.co_occurrence <= 100, a)[0:100]

    def write_to_file(self, pairs, filename):
        with open(os.path.join(self.dirpath, filename), "w") as f:
            for w in pairs:
                f.write(str(w) + "\n")

        

    def sorted_bucket(self, bucket):
        return sorted(bucket.values(), key=lambda wiki_pair: wiki_pair.co_occurrence)[::-1]
        



# In[5]:
 

from itertools import combinations

        
def build_wiki_pairs(docs):
    wiki_pairs = WikiPairs()
    
    for index, entities in enumerate([doc.entities for doc in docs]):
        combs = combinations(entities, 2)
        for pair in combs:
            try:
                wiki_pairs.add_pair(*pair)
            except KeyError:
                continue
            except Exception:
                print("Excpetion not managed... {0}".format(str(sys.exc_info()[0])))
        if index % 1000 == 0 and index != 0:
          print("Percentage {0} documents processed...".format(index)) 

    return wiki_pairs
    
            

GOOGLE_DATASET = get_google_dataset()
google_docs = GOOGLE_DATASET.train + GOOGLE_DATASET.eval
google_docs = google_docs

print("Google dataset loaded.")


load_freebase_wikipedia_mapping()

print("Freebase mapping loaded.")



wiki_pairs = build_wiki_pairs(google_docs)
print("Paris stats: {0}".format(str(wiki_pairs)))

writer = WikiPairWriter("/data/ponza/Projects/WikipediaRelatedness/data/dataset/nyt", wiki_pairs)
writer.write()



