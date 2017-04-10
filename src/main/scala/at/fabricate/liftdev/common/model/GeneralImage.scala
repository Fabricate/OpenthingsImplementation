package at.fabricate.liftdev.common
package model

import net.liftweb.mapper.IdPK
import net.liftweb.util.FieldError
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.OkResponse
import net.liftweb.http.BadResponse
import net.liftweb._
import net.liftweb.mapper._
import net.liftweb.http._
import net.liftweb.http.LiftRules
import net.liftweb.common._
import net.liftmodules.imaging._
import net.liftweb.util.BasicTypesHelpers._
import net.liftweb.util._
import java.awt.image.BufferedImage
import lib.MatchString
import scala.collection.mutable
import at.fabricate.liftdev.common.lib.ImageHelper
import java.util.Locale
import at.fabricate.liftdev.common.lib.FieldValidation

trait GeneralImageMeta[ModelType <: (GeneralImage[ModelType]) ] extends BaseMetaEntity[ModelType] with BaseMetaEntityWithTitleAndDescription[ModelType] {
	self: ModelType => 
	  
	  abstract override def getItemsToSchemify : List[BaseMetaMapper] =  TheImageData :: super.getItemsToSchemify
	  
	        // defines the path for uploading files to the rest api
      // example path : URL_TO_THE_SERVER/api/upload/image    
      def apiPath : String = "api"
        
      def uploadPath : String = "upload"
        
      def imagePath : String = "image"      
      
        def baseServingPath = "serve"      
      
        def qualityPathThumb = "thumb"    
      
        def qualityPathOrig = "orig"
        

  def jpegImageQuality : Float = 95 / 100.toFloat
        
          object MatchServePath extends MatchString(baseServingPath)
      
      object MatchImagePath extends MatchString(imagePath)
      
      object MatchQualityPathThumb extends MatchString(qualityPathThumb)
      
      object MatchQualityPathOrig extends MatchString(qualityPathOrig)
	  
	    abstract override def init : Unit = {


        // Dispatch to Serve the Images
    LiftRules.dispatch.append({
      case r @ Req(MatchServePath(servePath) :: MatchImagePath(imgPath)  :: MatchItemByID(imageID) :: 
                 Nil, _, GetRequest) => ()  =>  {
                   if(imageID.resizedImage.isDefined && imageID.resizedImage.obj.isDefined){
                     val imageData = imageID.resizedImage.obj.openOrThrowException("Opened empty Box")
                     Full(InMemoryResponse(imageData.image.get,
                               List("Content-Type" -> imageData.imageFormat.toString(),
                                    "Content-Length" ->
                                    imageData.image.get.length.toString), Nil, 200))
                                    
                   } else {
                     Full(InMemoryResponse(new Array[Byte](0),Nil, Nil, 404))
                   }
                 }
    })
    
    LiftRules.dispatch.append({
      case r @ Req(MatchServePath(servePath) :: MatchImagePath(imgPath)  :: MatchItemByID(imageID) :: MatchQualityPathThumb(thumbPath) ::
                 Nil, _, GetRequest) => ()  =>  {
                   if(imageID.thumbnailImage.isDefined && imageID.thumbnailImage.obj.isDefined){
                     val imageData = imageID.thumbnailImage.obj.openOrThrowException("Opened empty Box")
                     Full(InMemoryResponse(imageData.image.get,
                               List("Content-Type" -> imageData.imageFormat.get,
                                    "Content-Length" ->
                                    imageData.image.get.length.toString), Nil, 200))
                                    
                   } else {
                     Full(InMemoryResponse(new Array[Byte](0),Nil, Nil, 404))
                   }
                 }
    })  
    
        LiftRules.dispatch.append({
      case r @ Req(MatchServePath(servePath) :: MatchImagePath(imgPath)  :: MatchItemByID(imageID) :: MatchQualityPathOrig(origPath) ::
                 Nil, _, GetRequest) => ()  =>  {
                   if(imageID.originalImage.isDefined && imageID.originalImage.obj.isDefined){
                     val imageData = imageID.originalImage.obj.openOrThrowException("Opened empty Box")
                     Full(InMemoryResponse(imageData.image.get,
                               List("Content-Type" -> imageData.imageFormat.get,
                                    "Content-Length" ->
                                    imageData.image.get.length.toString), Nil, 200))
                                    
                   } else {
                     Full(InMemoryResponse(new Array[Byte](0),Nil, Nil, 404))
                   }
                 }
    })  
        // REST to Upload Images
        object ImageUploadREST extends RestHelper {

		  serve ( apiPath / uploadPath / imagePath prefix {
		//MatchItemByID(item) ::
		    case MatchItemByID(item) :: Nil Post req =>
		    //case 
		      	      /**/
		      for (fileHolder <- req.uploadedFiles) {
		        println("Received: "+fileHolder.fileName)
		        if (ImageHelper.containsImage(Full(fileHolder))){
		          //val id = 
		            setImageFromFileHolder(fileHolder, item)
		          //println("image id was "+id);
		          OkResponse()
		        }
		        else
		        {		          
		          println("sorry  - no image type: "+fileHolder.mimeType.toString());
		          S.error("Please provide an image!")		          
		      BadResponse()
		        }
		      }          
		          //println("sorry  - no files ");
		       BadResponse()
		      
		    case any :: Nil Post req => {
		      println("upload file :: id is not matching a item")
		      println(any)
		      BadResponse()
		    }
		    
		  })
		
		}
        LiftRules.dispatch.append(ImageUploadREST)
        println("imageupload rest added");
        super.init
      }
  def setImageFromFileHolder(imageUpload:FileParamHolder, theItem : GeneralImage[ModelType]) = {
  
  
  val inputStream = imageUpload.fileStream
	var metaImage = ImageResizer.getImageFromStream(inputStream)	
	theItem.imageFileName(imageUpload.fileName)
	
	// save the original image
	var originalImageData = if (theItem.originalImage.isDefined && theItem.originalImage.obj.isDefined){
	  theItem.originalImage.obj.openOrThrowException("Opened empty Box")
	} else {
	  theItem.TheImageData.create
	}
	var thumbImageData = if (theItem.thumbnailImage.isDefined && theItem.thumbnailImage.obj.isDefined){
	  theItem.thumbnailImage.obj.openOrThrowException("Opened empty Box")
	} else {
	  theItem.TheImageData.create
	}
	var resizedImageData = if (theItem.resizedImage.isDefined && theItem.resizedImage.obj.isDefined){
	  theItem.resizedImage.obj.openOrThrowException("Opened empty Box")
	} else {
	  theItem.TheImageData.create
	}
	
  originalImageData.image(imageUpload.file)
  originalImageData.imageFormat(imageUpload.mimeType)
  originalImageData.imageHeight(metaImage.image.getHeight)
  originalImageData.imageWidth(metaImage.image.getWidth)
  originalImageData.save()
	theItem.originalImage(originalImageData)
	
	// create the thumb image
	var resizedImageWithMeta = 
	  if (thumbApplyImageCropping)
	    ImageHelper.crop(metaImage, thumbWidth, thumbHeight, thumbAlignCroppedImage)
	  else
	    ImageHelper.resize(metaImage, thumbWidth, thumbHeight)
	// save the resized image
  thumbImageData.image(ImageHelper.imageToJPEGByteArray(resizedImageWithMeta, thumbJpegImageQuality))
  thumbImageData.imageFormat(ImageHelper.jpegMIMEType)
  thumbImageData.imageHeight(resizedImageWithMeta.image.getHeight)
  thumbImageData.imageWidth(resizedImageWithMeta.image.getWidth)
  thumbImageData.save()
	theItem.thumbnailImage(thumbImageData)
	
		// create the resized image
	resizedImageWithMeta = 
	  if (resizedApplyImageCropping)
	    ImageHelper.crop(metaImage, resizedWidth, resizedHeight, resizedAlignCroppedImage)
	  else
	    ImageHelper.resize(metaImage, resizedWidth, resizedHeight)
	// save the resized image
  resizedImageData.image(ImageHelper.imageToJPEGByteArray(resizedImageWithMeta, thumbJpegImageQuality))
  resizedImageData.imageFormat(ImageHelper.jpegMIMEType)
  resizedImageData.imageHeight(resizedImageWithMeta.image.getHeight)
  resizedImageData.imageWidth(resizedImageWithMeta.image.getWidth)
  resizedImageData.save()
	theItem.resizedImage(resizedImageData)

  
	theItem.save()
}
}
  

