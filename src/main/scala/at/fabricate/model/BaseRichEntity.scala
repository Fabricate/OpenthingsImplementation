package at.fabricate
package model

import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedTextarea
import net.liftweb.mapper.LongKeyedMetaMapper

trait BaseRichEntity [T <: (BaseRichEntity[T] with LongKeyedMapper[T]) ] extends LongKeyedMapper[T] with MatchByID[T] with AddIcon[T] with AddRepository[T] with
AddComment[T] with IdPK{
  self: T =>
    
	object title extends MappedString(this, 30)
	object teaser extends MappedString(this, 150)
	object description extends MappedTextarea(this, 2000)
	
	// TODO:
	// add Licence Tag (one of many that are stored in a database)
	// add a difficulty (one of many that comes from a list of string options)
	// add Tag, Tool, ...

	// TOD: Clean up that mess!
	// FindByID comes from the AddIcon trait, 
	// getItemsToSchemify comes from the AddComment trait
}

trait BaseRichEntityMeta[ModelType <: ( BaseRichEntity[ModelType] with LongKeyedMapper[ModelType]) ] extends LongKeyedMetaMapper[ModelType] with AddIconMeta[ModelType] with AddRepositoryMeta[ModelType] with
AddCommentMeta[ModelType] { //
//  with MatchByIDMeta[ModelType]
    self: ModelType  =>
      
}