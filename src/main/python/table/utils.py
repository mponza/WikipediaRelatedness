import os
from pyhocon import ConfigFactory


def absolute_path(path_from_latent_gensim):
    WORKING_DIR = os.path.dirname(os.path.abspath(__file__))
    return os.path.join(WORKING_DIR, path_from_latent_gensim)


def dataset_paths():
    reference = absolute_path('../../resources/reference.conf')

    conf =  ConfigFactory.parse_file(reference)
    corrdir = conf.get_string('wikipediarelatedness.benchmark.correlation')

    datapaths = []
    for dataname in os.listdir(corrdir):
        datapaths.append( os.path.join(corrdir, dataname) )

    return datapaths
