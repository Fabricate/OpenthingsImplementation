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
import net.liftweb.common.Empty
import java.util.Locale
import lib._

trait AddImages[T <: (AddImages[T])] extends BaseEntity[T]  with OneToMany[Long, T] { //
	self: T =>
	  	  
	  type TheImageType <: GeneralImage[TheImageType]
	  
	  type TheImageTranslation = TheGenericTranslation
	  
	  def theImageObject : GeneralImageMeta[TheImageType]
	        
      def getImageMapper : LongKeyedMetaMapper[TheImage] = TheImage
      
      
	def getSingleton : AddImagesMeta[T]
	  
	  


	        
	  object images extends MappedOneToMany(TheImage, TheImage.taggedItem, OrderBy(TheImage.primaryKeyField, Ascending))  with Owned[TheImage]
with Cascade[TheImage]

      
      class TheImage extends LongKeyedMapper[TheImage] with IdPK {
    	  def getSingleton = TheImage
	    	  
	      object taggedItem extends MappedLongForeignKey(this,self.getSingleton)
    	  object image extends MappedLongForeignKey(this,theImageObject)
		  
	}
	
	object TheImage  extends TheImage with LongKeyedMetaMapper[TheImage]{
	  	  override def dbTableName =  self.getSingleton.dbTableName+"_image"
	  	  
	  	  // Bugfix for the compilation issue
	  	  // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	  	  override def createInstance = new TheImage
	  	  
	  	  // Hint by David Pollak on https://groups.google.com/forum/#!topic/liftweb/Rkz06yng-P8
	  	  override def getSingleton = this
	}

      // some helpers for images
	  def getAllAvailableImages : List[TheImageType] = theImageObject.findAll
	  
	  def getAllImagesForThisItem : List[TheImageType] = this.images.map(
	      // load the connected image object
	      _.image.obj).filter(
	          // filter out all empty boxes
	          _ != Empty).map(
	              // open the boxes now that only full boxes are available (hopefully now failure)
	              imageBox => imageBox.openOrThrowException("Opened empty Box")).distinct.
	              // convert to a list again
	              toList 
	              
	  def addNewImageToItem(language: Locale, imageTitle:String, imageDescription:String = null, imageTeaser : String=null) : TheImageType = {
	    val newImage= theImageObject.createNewEntity(language, title=imageTitle, teaser=imageTeaser,description=imageDescription).saveMe()
          
          // create a link between the item and the new tag
          TheImage.create.taggedItem(this).image(newImage).saveMe
	    newImage
	  }


}

trait AddImagesMeta[ModelType <: (AddImages[ModelType]) ] extends BaseMetaEntity[ModelType] {
	self: ModelType =>
	  	  abstract override def getItemsToSchemify : List[BaseMetaMapper] =  getImageMapper :: super.getItemsToSchemify
	  	  
}
