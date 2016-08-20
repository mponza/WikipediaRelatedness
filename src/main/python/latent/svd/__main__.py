from eigen import generate_eigenvectors
import sys

if __name__ == '__main__':
    inp, outp = sys.argv[1:3]
    generate_eigenvectors(inp, outp)