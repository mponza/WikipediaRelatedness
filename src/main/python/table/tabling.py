import pandas as pd

from utils import dataset_paths
from dataset import CorrelationDataset


def generate_table(outfile):
    datasets = {}  # dataset name -> pandas DataFrame

    for datapath in dataset_paths():
        corrdata = CorrelationDataset(datapath)
        print(corrdata.dataset_name)
        datasets[corrdata.dataset_name] = corrdata.to_df()

    merged = merge_dataset(datasets)
    merged.to_csv(outfile)


def merge_dataset(datasets):
    merged=pd.merge( datasets['WikiSim'], datasets['WiRe'], on="Method" )

    return merged.sort_values(by=['Method'])