trait GeneralImage[T <: GeneralImage[T] ] extends BaseEntity[T] with BaseEntityWithTitleAndDescription[T] with IdPK  {
  	self: T =>
  	  
  def thumbWidth = 800
  def thumbHeight = 800
  def thumbApplyImageCropping = false  
  def thumbAlignCroppedImage = ImageHelper.ALIGN_CENTER
  def thumbJpegImageQuality : Float = 95 / 100.toFloat
  
  def resizedWidth = 1600
  def resizedHeight = 1600
  def resizedApplyImageCropping = false  
  def resizedAlignCroppedImage = ImageHelper.ALIGN_CENTER
  def resizedJpegImageQuality : Float = 90 / 100.toFloat
  	  

	 
	 	  object originalImage extends MappedLongForeignKey(this,TheImageData) //TheImageDataMeta, TheImageDataMeta.translatedItem) with Owned[TheImageData] with Cascade[TheImageData]
	
    	 object resizedImage extends MappedLongForeignKey(this,TheImageData)
	 	  
	 	  object thumbnailImage extends MappedLongForeignKey(this,TheImageData)
	 	  
	class TheImageData extends BaseEntity[TheImageData] with LongKeyedMapper[TheImageData] with IdPK  {
	  

		  type TheImageItem = T
	  
    	  def getSingleton = TheImageData
    	  
    	  object taggedItem extends MappedLongForeignKey(this,self.getSingleton)
    	  
    	  object image extends MappedBinaryImageFileUpload(this);
  
       object imageFormat extends MappedString(this, 10);
   
       object imageHeight extends MappedInt(this);
   
       object imageWidth extends MappedInt(this);
    	  
	}
	 
	 	object TheImageData  extends TheImageData with BaseMetaEntity[TheImageData] with LongKeyedMetaMapper[TheImageData] {
	  	  override def dbTableName =  self.getSingleton.dbTableName+"_imagedata"
	  	  
	  	  // Bugfix for the compilation issue
	  	  // solution by https://groups.google.com/forum/#!msg/liftweb/XYiKeS_wgjQ/KBEcrRZxF4cJ
	  	  override def createInstance = new TheImageData
	  	  
	  	  // Hint by David Pollak on https://groups.google.com/forum/#!topic/liftweb/Rkz06yng-P8
	  	  override def getSingleton = this
	}

   
   object imageFileName extends MappedString(this, 100);

   
  // no minimumlenght for teaser, as it is not available 
  override val teaserValidations : mutable.MutableList[String => List[FieldError]] = mutable.MutableList( FieldValidation.maxLength(TheTranslationMeta.teaser,teaserLength) _ )

  // no minimumlenght for description, as it is not available 
  override val descriptionValidations : mutable.MutableList[String => List[FieldError]] = mutable.MutableList(FieldValidation.maxLength(TheTranslationMeta.description,descriptionLength) _ )


}