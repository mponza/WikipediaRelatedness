import os

from utils import dataset_paths
from dataset import CorrelationDataset



def generate_table(outfile):

    datasets = []
    for datapath in dataset_paths():
        datasets.append( CorrelationDataset(datapath) )

    # merge via pandas

    # save to output

