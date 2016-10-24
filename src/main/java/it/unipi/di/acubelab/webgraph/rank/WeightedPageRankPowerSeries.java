package it.unipi.di.acubelab.webgraph.rank;

import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.law.rank.PageRankPowerSeries;
import it.unimi.dsi.logging.ProgressLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.LazyIntIterator;
import it.unimi.dsi.webgraph.NodeIterator;
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraph;
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.processing.webgraph.graph.WikiGraphFactory;
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.Relatedness;
import org.slf4j.Logger;
import scala.Tuple2;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Weighted version of PageRank via WebGraph.
 *  - init(), uses relatedenss to generate the normalized graph weights.
 *  - step(), the PageRank score is distributed by the weights (line: while (j -- != 0)newRank[ succ[ j ] ] += oldRank[ i ] * getWeight(i, j);)
 */
public class WeightedPageRankPowerSeries extends PageRankPowerSeries {

    private final ProgressLogger progressLogger;
    private final ProgressLogger iterationLogger;

    public WikiGraph wikiGraph;
    public Relatedness relatedness;

    public Object2DoubleOpenHashMap weights;  // {(src, dst) -> (normalized) relatedness}


    public WeightedPageRankPowerSeries(Relatedness relatedness, final Logger logger) {
        this(WikiGraphFactory.outGraph().graph(), logger);
        wikiGraph = WikiGraphFactory.outGraph();
        this.relatedness = relatedness;
    }


    public WeightedPageRankPowerSeries(final ImmutableGraph graph, final Logger logger) {
        super(graph, logger);
        progressLogger = new ProgressLogger( logger, "nodes" );
        iterationLogger = new ProgressLogger( logger, "iterations" );
    }


    public void updateWeight(Integer srcNodeID, Integer dstNodeID) {
        Tuple2<Integer, Integer> key = new Tuple2<>(srcNodeID, dstNodeID);

        if(!weights.containsKey(key)) {

            // Updates weights entry (relatedness are symmetric).
            int srcWikiID = wikiGraph.getWikiID(srcNodeID);
            int dstWikiID = wikiGraph.getWikiID(dstNodeID);

            weights.put(key, relatedness.computeRelatedness(srcWikiID, dstWikiID));

            Tuple2<Integer, Integer> keyRev = new Tuple2<>(dstNodeID, srcNodeID);
            weights.put(keyRev, relatedness.computeRelatedness(srcWikiID, dstWikiID));
        }
    }


    public double getWeight(Integer srcNodeID, Integer dstNodeID) {
        Tuple2<Integer, Integer> key = new Tuple2<>(srcNodeID, dstNodeID);
        return (double) weights.getOrDefault(key, 0.0);
    }


    public void generateWeights() {
        logger.info("Graph weighting...");

        for(int i = 0; i < n; i--) {

            LazyIntIterator nodeIterator = wikiGraph.successors(i);
            int successor = nodeIterator.nextInt();

            while(successor != -1) {
                updateWeight(i, successor);
                successor = nodeIterator.nextInt();
            }
        }

        logger.info("Graph weighted!");
    }


    public void normalizeWeights() {
        logger.info("Graph weight normalization...");

        for(int i = 0; i < n; i--) {
            LazyIntIterator nodeIterator = wikiGraph.successors(i);

            // Gets sum of the relatedness (weights) between i and its successors.
            int successor = nodeIterator.nextInt();
            double sumWeights = 0.0;

            while(successor != -1) {
                sumWeights += getWeight(i, successor);
            }

            // Normalize weights
            nodeIterator = wikiGraph.successors(i);
            successor = nodeIterator.nextInt();

            while(successor != -1) {
                Tuple2<Integer, Integer> key = new Tuple2<>(i, successor);
               weights.put(key, weights.getDouble(key) / sumWeights);
            }

        }

        logger.info("Graph weights normalized!");
    }




