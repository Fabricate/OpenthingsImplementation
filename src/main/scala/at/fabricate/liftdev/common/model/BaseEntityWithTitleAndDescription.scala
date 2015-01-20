package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedTextarea
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.CreatedUpdated

// This is the basic database entity 
// every db object should inherit from that
// specially needed for tags and skills that dont have an icon
// TODO: maybe even tags and skills want to have an icon?

trait BaseEntityWithTitleAndDescription [T <: (BaseEntityWithTitleAndDescription[T]) ] extends BaseEntity[T] with MatchByID[T] with CreatedUpdated
{
  self: T =>
    
	object title extends MappedString(this, 30)
	object teaser extends MappedTextarea(this, 150)
	object description extends MappedTextarea(this, 2000)
	
	// TODO:
	// add Licence Tag (one of many that are stored in a database)
	// add a difficulty (one of many that comes from a list of string options)
	// add Tag, Tool, ...

	// TOD: Clean up that mess!
	// getItemsToSchemify comes from the AddComment trait
}

trait BaseMetaEntityWithTitleAndDescription[ModelType <: ( BaseEntityWithTitleAndDescription[ModelType]) ] extends BaseMetaEntity[ModelType] with CreatedUpdated
{
    self: ModelType  =>
      
}
