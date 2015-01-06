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

trait BaseEntityWithTitleDescriptionIconAndCommonFields [T <: (BaseEntityWithTitleDescriptionIconAndCommonFields[T,U] with LongKeyedMapper[T]), U <: MegaProtoUser[U] ] extends BaseEntity[T]
with BaseEntityWithTitleDescriptionAndIcon[T]
//with AddRepository[T]
with AddComment[T]
with AddRating[T]
with AddCreatedByUser[T,U]
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
        object licenceEnum extends EnumWithDescriptionAndObject[Elem] {
      
      private def wrapLicenceLink(linkTarget : String, linkText : String, iconClasses : List[String]) : Elem = 
      <a href={linkTarget} target="_blank">{linkText} {iconClasses.map(iClass => iconClass(iClass)) }</a>
//      : _ *
//      List[Elem]
    
      private def iconClass(theClass : String) : Elem = <span class={theClass}></span>
//      <a href="https://creativecommons.org/licenses/by-nc/3.0/" target="_blank">Attribution 4.0 International <span class="icon-cc"></span> <span class="icon-cc-by"></span></a>
	val cc_by_nc_30 = Value("Creatice Commons 3.0 BY-NC",wrapLicenceLink(
	    "https://creativecommons.org/licenses/by-nc/3.0/",
	    "Attribution 3.0 International",
	    List("icon-cc","icon-cc-by")
	    ))
	 val cc_by_nc_40 = Value("Creatice Commons 4.0 BY-NC",wrapLicenceLink(
	    "https://creativecommons.org/licenses/by-nc/4.0/",
	    "Attribution 4.0 International",
	    List("icon-cc","icon-cc-by")
	    ))
	}
  
   /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object licence extends MappedEnumWithDescription[Elem,T](this,licenceEnum)
    
  	// TODO:
//  	// maybe add a completeness / development state field
//  		would apply to both, projects and tutorials
//  		 * concept
//  		 * early development state
//  		 * evolved (medium state)
//  		 * late development state
//  		 * mature
//  		 * concept
//  		 * early development state
//  		 * evolved (medium state)
//  		 * late development state
//  		 * mature
  
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

trait BaseMetaEntityWithTitleDescriptionIconAndCommonFields[ModelType <: ( BaseEntityWithTitleDescriptionIconAndCommonFields[ModelType, UserType] with LongKeyedMapper[ModelType]), UserType <: MegaProtoUser[UserType]  ] extends BaseMetaEntity[ModelType]
with BaseMetaEntityWithTitleDescriptionAndIcon[ModelType]
//with AddRepositoryMeta[ModelType] 
with AddCommentMeta[ModelType] 
with AddRatingMeta[ModelType] 
with AddCreatedByUserMeta[ModelType,UserType]
with AddTagsMeta[ModelType]
{
    self: ModelType  =>
      
}
