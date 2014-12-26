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

trait AddComment[T <: (AddComment[T] with LongKeyedMapper[T]) ] extends KeyedMapper[Long, T]  with OneToMany[Long, T] { // 
	self: T =>

	  object comments extends MappedOneToMany(TheComment, TheComment.commentedItem, OrderBy(TheComment.id, Ascending))

	  def getItemsToSchemify = List(TheComment, self)

      
      class TheComment extends LongKeyedMapper[TheComment] with IdPK{
	  
	  def getSingleton = TheComment
	  
	 // Bugfix for the compilation issue
	 // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	 override def dbDefaultConnectionIdentifier = self.getSingleton.dbDefaultConnectionIdentifier	
	
	
	  object commentedItem extends MappedLongForeignKey(this, self.getSingleton)
	  object title extends MappedString(this, 40)
	  object author extends MappedString(this, 40)
	  object comment extends MappedString(this, 140)
	}
	
	object TheComment  extends TheComment with LongKeyedMetaMapper[TheComment]{
	  
	  	  override def dbTableName =  self.getSingleton.dbTableName+"_comments"
	  	  
	  	  // Bugfix for the compilation issue
	  	  // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	  	  override def dbDefaultConnectionIdentifier = self.getSingleton.dbDefaultConnectionIdentifier	  	  
	  	  override def createInstance = new TheComment

	}

      
}

trait AddCommentMeta[ModelType <: ( AddComment[ModelType] with LongKeyedMapper[ModelType]) ] extends KeyedMetaMapper[Long, ModelType] { //
    self: ModelType  =>

      type TheCommentType = ModelType
      

}

