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
      
//        object createdByUser extends MappedLongForeignKey(this, theUserObject){
//
//    override def defaultValue = theUserObject.currentUser.map(_.primaryKeyField.get ) openOr(-1)
//    
//	  /**Genutzter Spaltenname in der DB-Tabelle*/
//    override def dbColumnName = "initiator"
//    
//    /**Name des Datenfeldes für CRUD-Seiten*/
////    override def displayName = S.?("project\u0020initiator")
//    
////    override def validations = FieldValidation.notEmpty(this) :: Nil
//    
//      
//    /**Darstellung des Feldes auf CRUD-  object createdByUser extends MappedManyToMany(self,){
//    
//  }Seiten. Anstelle der Id wird Nachname und Vorname des Autors
//     * angezeigt bzw. "k.A." für "keine Angabe", wenn es zu dieser User-Id keinen User gibt. */
////    override def asHtml = User.getLinkToUser(get)
//    
//  }
	        
	  object categories extends MappedOneToMany(TheCategories, TheCategories.categorizedItem, OrderBy(TheCategories.primaryKeyField, Ascending))  with Owned[TheCategories]
with Cascade[TheCategories]
//	{
////	    this.
//	    override def defaultValue = theUserObject.currentUser.map(_.primaryKeyField.get ) openOr(-1)
//	    override def dbColumnName = "initiator"
////	    override def validations = FieldValidation.notEmpty(this) :: Nil
//	  }

	  //def getItemsToSchemify = List(TheComment, T)
      
      class TheCategories extends LongKeyedMapper[TheCategories] with IdPK {
    	  def getSingleton = TheCategories
	    	  
	      object categorizedItem extends MappedLongForeignKey(this,self.getSingleton)
    	  object theCategory extends MappedLongForeignKey(this,theCategoryObject)
		  
	}
	
	object TheCategories  extends TheCategories with LongKeyedMetaMapper[TheCategories]{
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

//  // a link to all the created Projects
//  val mappingToProjects : LongKeyedMetaMapper[Project.TheMapping] = Project.getUserMapper
//  
//  object createdProjects extends MappedManyToMany(mappingToProjects, mappingToProjects.byUser, mappingToProjects.createdItem, Project)   with Owned[mappingToProjects]
//with Cascade[mappingToProjects]
