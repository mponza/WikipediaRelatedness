package it.unipi.di.acubelab.webgraph;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.law.rank.PageRank;
import it.unimi.dsi.law.util.KahanSummation;
import it.unimi.dsi.logging.ProgressLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.NodeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.BitSet;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Customization of PageRankParallelGaussSeidel which saves at each PageRank iteration the stationary distribution.
 * The Wikipedia (in) graph is used.
 */
public class PPRParallelGaussSeidel extends PageRank  {
    private static final Logger LOGGER = LoggerFactory.getLogger(PPRParallelGaussSeidel.class);
    private final ProgressLogger progressLogger;
    private final ProgressLogger iterationLogger;
    private final int numberOfThreads;
    private final AtomicLong nextNode;
    private double danglingRankAccumulator;
    private double danglingRank;
    private double normDelta;
    public int[] outdegree;
    private volatile boolean completed;
    private volatile CyclicBarrier barrier;
    private volatile Throwable threadThrowable;
    private byte[] normVector;
    private double sigma;
    public boolean pseudoRank;

    //
    // Added fields
    private static final PPRGraph pprGraph = new PPRGraph();

    //
    // Added Constructor
    public PPRParallelGaussSeidel() { this(pprGraph.wikiBVgraph.graph()); }


    public PPRParallelGaussSeidel(ImmutableGraph transpose, int requestedThreads, Logger logger) {
        super(transpose, logger);
        this.progressLogger = new ProgressLogger(logger, "nodes");
        this.iterationLogger = new ProgressLogger(logger, "iterations");
        this.numberOfThreads = requestedThreads != 0?requestedThreads:Runtime.getRuntime().availableProcessors();
        this.nextNode = new AtomicLong();
    }

    public PPRParallelGaussSeidel(ImmutableGraph transpose) {
        this(transpose, 0, LOGGER);
    }

    public PPRParallelGaussSeidel(ImmutableGraph transpose, Logger logger) {
        this(transpose, 0, logger);
    }

    public void normVector(String normVectorFilename, double sigma) throws IOException {
        this.normVector = normVectorFilename == null?null:this.approximateNormVector(BinIO.asDoubleIterator(normVectorFilename));
        this.sigma = sigma;
    }

    public void normVector(double[] normVector, double sigma) {
        this.normVector = this.approximateNormVector(DoubleIterators.wrap(normVector));
        this.sigma = sigma;
    }

    public void init() throws IOException {
        super.init();
        if(this.normVector != null) {
            if(!this.pseudoRank) {
                throw new IllegalStateException("Norm vectors can be used only when computing pseudoranks");
            }

            if(this.alpha >= 1.0D / this.sigma) {
                throw new IllegalStateException("The specified norm vector can be used only with values of alpha smaller than " + 1.0D / this.sigma);
            }
        }




        int i;
        /*
        if(this.outdegree == null) {
            this.outdegree = new int[this.n];
            this.progressLogger.expectedUpdates = (long)this.n;
            this.progressLogger.start("Computing outdegrees...");
            NodeIterator danglingNodes = this.graph.nodeIterator();
            i = this.n;

            while(i-- != 0) {
                danglingNodes.nextInt();
                int[] pred = danglingNodes.successorArray();

                for(int d = danglingNodes.outdegree(); d-- != 0; ++this.outdegree[pred[d]]) {
                    ;
                }

                this.progressLogger.lightUpdate();
            }

            this.progressLogger.done();
        }*/
        outdegree = pprGraph.outdegree;

        this.progressLogger.expectedUpdates = (long)this.n;
        this.progressLogger.start("Computing initial dangling rank...");
        this.danglingRank = 0.0D;
        int var5 = 0;
        i = this.n;

        while(true) {
            do {
                if(i-- == 0) {
                    this.progressLogger.done();
                    this.logger.info(var5 + " dangling nodes");
                    if(this.buckets != null) {
                        this.logger.info(this.buckets.cardinality() + " buckets");
                    }

                    this.logger.info("Initial dangling rank: " + this.danglingRank);
                    this.normDelta = this.danglingRankAccumulator = 0.0D;
                    this.completed = false;
                    this.logger.info("Completed.");
                    this.iterationLogger.start();
                    return;
                }
            } while(this.outdegree[i] != 0 && (this.buckets == null || !this.buckets.get(i)));

            this.danglingRank += this.rank[i];
            if(this.outdegree[i] == 0) {
                ++var5;
            }
        }
    }

