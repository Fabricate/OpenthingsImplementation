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
import net.liftweb.mapper.MegaProtoUser
import net.liftweb.mapper.MetaMegaProtoUser

trait AddCategories[T <: (AddCategories[T])] extends BaseEntity[T]  with OneToMany[Long, T] { // 
	self: T =>
	  	  
	  type TheCategoryType <: GeneralCategory[TheCategoryType]
	  
	  def theCategoryObject : GeneralCategoryMeta[TheCategoryType] //with Tags[Z]
	        
      def getCategoryMapper : LongKeyedMetaMapper[TheCategories] = TheCategories
      

	        
	  object categories extends MappedOneToMany(TheCategories, TheCategories.categorizedItem, OrderBy(TheCategories.primaryKeyField, Ascending))  with Owned[TheCategories]
with Cascade[TheCategories]

      
      class TheCategories extends BaseEntity[TheCategories] with LongKeyedMapper[TheCategories] with IdPK {
    	  def getSingleton = TheCategories
	    	  
	      object categorizedItem extends MappedLongForeignKey(this,self.getSingleton)
    	  object theCategory extends MappedLongForeignKey(this,theCategoryObject)
		  
	}
	
	object TheCategories  extends TheCategories with BaseMetaEntity[TheCategories] with LongKeyedMetaMapper[TheCategories]{
	  	  override def dbTableName =  self.getSingleton.dbTableName+"_category"
	  	  
	  	  // Bugfix for the compilation issue
	  	  // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	  	  override def createInstance = new TheCategories
	  	  
	  	  // Hint by David Pollak on https://groups.google.com/forum/#!topic/liftweb/Rkz06yng-P8
	  	  override def getSingleton = this
	}

      
}

trait AddCategoriesMeta[ModelType <: (AddCategories[ModelType]) ] extends BaseMetaEntity[ModelType] {
	self: ModelType =>
	  	  abstract override def getItemsToSchemify : List[BaseMetaMapper] =  getCategoryMapper :: super.getItemsToSchemify

}
