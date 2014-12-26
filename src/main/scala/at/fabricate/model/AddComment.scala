package at.fabricate
package model

import net.liftweb.mapper.KeyedMapper
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.KeyedMetaMapper
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedLongForeignKey
import net.liftweb.mapper.Mapper
import net.liftweb.mapper.OneToMany
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.Ascending
import net.liftweb.mapper.Schemifier
import net.liftweb.mapper.MappedLong
import net.liftweb.mapper.MetaMapper
import net.liftweb.mapper.BaseLongKeyedMapper
import net.liftweb.mapper.MappedForeignKey

trait AddComment[T <: (AddComment[T] with LongKeyedMapper[T]) ] extends KeyedMapper[Long, T]  with OneToMany[Long, T] { // 
	self: T =>


      type TheCommentedType = T
	  
	  
      val commentGenerator = new GenerateNewComment[TheCommentedType](self.getSingleton)

      
      
      type TheComment = commentGenerator.TheHiddenComment
        
      val TheComment = commentGenerator.getCommentObject
      
      
	  object comments extends MappedOneToMany(TheComment, TheComment.commentedItem, OrderBy(TheComment.primaryKeyField, Ascending))

	  //def getItemsToSchemify = List(TheComment, self)


      
}

trait AddCommentMeta[ModelType <: (AddComment[ModelType]  with LongKeyedMapper[ModelType]) ] extends KeyedMetaMapper[Long, ModelType] {
	self: ModelType =>
	  override type TheComment = self.commentGenerator.TheHiddenComment
//	  val TheComment = self.theComment 
}




class GenerateNewComment[U <: (AddComment[U] with LongKeyedMapper[U]) ](commentedMapper : KeyedMetaMapper[Long,U]) {
  
      class TheHiddenComment extends LongKeyedMapper[TheHiddenComment] with IdPK { // with CommentInterface[TheHiddenComment]
    	  def getSingleton = TheHiddenCommentMeta
//	  	  .asInstanceOf[KeyedMetaMapper[Long,CommentInterface[U]]
	    	  
	      object commentedItem extends MappedLongForeignKey(this,commentedMapper)
	  	  object title extends MappedString(this, 40)
		  object author extends MappedString(this, 40)
		  object comment extends MappedString(this, 140)
		  
		 // Bugfix for the compilation issue
		 // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
		 def dbDefaultConnectionIdentifier = commentedMapper.dbDefaultConnectionIdentifier	
	}
	
	object TheHiddenCommentMeta  extends TheHiddenComment with LongKeyedMetaMapper[TheHiddenComment]{
	  	  override def dbTableName =  commentedMapper.dbTableName+"_comments"
	  	  
	  	  // Bugfix for the compilation issue
	  	  // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	  	  override def dbDefaultConnectionIdentifier = commentedMapper.dbDefaultConnectionIdentifier	  	  
	  	  override def createInstance = new TheHiddenComment

	}
	
	def getCommentObject  = TheHiddenCommentMeta
//	.asInstanceOf[CommentInterface[U]]
	//: CommentInterface[U]
	
//	.asInstanceOf[LongKeyedMetaMapper[CommentInterface[T]]]
}