    public void step() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * It works as classical stepUntil, but:
     *      1. It drugs preference with standard basis vector in nodeID(srcWikiID)\
     *      2. fill pprVector with the stationary distribution of each
     *
     * @param stoppingCriterion
     * @param pprTask saves the stationary distribution at each iteration inside this
     * @throws IOException
     */
    public void stepUntil(final StoppingCriterion stoppingCriterion, PPRTask pprTask) throws IOException {
        // Set preference vector before initialization
        //int nodeID = pprGraph.wikiBVgraph.getNodeID(srcWikiID);
        //this.preference = new DoubleArrayList(new double[pprGraph.n]);
        //this.preference.set(nodeID, 1.0);
        this.preference = pprTask.preference();

        this.init();
        PPRParallelGaussSeidel.IterationThread[] thread = new PPRParallelGaussSeidel.IterationThread[this.numberOfThreads];

        int i;
        for(i = thread.length; i-- != 0; thread[i] = new PPRParallelGaussSeidel.IterationThread()) {
            ;
        }

        this.barrier = new CyclicBarrier(this.numberOfThreads, new Runnable() {
            public void run() {
                if(PPRParallelGaussSeidel.this.iteration > 0) {
                    PPRParallelGaussSeidel.this.progressLogger.done();
                    PPRParallelGaussSeidel.this.iterationLogger.setAndDisplay((long) PPRParallelGaussSeidel.this.iteration);
                    if(stoppingCriterion.shouldStop(PPRParallelGaussSeidel.this)) {
                        PPRParallelGaussSeidel.this.completed = true;
                        return;
                    }

                    PPRParallelGaussSeidel.this.danglingRank = PPRParallelGaussSeidel.this.danglingRankAccumulator;
                    PPRParallelGaussSeidel.this.danglingRankAccumulator = 0.0D;
                }

                PPRParallelGaussSeidel.this.normDelta = PPRParallelGaussSeidel.this.danglingRankAccumulator = 0.0D;
                PPRParallelGaussSeidel.this.nextNode.set(0L);
                PPRParallelGaussSeidel.this.progressLogger.expectedUpdates = (long) PPRParallelGaussSeidel.this.n;
                pprTask.add(PPRParallelGaussSeidel.this.rank);
                PPRParallelGaussSeidel.this.progressLogger.start("Iteration " + PPRParallelGaussSeidel.this.iteration++ + "...");
            }
        });
        i = thread.length;

        while(i-- != 0) {
            thread[i].start();
        }

        i = thread.length;

        while(i-- != 0) {
            try {
                thread[i].join();
            } catch (InterruptedException var5) {
                throw new RuntimeException(var5);
            }
        }

        if(this.threadThrowable != null) {
            throw new RuntimeException(this.threadThrowable);
        } else {
            if(this.progressLogger != null) {
                this.progressLogger.done();
            }

            this.iterationLogger.done();
        }
    }

    public double normDelta() {
        return this.normVector == null?this.normDelta * this.alpha / (1.0D - this.alpha):this.alpha * this.sigma * this.normDelta / (1.0D - this.alpha * this.sigma);
    }

    public void clear() {
        super.clear();
        this.outdegree = null;
    }

    private final class IterationThread extends Thread {
        private static final int GRANULARITY = 10000;

        private IterationThread() {
        }

