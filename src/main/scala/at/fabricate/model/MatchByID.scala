package at.fabricate
package model

import net.liftweb.mapper.KeyedMetaMapper
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.By
import net.liftweb.mapper.KeyedMapper



trait MatchByID [T <: (KeyedMapper[Long,T]) ]  { // 
  self: T =>
     final object MatchItemByID  {     		        
	    def unapply(in: String): Option[T] = 
	      self.getSingleton.findByKey(in.toLong)
	  }
}

/*

trait MatchByIDMeta [ModelType <: ( MatchByID[ModelType] with LongKeyedMapper[ModelType]) ] extends KeyedMetaMapper[Long, ModelType] {
  self : ModelType =>  
    

      type TheMatchingType = ModelType
//    self.FindByID
    /*
  object FindByID extends FieldOwner[KeyedMetaMapper[Long,ModelType]](self) { 
    //self: TheIconType =>
        /*
    def apply(in: Long): Option[TheIconType] = 
      fieldOwner.find( // As[LongKeyedMetaMapper[TheType]]
    		    By(fieldOwner.primaryKeyField, 
    		        in ))
    		        * 
    		        */
    		        
    def unapply(in: String): Option[ModelType] = 
      fieldOwner.find( // As[LongKeyedMetaMapper[TheType]]
    		    By(fieldOwner.primaryKeyField, 
    		        in.toLong ))
  }
  * 
}
* 
* 
*/
* 
*/
