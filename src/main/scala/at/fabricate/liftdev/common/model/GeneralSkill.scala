package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.IdPK
import net.liftweb.util.FieldError
import scala.collection.mutable
import at.fabricate.liftdev.common.lib.FieldValidation

trait GeneralSkillMeta[ModelType <: (GeneralSkill[ModelType]) ] extends BaseMetaEntity[ModelType] with BaseMetaEntityWithTitleAndDescription[ModelType] {
	self: ModelType => 
  
}
  

trait GeneralSkill[T <: GeneralSkill[T] ] extends BaseEntity[T] with BaseEntityWithTitleAndDescription[T] with IdPK with EnsureUniqueTextFields[T] {
  	self: T =>

	 override type TheUniqueTextType = TheTranslation

  titleValidations ++= List(ensureFieldIsUnique(TheTranslationMeta.title) _ )
  // redefine the validations

  // no minimumlenght for teaser, as it is not available on register!
  override val teaserValidations : mutable.MutableList[String => List[FieldError]] = mutable.MutableList( FieldValidation.maxLength(TheTranslationMeta.teaser,teaserLength) _ )

  // no minimumlenght for description, as it is not available on register!
  override val descriptionValidations : mutable.MutableList[String => List[FieldError]] = mutable.MutableList(FieldValidation.maxLength(TheTranslationMeta.description,descriptionLength) _ )

}