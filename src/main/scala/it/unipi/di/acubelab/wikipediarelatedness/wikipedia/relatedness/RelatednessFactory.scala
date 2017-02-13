package it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness

import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.embeddings.neural.line.LINEEmbeddings
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.subgraph.CliqueRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.clustering.{CosineLocalClusteringRelatedness, JaccardLocalClusteringRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.embeddings.latent.{LDARelatedness, SVDRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.lm.LMRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.lucene.esa.{ESAEntityRelatedness, ESARelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.lucene.vsm.VectorSpaceModelRelatedeness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.neural.{LINERelatedness, Word2VecRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.casaro.{CasaroCoSimRankRelatedness, CasaroPPRRelatedness}
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.randomwalks.PPRRelatedness
import it.unipi.di.acubelab.wikipediarelatedness.wikipedia.relatedness.set._


object RelatednessFactory {


  def make(options: RelatednessOptions) : Relatedness = options.name.toLowerCase match {

    //
    // Set-based
    case "milnewitten" => new MilneWittenRelatedness(options)
    case "jaccard" =>   new JaccardRelatedness(options)

    case "adamicadar" => new AdamicAdarRelatedness(options)
    case "biblio" => new BibliographicCopulingRelatedness(options)
    case "cocitation" => new CoCitationRelatedness(options)
    case "common" => new CommonNeighborsRelatedness(options)
    case "dice" => new DiceRelatedness(options)
    case "overlap" => new OverlapRelatedness(options)
    case "preferential" => new PreferentialAttachmentRelatedness(options)

    //
    // LocalClustering-based
    case "jaccardlocalclustering" => new JaccardLocalClusteringRelatedness(options)
    case "cosinelocalclustering" => new CosineLocalClusteringRelatedness(options)

    //
    // Lucene-based
    case "esa" => new ESARelatedness(options)
    case "esaentity" => new ESAEntityRelatedness(options)
    case "vsm" => new VectorSpaceModelRelatedeness(options)

    //
    // Latent
    case "svd" => new SVDRelatedness(options)
    case "lda" => new LDARelatedness(options)

    //
    // Neural
    case "w2v" => new Word2VecRelatedness(options)
    case "line" => new LINERelatedness(options)

    //
    // Language Model
    case "lm" => new LMRelatedness(options)

    //
    // Clique-subgraph-based
    case "clique" => new CliqueRelatedness(options)

    //
    // RandomWalks
    case "ppr" => new PPRRelatedness(options)

    case "casaro.csr" => new CasaroCoSimRankRelatedness(options)
    case "casaro.ppr" => new CasaroPPRRelatedness(options)


    case "mix" => new MixedRelatedness(options)
  }

}