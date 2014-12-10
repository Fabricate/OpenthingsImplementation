package at.fabricate
package model

import net.liftweb._
import net.liftweb.mapper._
import net.liftweb.http._
import net.liftweb.http.LiftRules
import net.liftweb.common._
import net.liftmodules.imaging._
import net.liftweb.util.BasicTypesHelpers._
import net.liftweb.util._
import scala.xml.Node
import scala.xml.Elem


trait WithImage[T <: WithImage[T] ] extends  LongKeyedMapper[T] { 
  // [T <: Mapper[T] ]s
  //self: KeyedMetaMapper[IdPK,T] =>
  self: T =>
    
  //type A <: LongKeyedMapper[A]
  
  val defaultImage = "/images/nouser.jpg"
    
  val imageDisplayName = "user image"
  
  val imageDbColumnName = "user_image"
    
  val baseServingPath = "userimage"
    
  val maxWidth = 792
  val maxHeight = 445
  val jpegQuality : Float = 85 / 100.toFloat // HINT: you have to use toFloat, otherwise it will result in 0 !!!
	    
	    
  lazy val image : MappedBinaryFileUpload[T] = new myImage(this)
  
   protected class myImage(obj : T) extends MappedBinaryFileUpload(obj) {
	    
	        // TODO  implement later, as Crudify and Megaprotouser can not be mixed in at the same time
	    override def displayName = imageDisplayName
	    	  /**Genutzter Spaltenname in der DB-Tabelle*/
	    override def dbColumnName = imageDbColumnName
	    
	    override def setFromUpload(fileHolder: Box[FileParamHolder]) = 
	      fileHolder match {
	              case Full(FileParamHolder(_,null,_,_)) => S.error("Sorry - this does not look like an image!")
	              case Full(FileParamHolder(_,mime,_,data))
	                if mime.startsWith("image/") => 	{
	                  val inputStream = fileHolder.openOrThrowException("Image should not be Empty!").fileStream
	                  var metaImage = ImageResizer.getImageFromStream(inputStream)
	                  metaImage = ImageResizer.removeAlphaChannel(metaImage)
	                  val image = ImageResizer.max(metaImage.orientation, metaImage.image, maxWidth , maxHeight )
	                  val jpg = ImageResizer.imageToBytes(ImageOutFormat.jpeg , image, jpegQuality)
	                  this.set(jpg)
	                }
	              case Full(FileParamHolder(_,mime,_,data))
	                if ! mime.startsWith("image/") =>  S.error("Sorry - this does not look like an image!")
	              case Full(_) => S.error("Invalid attachment")
	              case _ => S.error( "No Attachment: "+fileHolder )
	            }
	
	  override def asHtml:Node = {
	      if (this.get != null && this.get.length > 0)
	      	<img src={"/serve/"+baseServingPath+"/"+this.fieldOwner.asInstanceOf[LongKeyedMapper[T]].primaryKeyField.get}  ></img>
	      else
	        <img src={defaultImage}  ></img>
	    }
	    //style={"max-width:" + maxWidth + ";max-height:"+maxHeight}
	  
	  override def _toForm: Box[Elem] = Full(SHtml.fileUpload(fu=>setFromUpload(Full(fu)))) //fu=>setFromUpload(Full(fu)) setFromUpload(Full(fu))))
	  
  }
  
}

trait WithImageMeta[U <: WithImage[U] ] extends LongKeyedMetaMapper[U] {
  self: U  =>
  
  
    
  object TImage extends GetParent[WithImageMeta[U]](this) {
    def unapply(in: String): Option[U] =
    		getParent.find(By(getParent.primaryKeyField, in.toInt ))
  }
  
  /*
  override def afterSchemifier = {
    
    

    super.afterSchemifier 
    
    LiftRules.dispatch.append({
      case r @ Req("serve" :: baseServingPath  :: TImage(id) ::
                 Nil, _, GetRequest) => () => Full(InMemoryResponse(this.image.get,
                               List("Content-Type" -> "image/jpeg",
                                    "Content-Length" ->
                                    this.image.get.length.toString), Nil, 200))
                                    })
  }
  * 
  */
}

class MappedBinaryFileUpload[T <: Mapper[T]](fieldOwner : T) extends MappedBinary[T](fieldOwner) {
  def setFromUpload(fileHolder: Box[FileParamHolder]) = {}
}

abstract class GetParent[V](parent: V){
  def getParent : V = parent
}
/*
object TImageBase [V <: WithImageMeta[V]] (withImage : V){
    	def unapply(in: String): Option[V] =
    		withImage.find(By(withImage.id, in.toInt ))
  }
  * 
  
    	def unapply(in: String): Option[U] =
    		this.find(By(this.primaryKeyField, in.toInt ))
  */
