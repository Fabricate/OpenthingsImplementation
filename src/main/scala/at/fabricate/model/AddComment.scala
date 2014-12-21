package at.fabricate
package model

import net.liftweb.mapper.KeyedMapper
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.KeyedMetaMapper

trait AddComment[T <: (AddComment[T] with LongKeyedMapper[T]) ] extends KeyedMapper[Long, T]  { // 
	self: T =>

}

trait AddCommentMeta[ModelType <: ( AddComment[ModelType] with LongKeyedMapper[ModelType]) ] extends KeyedMetaMapper[Long, ModelType] { //
    self: ModelType  =>

      type TheCommentType = ModelType
      
//          object comments extends MappedOneToMany(Comment, Comment.post, OrderBy(Comment.id, Ascending))

      
}