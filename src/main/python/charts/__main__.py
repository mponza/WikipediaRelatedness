import os

from utils import ANALYSIS_DIR
from utils import list_files

from plot import make_plotter


def main():
    for directory in list_files(ANALYSIS_DIR, list_only_dirs=True):
		print 'Generating plots for {0}...'.format(os.path.basename(directory))

		plotter = make_plotter(directory)
		plotter.generate_plots()

if __name__ == '__main__':
    main()