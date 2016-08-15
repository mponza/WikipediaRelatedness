# Use this for remove IPython/Jupyter warnings.
# from IPython.utils.shimmodule import ShimWarning
# import warnings; warnings.simplefilter('ignore', ShimWarning)
# from IPython.html import widgets

import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
import os


from utils import list_files


sns.set(style='whitegrid')

def chart_file(csv_file, extension='png'):
    '''Actually not used.'''
    last_dot_index = os.path.basename(csv_file).rfind('.')
    chart_name = os.path.basename(csv_file)[0:last_dot_index] + '.' + extension
    absolute_chart_name = os.path.join(os.path.dirname(csv_file), chart_name)
    return absolute_chart_name

def plot_title(csv_file):
    last_dot_index = os.path.basename(csv_file).rfind('.')
    file_name = os.path.basename(csv_file)[0:last_dot_index]
    return file_name

def make_barplot(data, ax, x='Method', y='Pearson', title=''):
    plot = sns.barplot(y='Method', x=y, data=data, palette='Paired', ax=ax)
        
    # x labels roation.
    for item in plot.get_xticklabels():
        item.set_rotation(0)

    # y label
    plot.set(ylabel=y, title=title)
    
    return plot

def generate_barplots(directory):

    csv_files = [f for f in list_files(directory) if f.endswith('csv')]
    fig, axPS = plt.subplots(nrows=len(csv_files), ncols=2, figsize=(60, 60))

    for i, csv_file in enumerate(csv_files):
        title = plot_title(csv_file)
        data = pd.read_csv(csv_file)

        parson_plot = make_barplot(data, axPS[i][0], y='Pearson', title=title)
        spearman_plot = make_barplot(data, axPS[i][1], y='Spearman', title=title)
        
    fig.savefig(os.path.join(directory, 'plots.png'))
    plt.close(fig)


