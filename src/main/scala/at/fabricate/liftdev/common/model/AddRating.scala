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
import net.liftweb.mapper.MappedInt

trait AddRating[T <: (AddRating[T]) ] extends BaseEntity[T]  with OneToMany[Long, T] { // 
	self: T =>
	  
	  def getCurrentUser : Box[ProtoUser[_]]
	  
      type TheRatedType = T
      
      def getRatingMapper : LongKeyedMetaMapper[_] = TheRating
	        
	  object ratings extends MappedOneToMany(TheRating, TheRating.ratedItem, OrderBy(TheRating.primaryKeyField, Ascending))  with Owned[TheRating]
with Cascade[TheRating]

	  //def getItemsToSchemify = List(TheComment, T)
      
      class TheRating extends LongKeyedMapper[TheRating] with IdPK {
    	  def getSingleton = TheRating
	    	  
	      object ratedItem extends MappedLongForeignKey(this,self.getSingleton)

		  object author extends MappedString(this, 40){
//    	    override def validations = FieldValidation.minLength(this,3) _ :: Nil
    	    override def defaultValue = getCurrentUser.map(user => "%s %s".format(user.firstName, user.lastName )) openOr("")
    	  }
		  object rating extends MappedInt(this){		    
//    	    override def validations = FieldValidation.minLength(this,10) _ :: Nil
		  }
		  
	}
	
	object TheRating  extends TheRating with LongKeyedMetaMapper[TheRating]{
	  	  override def dbTableName =  self.getSingleton.dbTableName+"_ratings"
	  	  
	  	  // Bugfix for the compilation issue
	  	  // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	  	  override def createInstance = new TheRating
	  	  
	  	  // Hint by David Pollak on https://groups.google.com/forum/#!topic/liftweb/Rkz06yng-P8
	  	  override def getSingleton = this
	}

      
}

trait AddRatingMeta[ModelType <: (AddRating[ModelType]) ] extends BaseMetaEntity[ModelType] {
	self: ModelType =>
	  	  abstract override def getItemsToSchemify : List[BaseMetaMapper] =  getRatingMapper :: super.getItemsToSchemify

}
