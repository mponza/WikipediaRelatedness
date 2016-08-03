import os

def analysis_directory():
    cur_file = os.path.realpath(__file__)
    proj_dir = os.path.join(os.path.dirname(cur_file), '../../../../')
    analysis = os.path.join(proj_dir, 'data/analysis')
    return analysis

def list_files(directory):
    files = [d for d in os.listdir(directory)]
    absolute_files = [os.path.join(directory, f) for f in files]
    return absolute_files

def list_analysis_dirs():
    absolute_files = list_files(ANALYSIS_DIR)
    absolute_dirs = [f for f in absolute_files if os.path.isdir(f)]
    return absolute_dirs


ANALYSIS_DIR = analysis_directory()