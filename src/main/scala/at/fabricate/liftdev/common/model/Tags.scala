package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.IdPK

trait TagsMeta[ModelType <: (Tags[ModelType]) ] extends BaseMetaEntity[ModelType] with BaseMetaEntityWithTitleAndDescription[ModelType] {
	self: ModelType => 
  
}
  

trait Tags[T <: Tags[T] ] extends BaseEntity[T] with BaseEntityWithTitleAndDescription[T] with IdPK {
  	self: T =>


}