        public void run() {
            try {
                ImmutableGraph t = PPRParallelGaussSeidel.this.graph.copy();
                int n = PPRParallelGaussSeidel.this.n;
                double oneMinusAlpha = 1.0D - PPRParallelGaussSeidel.this.alpha;
                double oneMinusAlphaOverN = oneMinusAlpha / (double)n;
                double[] rank = PPRParallelGaussSeidel.this.rank;
                int[] outdegree = PPRParallelGaussSeidel.this.outdegree;
                BitSet buckets = PPRParallelGaussSeidel.this.buckets;
                boolean pseudoRank = PPRParallelGaussSeidel.this.pseudoRank;
                double alpha = PPRParallelGaussSeidel.this.alpha;
                DoubleList danglingNodeDistribution = PPRParallelGaussSeidel.this.danglingNodeDistribution;
                DoubleList preference = PPRParallelGaussSeidel.this.preference;
                KahanSummation s = new KahanSummation();

                while(true) {
                    PPRParallelGaussSeidel.this.barrier.await();
                    if(PPRParallelGaussSeidel.this.completed) {
                        return;
                    }

                    double danglingRank = PPRParallelGaussSeidel.this.danglingRank;

                    while(true) {
                        long start = PPRParallelGaussSeidel.this.nextNode.getAndAdd(10000L);
                        if(start >= (long)n) {
                            PPRParallelGaussSeidel.this.nextNode.getAndAdd(-10000L);
                            break;
                        }

                        int end = (int)Math.min((long)n, start + 10000L);
                        double danglingRankAccumulator = 0.0D;
                        double norm = 0.0D;
                        NodeIterator nodeIterator = t.nodeIterator((int)start);

                        label131:
                        for(int i = (int)start; i < end; ++i) {
                            nodeIterator.nextInt();
                            s.reset();
                            boolean hasLoop = false;
                            int[] pred = nodeIterator.successorArray();
                            int selfDanglingRank = nodeIterator.outdegree();

                            while(true) {
                                int currPred;
                                do {
                                    if(selfDanglingRank-- == 0) {
                                        double selfLoopFactor;
                                        double var42;
                                        if(outdegree[i] == 0 || buckets != null && buckets.get(i)) {
                                            var42 = rank[i];
                                            selfLoopFactor = pseudoRank?1.0D:(danglingNodeDistribution != null?1.0D - alpha * danglingNodeDistribution.getDouble(i):1.0D - alpha / (double)n);
                                        } else {
                                            var42 = 0.0D;
                                            selfLoopFactor = hasLoop?1.0D - alpha / (double)outdegree[i]:1.0D;
                                        }

                                        if(!pseudoRank) {
                                            s.add(danglingNodeDistribution != null?(danglingRank - var42) * danglingNodeDistribution.getDouble(i):(danglingRank - var42) / (double)n);
                                        }

                                        double newRank = ((preference != null?oneMinusAlpha * preference.getDouble(i):oneMinusAlphaOverN) + alpha * s.value()) / selfLoopFactor;
                                        if(outdegree[i] == 0 || buckets != null && buckets.get(i)) {
                                            danglingRankAccumulator += newRank;
                                        }

                                        if(PPRParallelGaussSeidel.this.normVector != null) {
                                            norm = Math.max(norm, Math.abs(newRank - rank[i]) * (double)(1L << (255 & PPRParallelGaussSeidel.this.normVector[i])));
                                        } else {
                                            norm += Math.abs(newRank - rank[i]);
                                        }

                                        rank[i] = newRank;
                                        continue label131;
                                    }

                                    currPred = pred[selfDanglingRank];
                                } while(buckets != null && buckets.get(pred[selfDanglingRank]));

                                if(i == currPred) {
                                    hasLoop = true;
                                } else {
                                    s.add(rank[currPred] / (double)outdegree[currPred]);
                                }
                            }
                        }

                        synchronized(PPRParallelGaussSeidel.this.progressLogger) {
                            PPRParallelGaussSeidel.this.progressLogger.update((long)end - start);
                        }

                        PPRParallelGaussSeidel var40 = PPRParallelGaussSeidel.this;
                        synchronized(PPRParallelGaussSeidel.this) {
                            PPRParallelGaussSeidel var41 = PPRParallelGaussSeidel.this;
                            var41.danglingRankAccumulator = var41.danglingRankAccumulator + danglingRankAccumulator;
                            if(PPRParallelGaussSeidel.this.normVector != null) {
                                PPRParallelGaussSeidel.this.normDelta = Math.max(PPRParallelGaussSeidel.this.normDelta, norm);
                            } else {
                                var41 = PPRParallelGaussSeidel.this;
                                var41.normDelta = var41.normDelta + norm;
                            }
                        }
                    }
                }
            } catch (Throwable var39) {
                PPRParallelGaussSeidel.this.threadThrowable = var39;
            }
        }
    }
}

