package at.fabricate
package model

import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.IdPK

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