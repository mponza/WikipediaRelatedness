package it.unimi.dsi.webgraph;

import com.martiansoftware.jsap.*;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.io.BinIO;
import it.unimi.dsi.law.rank.PageRank;
import it.unimi.dsi.law.util.KahanSummation;
import it.unimi.dsi.logging.ProgressLogger;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.BitSet;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Adapted PageRankParallelGaussSeidel in order to compute the PPR computation step by step.
 *
 */
public class StepPageRankParallelGaussSeidel extends PageRank {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepPageRankParallelGaussSeidel.class);
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

    public StepPageRankParallelGaussSeidel(ImmutableGraph transpose, int requestedThreads, Logger logger) {
        super(transpose, logger);
        this.progressLogger = new ProgressLogger(logger, "nodes");
        this.iterationLogger = new ProgressLogger(logger, "iterations");
        this.numberOfThreads = requestedThreads != 0?requestedThreads:Runtime.getRuntime().availableProcessors();
        this.nextNode = new AtomicLong();
    }

    public StepPageRankParallelGaussSeidel(ImmutableGraph transpose) {
        this(transpose, 0, LOGGER);
    }

    public StepPageRankParallelGaussSeidel(ImmutableGraph transpose, Logger logger) {
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
        }

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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Computes one PageRank iteration without initialisation.
    // Usage:
    //  1. init()
    //  2. Call step()
    //  3. Retrieve PPR vectors

    public void step() throws IOException {
        StoppingCriterion stoppingCriterion = new IterationNumberStoppingCriterion(1);


        StepPageRankParallelGaussSeidel.IterationThread[] thread = new StepPageRankParallelGaussSeidel.IterationThread[this.numberOfThreads];

        int i;
        for(i = thread.length; i-- != 0; thread[i] = new StepPageRankParallelGaussSeidel.IterationThread()) {  // original: PageRankParallelGaussSeidel.IterationThread(null)
            ;
        }

        this.barrier = new CyclicBarrier(this.numberOfThreads, new Runnable() {
            public void run() {
                if(StepPageRankParallelGaussSeidel.this.iteration > 0) {
                    StepPageRankParallelGaussSeidel.this.progressLogger.done();
                    StepPageRankParallelGaussSeidel.this.iterationLogger.setAndDisplay((long)StepPageRankParallelGaussSeidel.this.iteration);
                    if(stoppingCriterion.shouldStop(StepPageRankParallelGaussSeidel.this)) {
                        StepPageRankParallelGaussSeidel.this.completed = true;
                        return;
                    }

                    StepPageRankParallelGaussSeidel.this.danglingRank = StepPageRankParallelGaussSeidel.this.danglingRankAccumulator;
                    StepPageRankParallelGaussSeidel.this.danglingRankAccumulator = 0.0D;
                }

                StepPageRankParallelGaussSeidel.this.normDelta = StepPageRankParallelGaussSeidel.this.danglingRankAccumulator = 0.0D;
                StepPageRankParallelGaussSeidel.this.nextNode.set(0L);
                StepPageRankParallelGaussSeidel.this.progressLogger.expectedUpdates = (long)StepPageRankParallelGaussSeidel.this.n;
                StepPageRankParallelGaussSeidel.this.progressLogger.start("Iteration " + StepPageRankParallelGaussSeidel.this.iteration++ + "...");
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void stepUntil(final StoppingCriterion stoppingCriterion) throws IOException {
        this.init();
        StepPageRankParallelGaussSeidel.IterationThread[] thread = new StepPageRankParallelGaussSeidel.IterationThread[this.numberOfThreads];

        int i;
        for(i = thread.length; i-- != 0; thread[i] = new StepPageRankParallelGaussSeidel.IterationThread()) {  // original: PageRankParallelGaussSeidel.IterationThread(null)
            ;
        }

        this.barrier = new CyclicBarrier(this.numberOfThreads, new Runnable() {
            public void run() {
                if(StepPageRankParallelGaussSeidel.this.iteration > 0) {
                    StepPageRankParallelGaussSeidel.this.progressLogger.done();
                    StepPageRankParallelGaussSeidel.this.iterationLogger.setAndDisplay((long)StepPageRankParallelGaussSeidel.this.iteration);
                    if(stoppingCriterion.shouldStop(StepPageRankParallelGaussSeidel.this)) {
                        StepPageRankParallelGaussSeidel.this.completed = true;
                        return;
                    }

                    StepPageRankParallelGaussSeidel.this.danglingRank = StepPageRankParallelGaussSeidel.this.danglingRankAccumulator;
                    StepPageRankParallelGaussSeidel.this.danglingRankAccumulator = 0.0D;
                }

                StepPageRankParallelGaussSeidel.this.normDelta = StepPageRankParallelGaussSeidel.this.danglingRankAccumulator = 0.0D;
                StepPageRankParallelGaussSeidel.this.nextNode.set(0L);
                StepPageRankParallelGaussSeidel.this.progressLogger.expectedUpdates = (long)StepPageRankParallelGaussSeidel.this.n;
                StepPageRankParallelGaussSeidel.this.progressLogger.start("Iteration " + StepPageRankParallelGaussSeidel.this.iteration++ + "...");
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

    public static void main(String[] arg) throws IOException, JSAPException, ConfigurationException, ClassNotFoundException {
        SimpleJSAP jsap = new SimpleJSAP(StepPageRankParallelGaussSeidel.class.getName(), "Computes PageRank of a graph, given its transpose, using a parallel implementation of Gauss-Seidel\'s method. The file <rankBasename>.properties stores metadata about the computation, whereas the file <rankBasename>.ranks stores the result as a sequence of doubles in DataInput format.", new Parameter[]{new Switch("expand", 'e', "expand", "Expand the graph to increase speed (no compression)."), new FlaggedOption("alpha", JSAP.DOUBLE_PARSER, Double.toString(0.85D), false, 'a', "alpha", "Damping factor."), new FlaggedOption("maxIter", JSAP.INTEGER_PARSER, Integer.toString(2147483647), false, 'i', "max-iter", "Maximum number of iterations."), new FlaggedOption("threshold", JSAP.DOUBLE_PARSER, Double.toString(1.0E-6D), false, 't', "threshold", "Threshold (in l_1 norm, if no norm vector has been specified; in the weighted supremum norm otherwise) to determine whether to stop."), new FlaggedOption("preferenceVector", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, false, 'p', "preference-vector", "A preference vector stored as a vector of binary doubles."), new FlaggedOption("preferenceObject", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, false, 'P', "preference-object", "A preference vector stored as a serialised DoubleList."), new Switch("pseudoRank", '\u0000', "pseudorank", "Compute pseudoranks (the dangling preference is set to 0)."), new FlaggedOption("normVector", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, false, 'n', "norm-vector", "A vector inducing the correct weighted supremum norm."), new FlaggedOption("sigma", JSAP.DOUBLE_PARSER, JSAP.NO_DEFAULT, false, 's', "sigma", "The value for which the norm vector is suitable (i.e., the maximum ratio from its properties)."), new FlaggedOption("buckets", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, false, 'b', "buckets", "The buckets of the graph; if supplied, buckets will be treated as dangling nodes."), new Switch("mapped", 'm', "mapped", "Use loadMapped() to load the graph."), new Switch("strongly", 'S', "strongly", "use the preference vector to redistribute the dangling rank."), new FlaggedOption("threads", JSAP.INTSIZE_PARSER, "0", false, 'T', "threads", "The number of threads to be used. If 0, the number will be estimated automatically."), new UnflaggedOption("transposeBasename", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, true, false, "The basename of the transpose of the graph."), new UnflaggedOption("rankBasename", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, true, false, "The filename where the resulting rank (doubles in binary form) are stored.")});
        JSAPResult jsapResult = jsap.parse(arg);
        if(jsap.messagePrinted()) {
            System.exit(1);
        }

        boolean mapped = jsapResult.getBoolean("mapped", false);
        boolean strongly = jsapResult.getBoolean("strongly", false);
        String graphBasename = jsapResult.getString("transposeBasename");
        String rankBasename = jsapResult.getString("rankBasename");
        String normVectorFilename = jsapResult.getString("normVector");
        if(normVectorFilename != null && !jsapResult.userSpecified("sigma")) {
            throw new IllegalArgumentException("You must specify the sigma for which the norm vector is suitable");
        } else {
            String buckets = jsapResult.getString("buckets");
            int threads = jsapResult.getInt("threads");
            ProgressLogger progressLogger = new ProgressLogger(LOGGER, "nodes");
            ImmutableGraph graph = mapped?ImmutableGraph.loadMapped(graphBasename, progressLogger):ImmutableGraph.load(graphBasename, progressLogger);
            Object preference = null;
            String preferenceFilename = null;
            if(jsapResult.userSpecified("preferenceVector")) {
                preference = DoubleArrayList.wrap(BinIO.loadDoubles(preferenceFilename = jsapResult.getString("preferenceVector")));
            }

            if(jsapResult.userSpecified("preferenceObject")) {
                if(jsapResult.userSpecified("preferenceVector")) {
                    throw new IllegalArgumentException("You cannot specify twice the preference vector");
                }

                preference = (DoubleList)BinIO.loadObject(preferenceFilename = jsapResult.getString("preferenceObject"));
            }

            if(strongly && preference == null) {
                throw new IllegalArgumentException("The \'strongly\' option requires a preference vector");
            } else {
                if(jsapResult.userSpecified("expand")) {
                    graph = (new ArrayListMutableGraph(graph)).immutableView();
                }

                StepPageRankParallelGaussSeidel pr = new StepPageRankParallelGaussSeidel(graph, threads, LOGGER);
                pr.alpha = jsapResult.getDouble("alpha");
                pr.preference = (DoubleList)preference;
                pr.buckets = (BitSet)((BitSet)(buckets == null?null:BinIO.loadObject(buckets)));
                pr.stronglyPreferential = strongly;
                pr.pseudoRank = jsapResult.userSpecified("pseudoRank");
                if(normVectorFilename != null) {
                    pr.normVector(normVectorFilename, jsapResult.getDouble("sigma"));
                }

                pr.stepUntil(or(new NormStoppingCriterion(jsapResult.getDouble("threshold")), new IterationNumberStoppingCriterion(jsapResult.getInt("maxIter"))));
                BinIO.storeDoubles(pr.rank, rankBasename + ".ranks");
                pr.buildProperties(graphBasename, preferenceFilename, (String)null).save(rankBasename + ".properties");
            }
        }
    }

    private final class IterationThread extends Thread {
        private static final int GRANULARITY = 10000;

        private IterationThread() {
        }

        public void run() {
            try {
                ImmutableGraph t = StepPageRankParallelGaussSeidel.this.graph.copy();
                int n = StepPageRankParallelGaussSeidel.this.n;
                double oneMinusAlpha = 1.0D - StepPageRankParallelGaussSeidel.this.alpha;
                double oneMinusAlphaOverN = oneMinusAlpha / (double)n;
                double[] rank = StepPageRankParallelGaussSeidel.this.rank;
                int[] outdegree = StepPageRankParallelGaussSeidel.this.outdegree;
                BitSet buckets = StepPageRankParallelGaussSeidel.this.buckets;
                boolean pseudoRank = StepPageRankParallelGaussSeidel.this.pseudoRank;
                double alpha = StepPageRankParallelGaussSeidel.this.alpha;
                DoubleList danglingNodeDistribution = StepPageRankParallelGaussSeidel.this.danglingNodeDistribution;
                DoubleList preference = StepPageRankParallelGaussSeidel.this.preference;
                KahanSummation s = new KahanSummation();

                while(true) {
                    StepPageRankParallelGaussSeidel.this.barrier.await();
                    if(StepPageRankParallelGaussSeidel.this.completed) {
                        return;
                    }

                    double danglingRank = StepPageRankParallelGaussSeidel.this.danglingRank;

                    while(true) {
                        long start = StepPageRankParallelGaussSeidel.this.nextNode.getAndAdd(10000L);
                        if(start >= (long)n) {
                            StepPageRankParallelGaussSeidel.this.nextNode.getAndAdd(-10000L);
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

                                        if(StepPageRankParallelGaussSeidel.this.normVector != null) {
                                            norm = Math.max(norm, Math.abs(newRank - rank[i]) * (double)(1L << (255 & StepPageRankParallelGaussSeidel.this.normVector[i])));
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

                        synchronized(StepPageRankParallelGaussSeidel.this.progressLogger) {
                            StepPageRankParallelGaussSeidel.this.progressLogger.update((long)end - start);
                        }

                        StepPageRankParallelGaussSeidel var40 = StepPageRankParallelGaussSeidel.this;
                        synchronized(StepPageRankParallelGaussSeidel.this) {
                            StepPageRankParallelGaussSeidel var41 = StepPageRankParallelGaussSeidel.this;
                            var41.danglingRankAccumulator = var41.danglingRankAccumulator + danglingRankAccumulator;
                            if(StepPageRankParallelGaussSeidel.this.normVector != null) {
                                StepPageRankParallelGaussSeidel.this.normDelta = Math.max(StepPageRankParallelGaussSeidel.this.normDelta, norm);
                            } else {
                                var41 = StepPageRankParallelGaussSeidel.this;
                                var41.normDelta = var41.normDelta + norm;
                            }
                        }
                    }
                }
            } catch (Throwable var39) {
                StepPageRankParallelGaussSeidel.this.threadThrowable = var39;
            }
        }
    }
}
