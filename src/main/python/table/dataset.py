import os
import pandas as pd


class CorrelationDataset:

    def __init__(self, dirname):
        self.dataset_name = dirname.split('/')[-1]
        self.relatednesses = self._get_relatednesses(dirname)  # List of methods with their correlation performance

    def _get_relatednesses(self, dirname):
        relatednesses = []

        for reldir in os.listdir(dirname):
            abs_reldir = os.path.join(dirname, reldir)

            rel = Relatedness(abs_reldir)
            relatednesses.append(rel)

        return relatednesses


class Relatedness:

    def __init__(self, dirname):
        self.method_name = dirname.split("/")[-1]

        correlation_filename = os.path.join(dirname, self.method_name + ".correlation.csv")
        self.correlation = CorrelationContainer(correlation_filename)


    def to_json(self):
        return {
            'name': self.method_name,
            'pearson': self.correlation.pearson,
            'spearman': self.correlation.spearman,
            'harmonic': self.correlation.harmonic
        }


class CorrelationContainer:

    def __init__(self, filename):
        """
        :param filename: .correlation.csv file
        """
        content = pd.read_csv(filename)

        self.pearson = content['Pearson'].iloc[0]
        self.spearman =  content['Spearman'].iloc[0]

        self.harmonic = content['Harmonic'].iloc[0]
        self.average = ( self.pearson + self.spearman ) / 2