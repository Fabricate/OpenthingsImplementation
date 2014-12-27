package at.fabricate
package model

import net.liftweb.mapper.KeyedMetaMapper
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.By
import net.liftweb.mapper.KeyedMapper



trait MatchByID [T <: (KeyedMapper[Long,T]) ]  {
  self: T =>
    // add an Object for pattern matching
     final object MatchItemByID  {     		        
	    def unapply(in: String): Option[T] = 
	      self.getSingleton.findByKey(in.toLong)
	  }
}