# Use this for remove IPython/Jupyter warnings.
# from IPython.utils.shimmodule import ShimWarning
# import warnings; warnings.simplefilter('ignore', ShimWarning)
# from IPython.html import widgets

import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
import os

sns.set(style='whitegrid')

def chart_file(csv_file, extension='png'):
    last_dot_index = os.path.basename(csv_file).rfind('.')
    chart_name = os.path.basename(csv_file)[0:last_dot_index] + '.' + extension
    absolute_chart_name = os.path.join(os.path.dirname(csv_file), chart_name)
    return absolute_chart_name

def make_barplot(data, ax, x='Method', y='Pearson'):
    plot = sns.barplot(y='Method', x=y, data=data, palette='Paired', ax=ax)
        
    # x labels roation.
    for item in plot.get_xticklabels():
        item.set_rotation(0)

    # y label
    plot.set(ylabel=y)
    
    return plot

def generate_barplots(csv_files):
    for csv_file in csv_files:
            
        fig, (axP, axS) = plt.subplots(1,2, figsize=(30, 10))
        data = pd.read_csv(csv_file)

        parson_plot = make_barplot(data, axP, y='Pearson')
        spearman_plot = make_barplot(data, axS, y='Spearman')
        
        fig.savefig(chart_file(csv_file))

        plt.close(fig)


