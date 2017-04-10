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
import net.liftweb.mapper.MetaMegaProtoUser
import net.liftweb.mapper.MegaProtoUser
import net.liftweb.mapper.CreatedUpdated
import at.fabricate.liftdev.common.lib.FieldValidation

trait AddComment[T <: (AddComment[T]) ] extends BaseEntity[T]  with OneToMany[Long, T] { // 
	self: T =>
	  
	  type TheUserType <: MegaProtoUser[TheUserType] with BaseEntityWithTitleAndDescription[TheUserType]
	  
	  def theUserObject : MetaMegaProtoUser[TheUserType] with BaseMetaEntityWithTitleAndDescription[TheUserType]

	  def getCurrentUser : Box[ProtoUser[TheUserType]]
	  
      type TheCommentedType = T
      
      def getCommentMapper : LongKeyedMetaMapper[_] = TheComment
	        
	  object comments extends MappedOneToMany(TheComment, TheComment.commentedItem, OrderBy(TheComment.primaryKeyField, Ascending)) with Owned[TheComment]
with Cascade[TheComment]

      
      class TheComment extends LongKeyedMapper[TheComment] with IdPK { // TODO:  with CreatedUpdated
    	  def getSingleton = TheComment
	    	  
	      object commentedItem extends MappedLongForeignKey(this,self.getSingleton)
	  	  object title extends MappedString(this, 80){    	    
    	    override def validations = FieldValidation.minLength(this,5) _ :: Nil
    	  }

    	  object author extends MappedLongForeignKey(this,theUserObject){
    	    override def defaultValue = getCurrentUser.map(_.id.get).openOr(-1)
    	  }
    	  
		  object comment extends MappedString(this, 140){		    
    	    override def validations = FieldValidation.minLength(this,10) _ :: Nil
		  }
		  
	}
	
	object TheComment  extends TheComment with LongKeyedMetaMapper[TheComment]{
	  	  override def dbTableName =  self.getSingleton.dbTableName+"_comment"
	  	  
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
