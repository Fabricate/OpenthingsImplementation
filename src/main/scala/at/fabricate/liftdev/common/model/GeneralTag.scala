package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.IdPK

trait GeneralTagMeta[ModelType <: (GeneralTag[ModelType]) ] extends BaseMetaEntity[ModelType] with BaseMetaEntityWithTitleAndDescription[ModelType] {
	self: ModelType => 
  
}
  

trait GeneralTag[T <: GeneralTag[T] ] extends BaseEntity[T] with BaseEntityWithTitleAndDescription[T] with IdPK with EnsureUniqueTextFields[T] {
  	self: T =>

	 override type TheUniqueTextType = TheTranslation

	override def theUniqueFields = List(TheTranslationMeta.title)
}