import sys
import os

from eigen import generate_eigenvectors

if __name__ == '__main__':
    inp, outp = sys.argv[1:3]
    inp = os.path.expanduser(inp)
    outp = os.path.expanduser(outp)

    logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s')
    logging.root.setLevel(level=logging.INFO)

    generate_eigenvectors(inp, outp)