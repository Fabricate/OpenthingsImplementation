package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.IdPK

trait GeneralSkillMeta[ModelType <: (GeneralSkill[ModelType]) ] extends BaseMetaEntity[ModelType] with BaseMetaEntityWithTitleAndDescription[ModelType] {
	self: ModelType => 
  
}
  

trait GeneralSkill[T <: GeneralSkill[T] ] extends BaseEntity[T] with BaseEntityWithTitleAndDescription[T] with IdPK with EnsureUniqueTextFields[T] {
  	self: T =>

	 override type TheUniqueTextType = TheTranslation

  titleValidations ++= List(ensureFieldIsUnique(TheTranslationMeta.title) _ )
}