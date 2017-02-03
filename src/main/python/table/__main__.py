import baker
import logging

from tabling import generate_table


@baker.command
def generate(outfile):
    '''
    Generates csv table with all performance of all methods.
    '''
    generate_table(outfile)




if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',
                        level=logging.INFO)
baker.run()
