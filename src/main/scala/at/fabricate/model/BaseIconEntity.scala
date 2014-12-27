package at.fabricate
package model

import net.liftweb.mapper.LongKeyedMapper

// This is the basic mapper entity type
// especially for use in User (without IdPK)

trait BaseIconEntity [T <: (BaseIconEntity[T] with LongKeyedMapper[T]) ] extends BaseEntity[T] with AddIcon[T]
{
  self: T =>
    
	
	// TODO:
	// add Licence Tag (one of many that are stored in a database)
	// add a difficulty (one of many that comes from a list of string options)
	// add Tag, Tool, ...

	// TOD: Clean up that mess!
	// getItemsToSchemify comes from the AddComment trait
}

trait BaseIconEntityMeta[ModelType <: ( BaseIconEntity[ModelType] with LongKeyedMapper[ModelType]) ] extends BaseEntityMeta[ModelType] with AddIconMeta[ModelType]
{
    self: ModelType  =>
      
}