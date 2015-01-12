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
with AddCategories[T]
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
    
  
   /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object state extends MappedEnumWithDescription[Elem,T](this,StateEnum)
  
  
   /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object difficulty extends MappedEnumWithDescription[Elem,T](this,DifficultyEnum)
  

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
with AddCategoriesMeta[ModelType]
{
    self: ModelType  =>
      
}
