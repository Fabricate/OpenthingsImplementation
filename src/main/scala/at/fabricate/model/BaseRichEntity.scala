package at.fabricate
package model

import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.IdPK
import scala.xml.Elem
import at.fabricate.lib.EnumWithDescriptionAndObject
import at.fabricate.lib.MappedEnumWithDescription

// this is the mapper type where every higher level object inherits from
// project and tutorial are examples for that

trait BaseRichEntity [T <: (BaseRichEntity[T] with LongKeyedMapper[T]) ] extends BaseIconEntity[T]
//with AddRepository[T]
with AddComment[T]
with IdPK{
  self: T =>
    
	
	// TODO:
	// add Licence Tag (one of many that are stored in a database)
    
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

	// TOD: Clean up that mess!
	// getItemsToSchemify comes from the AddComment trait
}

trait BaseRichEntityMeta[ModelType <: ( BaseRichEntity[ModelType] with LongKeyedMapper[ModelType]) ] extends BaseIconEntityMeta[ModelType]
//with AddRepositoryMeta[ModelType] 
with AddCommentMeta[ModelType] 
{
    self: ModelType  =>
      
}