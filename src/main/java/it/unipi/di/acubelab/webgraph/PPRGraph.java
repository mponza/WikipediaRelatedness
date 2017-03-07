package it.unipi.di.acubelab.webgraph;


import it.unimi.dsi.webgraph.NodeIterator;
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraph;
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.webgraph.graph.WikiBVGraphFactory;

/**
 * Class which contains the pre-processed information for the PageRank computation (e.g. outdegree).
 *
 */
public class PPRGraph {
    public WikiBVGraph wikiBVgraph;
    public int n;
    public int[] outdegree;

    public PPRGraph() {
        this.wikiBVgraph =  WikiBVGraphFactory.make("in", true);
        this.n = wikiBVgraph.graph().numNodes();

        // Computing outdegree
        int i;
        if(this.outdegree == null) {
            this.outdegree = new int[n];
            NodeIterator danglingNodes = this.wikiBVgraph.graph().nodeIterator();
            i = this.n;

            while(i-- != 0) {
                danglingNodes.nextInt();
                int[] pred = danglingNodes.successorArray();

                for(int d = danglingNodes.outdegree(); d-- != 0; ++this.outdegree[pred[d]]) {
                    ;
                }
            }
        }
    }
}
