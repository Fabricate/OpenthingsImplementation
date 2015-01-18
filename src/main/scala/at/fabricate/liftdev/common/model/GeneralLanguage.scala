package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.IdPK
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedStringIndex

trait GeneralLanguageMeta[ModelType <: (GeneralLanguage[ModelType]) ] extends KeyedMetaMapper[String,ModelType] {
	self: ModelType => 
  
}
  

trait GeneralLanguage[T <: GeneralLanguage[T] ] extends KeyedMapper[String,T] with EnsureUniqueTextFields[T] {
  	self: T =>
  	  
    object langCode extends MappedStringIndex(this, 6)
    object languageNameInEnglish extends MappedString(this, 500 )
    object languageNameInTheLanguage extends MappedString(this, 500 )
    object languageNameInternational extends MappedString(this, 500 )
    
    override def primaryKeyField = langCode

	override def theUniqueFields = List(langCode,languageNameInEnglish)
}