package it.unipi.di.acubelab.wikipediarelatedness.utils

import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap

// Rewriting Memoize code with FastUtil hashtables.
// Original code from: http://stackoverflow.com/questions/3640823/what-type-to-use-to-store-an-in-memory-mutable-data-table-in-scala


/**
  * Memoization with FastUtil hashtable.
  *
  * @param f
  */
class IntInt2FloatMemoize(f: (Int, Int) => Float) {

    protected val mem = new Long2FloatOpenHashMap()


    def apply(x: Int, y: Int): Float = {

      val key = getCacheKey(x, y)

      if( mem.containsKey(key) ) {
        mem.get(key)
      } else {

        val score = f(x, y)
        mem.put(key, score)
        score
      }

    }


    protected def getCacheKey(srcWikiID: Int, dstWikiID: Int): Long = {
      val mn = Math.min(srcWikiID, dstWikiID)
      val mx = Math.max(srcWikiID, dstWikiID)

      val mnShifted = mn.asInstanceOf[Long] << 32
      mnShifted | mx
    }

}


object FastMemoize {

  def apply(f: (Int, Int) => Float) = new IntInt2FloatMemoize(f)
}

