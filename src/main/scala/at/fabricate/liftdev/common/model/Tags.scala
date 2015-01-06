package at.fabricate.liftdev.common
package model

trait TagsMeta[ModelType <: (TagsMeta[ModelType]) ] extends BaseMetaEntity[ModelType] with BaseMetaEntityWithTitleAndDescription[ModelType] {
	self: ModelType => 
  
}
  

trait Tags[T <: Tags[T] ] extends BaseEntity[T] with BaseEntityWithTitleAndDescription[T] {
  	self: T =>


}