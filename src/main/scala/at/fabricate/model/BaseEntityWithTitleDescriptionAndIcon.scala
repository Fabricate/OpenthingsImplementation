package at.fabricate
package model

import net.liftweb.mapper.LongKeyedMapper

// This is the basic mapper entity type
// especially for use in User (without IdPK)

trait BaseEntityWithTitleDescriptionAndIcon [T <: (BaseEntityWithTitleDescriptionAndIcon[T]) ] extends BaseEntityWithTitleAndDescription[T] with AddIcon[T]
{
  self: T =>
    
	
	// TODO:
	// add Licence Tag (one of many that are stored in a database)
	// add a difficulty (one of many that comes from a list of string options)
	// add Tag, Tool, ...

	// TOD: Clean up that mess!
	// getItemsToSchemify comes from the AddComment trait
}

trait BaseMetaEntityWithTitleDescriptionAndIcon[ModelType <: ( BaseEntityWithTitleDescriptionAndIcon[ModelType]) ] extends BaseMetaEntityWithTitleAndDescription[ModelType] with AddIconMeta[ModelType]
{
    self: ModelType  =>
      
}