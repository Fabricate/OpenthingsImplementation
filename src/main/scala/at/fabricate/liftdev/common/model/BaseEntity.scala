package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.KeyedMapper
import net.liftweb.mapper.KeyedMetaMapper
import net.liftweb.mapper.Mapper
import net.liftweb.mapper.BaseMetaMapper
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.LongKeyedMetaMapper

trait BaseEntity[T <: (BaseEntity[T])]  extends LongKeyedMapper[T]  {
	self: T =>

}

trait BaseMetaEntity[ModelType <: (BaseEntity[ModelType])]  extends LongKeyedMetaMapper[ModelType]  {
	self: ModelType =>
	  type TheBaseEntityType = ModelType
	  // will be abstract override modified in subclasses
	  def init() : Unit = {}
	  def getItemsToSchemify : List[BaseMetaMapper] = List(self)
}
