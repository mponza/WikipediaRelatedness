import os

from utils import list_analysis_dirs
from plot import generate_barplots

def main():
    for d in list_analysis_dirs():
    	print 'Generating plots for {0}...'.format(os.path.basename(d))
        generate_barplots(d)

if __name__ == '__main__':
    main()