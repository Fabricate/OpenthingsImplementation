package at.fabricate
package model

import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.MappedEnum

import at.fabricate.lib.EnumWithDescriptionAndObject

// this is the mapper type where every higher level object inherits from
// project and tutorial are examples for that

trait BaseRichEntity [T <: (BaseRichEntity[T] with LongKeyedMapper[T]) ] extends BaseIconEntity[T]
//with AddRepository[T]
with AddComment[T]
with IdPK{
  self: T =>
    
	
	// TODO:
	// add Licence Tag (one of many that are stored in a database)
	// add a difficulty (one of many that comes from a list of string options)
    object difficultyEnum extends EnumWithDescriptionAndObject[String] {
	val kids = Value("Kids","icon-difficulty1")
	val starter = Value("Starter","icon-difficulty2")
	val average = Value("Average","icon-difficulty3")
	val advanced = Value("Advanced","icon-difficulty4")
	val expert = Value("Expert","icon-difficulty5")
	val genius = Value("Genius","icon-difficulty6")
	}
  
   /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object difficulty extends MappedEnum(this, difficultyEnum  ){
    private def findEnumValue(text : String ) : difficultyEnum.ExtendedValue = difficultyEnum.valueOf(text).getOrElse(difficultyEnum.Value("Not Found","icon-difficulty1"))
    private def getWrapped = findEnumValue(get.toString).wrapped
    private def getDescription = findEnumValue(get.toString).description
    override def asHtml = <span class={getWrapped}></span>
    override def buildDisplayList: List[(Int, String)] = enum.values.toList.map(a => (a.id, findEnumValue(a.toString).description))
  }
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