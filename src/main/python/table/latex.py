import pandas as pd

columns = ['Method', 'WikiSim-Pearson', 'WikiSim-Spearman', 'WikiSim-Harmonic', 'WiRe-Pearson', 'WiRe-Spearman', 'WiRe-Harmonic', 'WiRe-AVG']

def colorize(index, value):
    name = {
        0: 'first',
        1: 'second',
        2: 'third'
    }[index]

    return '{{\color{{table:{0}}} {1}}}'.format(name, value)


def latex_table(gdocsfilename, outfile):
    global columns

    # Reading and setting up
    df = pd.read_csv(gdocsfilename)
    df.drop(df.index[0], inplace=True) # remove NaN line
    df.columns = columns

    # Colorizing top-3 largest with colorize function
    for c in columns[1:]:
        # top-3 largest
        for i in [0, 1, 2]:
            # get value sorted in decreasing order
            top = sorted( set( df[c].tolist() ) )
            top.reverse()

            df[c][ (df[c] == top[i]) ] = colorize(i, top[i])


    # Building LaTeX table

    # Header
    header = '\\begin{table*}[]\n'
    header += '\\centering\n'
    header += '\\caption{My caption}\n'
    header += '\\label{table:methodsperformance}\n'
    header += '\\begin{tabular}{lccc@{\\hskip 0.3in}ccc@{\\hskip 0.4in}c}\n'
    header += '\\toprule\n'
    header += '\\multirow{2}{*}{\\textbf{Method}} & \\multicolumn{3}{c|}{\\textbf{WikiSim}} & \\multicolumn{3}{c}{\\textbf{WiRe}} \\\\\n'
    header += '& \\textbf{Pearson} & \\textbf{Spearman} & \\textbf{Harmonic} & \\textbf{Pearson} & \\textbf{Spearman} & \\textbf{Harmonic} & \\textbf{AVG} \\\\ \hline \n'

    # Table rows
    latexrow = []
    for i, r in df.iterrows():
        for c in columns:
            #if c == 'Method':
            #    latexrow.append( '\\textsc{' + str(r[c]).lower() + '}' )
            #else:
            latexrow.append( r[c] )
            latexrow.append( ' & ' )

        # clean, finalize line
        del latexrow[-1]
        latexrow.append( '\\\\\n' )

    # Footer
    footer = '\\bottomrule \\\\\n'
    footer += '\\end{tabular}\n'
    footer += '\\end{table*}\n'

    # Writing
    latex = header + ''.join( latexrow ) + footer
    open(outfile, 'w').write(latex)

