import os
import pandas as pd


class CorrelationDataset:

    def __init__(self, dirname):
        self.dataset_name = dirname.split('/')[-1]
        self.rels = self._get_rels(dirname)  # List of methods with their correlation performance

    def _get_rels(self, dirname):
        rels = []

        for reldir in os.listdir(dirname):
            abs_reldir = os.path.join(dirname, reldir)

            rel = Relatedness(abs_reldir)
            rels.append(rel)

        return rels


    def to_df(self):
        #sorted_rels = sorted(self.rels, key=lambda r : r.method_name)
        jsons = [ r.to_dict(self.dataset_name) for r in self.rels ]
        cs = [self.dataset_name + '_' + corr_name for corr_name in ['P', 'S', 'H']]

        cs = [self.dataset_name + '_' + corr_name for corr_name in ['TotalAVG', 'SingleAVG']]

        return pd.DataFrame( jsons , columns=['Method'] + cs)


class Relatedness:

    def __init__(self, dirname):
        self.method_name = dirname.split("/")[-1]

        correlation_filename = os.path.join(dirname, "correlation.csv")
        self.correlation = CorrelationContainer(correlation_filename)

        # comment here and, in to_dict and in to_df time is not needed.
        time_filename = os.path.join(dirname, "time.csv")
        self.time = TimeContainer(time_filename)


    def to_dict(self, dataset_name):
        return {
            'Method': self.method_name,

            dataset_name + '_P': self.correlation.pearson,
            dataset_name + '_S': self.correlation.spearman,
            dataset_name + '_H': self.correlation.harmonic,

            dataset_name + '_TotalAVG': self.time.avg_total,
            dataset_name + '_SingleAVG': self.time.avg_single
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


class TimeContainer:

    def __init__(self, filename):
        content = pd.read_csv(filename)

        self.avg_total = content['Total'].iloc[0]
        self.avg_single = content['Average'].iloc[0]