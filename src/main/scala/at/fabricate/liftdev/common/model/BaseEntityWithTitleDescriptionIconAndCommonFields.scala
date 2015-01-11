package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.IdPK
import scala.xml.Elem
import lib.EnumWithDescriptionAndObject
import lib.MappedEnumWithDescription
import net.liftweb.mapper.BaseLongKeyedMapper
import net.liftweb.mapper.KeyedMapper
import net.liftweb.mapper.ProtoUser
import net.liftweb.mapper.MappedLongForeignKey
import net.liftweb.mapper.MegaProtoUser
import net.liftweb.mapper.MetaMegaProtoUser

// this is the mapper type where every higher level object inherits from
// project and tutorial are examples for that

trait BaseEntityWithTitleDescriptionIconAndCommonFields [T <: (BaseEntityWithTitleDescriptionIconAndCommonFields[T] with LongKeyedMapper[T])] extends BaseEntity[T]
with BaseEntityWithTitleDescriptionAndIcon[T]
//with AddRepository[T]
with AddComment[T]
with AddRating[T]
with AddCreatedByUser[T]
with AddTags[T]
with IdPK
with EqualityByID[T] 
{
  self: T =>
    
	// TODO:
	// add Licence Tag (one of many that are stored in a database)
    // Generalize generation of Licences, (
//    	CC: version+{BY-NC-SA}^2
//    	Public Domain
//    	MIT
//    	Gnu
//    	Berkley
//    	...
//    )

  
   /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object licence extends MappedEnumWithDescription[Elem,T](this,LicenceEnum)
    
  
	// add a difficulty (one of many that comes from a list of string options)
    object stateEnum extends EnumWithDescriptionAndObject[Elem] {
      
      private def wrapSpanWithClass(theClass : String) : Elem = <span class={theClass}></span>
    
	val concept = Value("concept / idea",wrapSpanWithClass("icon-state1"))
	val early_dev = Value("early development state",wrapSpanWithClass("icon-state2"))
	val evolved = Value("evolved (medium state)",wrapSpanWithClass("icon-state3"))
	val late_dev = Value("late development state",wrapSpanWithClass("icon-state4"))
	val mature = Value("mature",wrapSpanWithClass("icon-state5"))
	}
  
   /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object state extends MappedEnumWithDescription[Elem,T](this,stateEnum)
  
  
	// add a difficulty (one of many that comes from a list of string options)
    object difficultyEnum extends EnumWithDescriptionAndObject[Elem] {
      
      private def wrapSpanWithClass(theClass : String) : Elem = <span class={theClass}></span>
    
	val kids = Value("Kids",wrapSpanWithClass("icon-difficulty1"))
	val starter = Value("Starter",wrapSpanWithClass("icon-difficulty2"))
	val average = Value("Average",wrapSpanWithClass("icon-difficulty3"))
	val advanced = Value("Advanced",wrapSpanWithClass("icon-difficulty4"))
	val expert = Value("Expert",wrapSpanWithClass("icon-difficulty5"))
	val genius = Value("Genius",wrapSpanWithClass("icon-difficulty6"))
	}
  
   /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object difficulty extends MappedEnumWithDescription[Elem,T](this,difficultyEnum)
  

	// add Tag, Tool, ...
    // add field createdby, maybe (without display) also interesting for other types
  
}

trait BaseMetaEntityWithTitleDescriptionIconAndCommonFields[ModelType <: ( BaseEntityWithTitleDescriptionIconAndCommonFields[ModelType] with LongKeyedMapper[ModelType])] extends BaseMetaEntity[ModelType]
with BaseMetaEntityWithTitleDescriptionAndIcon[ModelType]
//with AddRepositoryMeta[ModelType] 
with AddCommentMeta[ModelType] 
with AddRatingMeta[ModelType] 
with AddCreatedByUserMeta[ModelType]
with AddTagsMeta[ModelType]
{
    self: ModelType  =>
      
}
