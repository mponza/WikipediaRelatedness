import baker
import logging

from tabling import generate_table
from latex import latex_table


@baker.command
def generate(outfile):
    '''
    Generates csv table with all performance of all methods.
    '''
    generate_table(outfile)


@baker.command
def latex(gdocsfile, outfile):

    latex_table(gdocsfile, outfile)


if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',
                        level=logging.INFO)
baker.run()