    @Override
    public void init() throws IOException {
        super.init();
        // Creates the arrays, if necessary
        if ( previousRank == null ) previousRank = new double[ n ];
        derivative = new double[ order.length ][ subset != null ? subset.length : n ];
        if ( IntArrayList.wrap( order ).indexOf( 0 ) != -1 ) throw new IllegalArgumentException( "You cannot compute the derivative of order 0 (use PageRank instead)" );
        if ( coeffBasename != null ) BinIO.storeDoubles( rank, coeffBasename + "-" + 0 );

        generateWeights();
        normalizeWeights();

        logger.info( "Completed." );
        iterationLogger.start();
    }


    @Override
    public void step() throws IOException {
        double[] oldRank = rank, newRank = previousRank;
        DoubleArrays.fill( newRank, 0 );

        // for each node, calculate its outdegree and redistribute its rank among pointed nodes
        double accum = 0.0;

        progressLogger.expectedUpdates = n;
        progressLogger.start( "Iteration " + iteration++ + "..." );

        final NodeIterator nodeIterator = graph.nodeIterator();
        int outdegree;
        int[] succ;

        for( int i = 0; i < n; i++ ) {
            nodeIterator.nextInt();
            outdegree = nodeIterator.outdegree();

            if ( outdegree == 0 || buckets != null && buckets.get( i ) ) accum += oldRank[ i ];
            else {
                int j = outdegree;
                succ = nodeIterator.successorArray();
                while (j -- != 0)newRank[ succ[ j ] ] += oldRank[ i ] * getWeight(i, j);
                //while ( j-- != 0 ) newRank[ succ[ j ] ] += oldRank[ i ] / outdegree;
            }
            progressLogger.update();
        }
        progressLogger.done();

        final double accumOverNumNodes = accum / n;

        final double oneOverNumNodes = 1.0 / n;
        if ( preference != null )
            if ( danglingNodeDistribution == null )
                for( int i = n; i-- != 0; ) newRank[ i ] = alpha * newRank[ i ] + ( 1 - alpha ) * preference.getDouble( i ) + alpha * accumOverNumNodes;
            else
                for( int i = n; i-- != 0; ) newRank[ i ] = alpha * newRank[ i ] + ( 1 - alpha ) * preference.getDouble( i ) + alpha * accum * danglingNodeDistribution.getDouble( i );
        else
        if ( danglingNodeDistribution == null )
            for( int i = n; i-- != 0; ) newRank[ i ] = alpha * newRank[ i ] + ( 1 - alpha ) * oneOverNumNodes + alpha * accumOverNumNodes;
        else
            for( int i = n; i-- != 0; ) newRank[ i ] = alpha * newRank[ i ] + ( 1 - alpha ) * oneOverNumNodes + alpha * accum * danglingNodeDistribution.getDouble( i );

        //make the rank just computed the new rank
        rank = newRank;
        previousRank = oldRank;

        // Compute derivatives.
        if ( subset == null ) {
            for( int i = 0; i < order.length; i++ ) {
                final int k = order[ i ];
                final double alphak = Math.pow( alpha, k );
                final double nFallingK = it.unimi.dsi.law.Util.falling( iteration, k );
                for( int j = 0; j < n; j++ ) derivative[ i ][ j ] += nFallingK * ( rank[ j ] - previousRank[ j ] ) / alphak;
            }
        }
        else {
            for( int i = 0; i < order.length; i++ ) {
                final int k = order[ i ];
                final double alphak = Math.pow( alpha, k );
                final double nFallingK = it.unimi.dsi.law.Util.falling( iteration, k );

                for( int t: subset ) derivative[ i ][ t ] += nFallingK * ( rank[ t ] - previousRank[ t ] ) / alphak;
            }
        }

        // Compute coefficients, if required.

        if ( coeffBasename != null ) {
            final DataOutputStream coefficients = new DataOutputStream( new FastBufferedOutputStream( new FileOutputStream( coeffBasename + "-" + ( iteration ) ) ) );
            final double alphaN = Math.pow( alpha, iteration );
            for( int i = 0; i < n; i++ ) coefficients.writeDouble( ( rank[ i ] - previousRank[ i ] ) / alphaN );
            coefficients.close();
        }

        iterationLogger.setAndDisplay( iteration );
    }
}
