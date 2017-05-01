package at.fabricate.liftdev.common
package lib


import net.liftweb._
import net.liftweb.http._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.common._

import net.liftmodules.imaging._

import java.awt.image.BufferedImage

object ImageHelper {
  
  val ALIGN_CENTER=0
  
  val ALIGN_TOP_OR_LEFT=1
    
  val ALIGN_BOTTOM_OR_RIGHT=2
  
  def containsImage(fileHolder: Box[FileParamHolder]) : Boolean = {
    	      fileHolder match {
	              case Full(FileParamHolder(_,mime,_,data))
	                if mime.startsWith("image/") => 	true
	              case _ => false
	            }
  }
  
  	      /**
   * Crop the image to fit the given width and height
   * Will preserve the aspect ratio of the original and than center crop the larger dimension.
   * A image of (200w,240h) squared to (100) will first resize to (100w,120h) and then take then crop
   * 10 pixels from the top and bottom of the image to produce (100w,100h)
   */
  def crop(sourceImage : ImageWithMetaData, imageWidth:Int , imageHeight:Int, alignCroppedImage:Int):ImageWithMetaData = {
    
      //val orientation:Box[ImageOrientation.Value] = image.orientation
      val originalImage:BufferedImage = sourceImage.image
      val orientation = sourceImage.orientation
      val height = originalImage.getHeight
      val width = originalImage.getWidth
      val ratio:Double = width.doubleValue/height
      
      val targetRatio:Double = imageWidth.doubleValue/imageHeight
      //just enter the limiting value that is needed there, 
      // ImageResizer.max will preserve the aspect ratio and will scale to the bounding box
      val (scaledWidth, scaledHeight) = if (ratio < targetRatio) {
        (imageWidth, (imageHeight.doubleValue/ratio*targetRatio).toInt)
        // should be (imageWidth,height)
      } else {
        ((imageWidth.doubleValue/targetRatio*ratio).toInt,imageHeight)
        // should be (width, imageHeight)
      }      
    val image = {
      // resize should also do the trick!
      ImageResizer.max(orientation, originalImage, scaledWidth, scaledHeight)
    }
    
    def heightDiff:Int = (image.getHeight-imageHeight)
    def widthDiff:Int = (image.getWidth-imageWidth)

          // calculate the corner position of the resulting image
          // inside the scaled image, depends on cropping strategy
          val (left,top) = alignCroppedImage match {
            case ALIGN_TOP_OR_LEFT => (0,0)
            case ALIGN_BOTTOM_OR_RIGHT => (widthDiff,heightDiff)
            case ALIGN_CENTER => (widthDiff/2,heightDiff/2)            
            case _ => (widthDiff/2,heightDiff/2)
          }


          // crop the image to fit the target size          
          new ImageWithMetaData(image.getSubimage(left,top, imageWidth, imageHeight),orientation,sourceImage.format)
  }
  
  def resize(sourceImage : ImageWithMetaData, maxImageWidth:Int , maxImageHeight:Int):ImageWithMetaData = {
    var metaImage = sourceImage.image
    new ImageWithMetaData(ImageResizer.max(sourceImage.orientation, metaImage, maxImageWidth , maxImageHeight ),sourceImage.orientation,sourceImage.format)
  }
  
  def imageToJPEGByteArray(sourceImage : ImageWithMetaData, jpegImageQuality : Float = 95/100): Array[Byte] = {
    val metaImage = sourceImage.image
    ImageResizer.imageToBytes(ImageOutFormat.jpeg , metaImage, jpegImageQuality)
  }
  
  def jpegMIMEType = "image/jpeg"
  
}