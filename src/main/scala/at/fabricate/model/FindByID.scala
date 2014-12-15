package at.fabricate
package model

import net.liftweb.mapper.KeyedMetaMapper
import net.liftweb.mapper.By
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.LongKeyedMapper

trait FindByID[T <: (LongKeyedMetaMapper[T])] {
  self: T =>
  
  object FindByID extends FieldOwner[T](self) {
    def unapply(in: String): Option[T] = 
      fieldOwner.find( // As[LongKeyedMetaMapper[TheType]]
    		    By(fieldOwner.primaryKeyField, 
    		        in.toInt ))
  }

}