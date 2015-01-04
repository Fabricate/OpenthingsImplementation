package at.fabricate.liftdev.common
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
import net.liftweb.mapper.BaseMetaMapper
import net.liftweb.mapper.ProtoUser
import net.liftweb.common.Box

trait AddComment[T <: (AddComment[T]) ] extends BaseEntity[T]  with OneToMany[Long, T] { // 
	self: T =>
	  
	  def getCurrentUser : Box[ProtoUser[_]]
	  
      type TheCommentedType = T
      
      def getCommentMapper : LongKeyedMetaMapper[_] = TheComment
	        
	  object comments extends MappedOneToMany(TheComment, TheComment.commentedItem, OrderBy(TheComment.primaryKeyField, Ascending))

	  //def getItemsToSchemify = List(TheComment, T)
      
      class TheComment extends LongKeyedMapper[TheComment] with IdPK {
    	  def getSingleton = TheComment
	    	  
	      object commentedItem extends MappedLongForeignKey(this,self.getSingleton)
	  	  object title extends MappedString(this, 80){    	    
    	    override def validations = FieldValidation.minLength(this,5) _ :: Nil
    	  }
		  object author extends MappedString(this, 40){
    	    override def validations = FieldValidation.minLength(this,3) _ :: Nil
    	    override def defaultValue = getCurrentUser.map(user => "%s %s".format(user.firstName, user.lastName )) openOr("")
    	  }
		  object comment extends MappedString(this, 140){		    
    	    override def validations = FieldValidation.minLength(this,10) _ :: Nil
		  }
		  
	}
	
	object TheComment  extends TheComment with LongKeyedMetaMapper[TheComment]{
	  	  override def dbTableName =  self.getSingleton.dbTableName+"_comments"
	  	  
	  	  // Bugfix for the compilation issue
	  	  // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	  	  override def createInstance = new TheComment
	  	  
	  	  // Hint by David Pollak on https://groups.google.com/forum/#!topic/liftweb/Rkz06yng-P8
	  	  override def getSingleton = this
	}

      
}

trait AddCommentMeta[ModelType <: (AddComment[ModelType]) ] extends BaseMetaEntity[ModelType] {
	self: ModelType =>
	  	  abstract override def getItemsToSchemify : List[BaseMetaMapper] =  getCommentMapper :: super.getItemsToSchemify

}
