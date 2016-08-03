from utils import list_analysis_dirs
from utils import list_files
from plot import generate_barplots

def main():
    for d in list_analysis_dirs():
    	print 'Generating plots for {0}...'.format(d)
        csv_files = [f for f in list_files(d) if f.endswith('csv')]
        generate_barplots(csv_files)

if __name__ == '__main__':
    main()