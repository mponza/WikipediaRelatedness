# Use this for remove IPython/Jupyter warnings:
#
# from IPython.utils.shimmodule import ShimWarning
# import warnings; warnings.simplefilter('ignore', ShimWarning)
# from IPython.html import widgets

import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
import os


from utils import list_files

sns.set(style='whitegrid')


def make_plotter(directory):
    if (directory.endswith('correlation')):
        return CorrelationPlotter(directory)

    elif (directory.endswith('classification')):
        return ClassificationPlotter(directory)

    raise ValueError('Plotter for directory {0} does not exist.'.format(directory))


class Plotter(object):

    def __init__(self, directory):
        self.directory = directory    

    def chart_file(self, csv_file, extension='png'):
        '''Actually not used.'''
        last_dot_index = os.path.basename(csv_file).rfind('.')
        chart_name = os.path.basename(csv_file)[0:last_dot_index] + '.' + extension
        absolute_chart_name = os.path.join(os.path.dirname(csv_file), chart_name)

        return absolute_chart_name

    def plot_title(self, csv_file):
        last_dot_index = os.path.basename(csv_file).rfind('.')
        file_name = os.path.basename(csv_file)[0:last_dot_index]
        return file_name

    def generate_plots(self):
        for sub_directory in list_files(self.directory, list_only_dirs=True):
            self.generate_barplots(sub_directory)

    def generate_barplots(self, sub_directory):
        pass


class CorrelationPlotter(Plotter):

    def __init__(self, directory):
        super(CorrelationPlotter, self).__init__(directory)

    def make_barplot(self, data, ax, x='Method', y='Pearson', title=''):
        plot = sns.barplot(y='Method', x=y, data=data, palette='Paired', ax=ax)

        # x labels roation.
        for item in plot.get_xticklabels():
            item.set_rotation(0)

        # y label
        plot.set(ylabel=y, title=title)
        
        return plot

    def generate_barplots(self, sub_directory):
        csv_files = [f for f in list_files(sub_directory) if f.endswith('.csv')]
        fig, axPS = plt.subplots(nrows=len(csv_files), ncols=2, figsize=(60, 60))

        for i, csv_file in enumerate(csv_files):
            title = self.plot_title(csv_file)
            data = pd.read_csv(csv_file)

            parson_plot = self.make_barplot(data, axPS[i][0], y='Pearson', title=title)
            spearman_plot = self.make_barplot(data, axPS[i][1], y='Spearman', title=title)
            
        fig.savefig(os.path.join(sub_directory, 'plots.png'))
        plt.close(fig)


class ClassificationPlotter(Plotter):

    def __init__(self, directory):
        super(ClassificationPlotter, self).__init__(directory)

    def make_barplot(self, data, ax, x='Method', y='Pearson', title=''):
        plot = sns.barplot(y='Method', x=y, data=data, palette='Paired', ax=ax)

        # x labels roation.
        for item in plot.get_xticklabels():
            item.set_rotation(0)

        # y label
        plot.set(ylabel=y, title=title)
        
        return plot

    def generate_barplots(self, sub_directory):
        csv_files = [f for f in list_files(sub_directory) if f.endswith('.csv')]
        fig, axPS = plt.subplots(nrows=len(csv_files), ncols=3, figsize=(90, 60))

        for i, csv_file in enumerate(csv_files):
            title = self.plot_title(csv_file)
            data = pd.read_csv(csv_file)

            low_f1 = self.make_barplot(data, axPS[i][0], y='Low_F1', title=title)
            medium_f1 = self.make_barplot(data, axPS[i][1], y='Medium_F1', title=title)
            high_f1 = self.make_barplot(data, axPS[i][2], y='High_F1', title=title)
            
        fig.savefig(os.path.join(sub_directory, 'plots.png'))
        plt.close(fig)
