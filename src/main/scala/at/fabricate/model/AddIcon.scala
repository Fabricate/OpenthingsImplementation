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
import scala.xml.Text
import scala.xml.UnprefixedAttribute
import scala.xml.Null
import at.fabricate.lib.MatchString

trait AddIcon[T <: (AddIcon[T] with MatchByID[T]) ] extends BaseEntity[T] { // 
  // [T <: Mapper[T] ]s
  //self: KeyedMetaMapper[IdPK,T] =>
  self: T =>
    
  //type A <: LongKeyedMapper[A]
  
 // HINT: you have to use toFloat, otherwise it will result in 0 !!!
    
    
   
      // define WithImage
    /* Syntax in ProtoUser in LIFT version 2.2, causes an Error: S.?? not available !
  def defaultImage = S.??("/images/nouser.jpg")
    
  def imageDisplayName = S.??("user name") //S.?("user\u0020image")
  
  def imageDbColumnName = S.??("user_image")
    
  def baseServingPath = S.??("userimage")
  * 
  */
    
  def defaultIcon = "/public/images/noimage.jpg"
    
  def iconDisplayName = S.?("icon")
  
  def iconDbColumnName = "icon"
    
  def baseServingPath = "serve"
    
  def iconPath = "icon"
	    
	    
  lazy val icon : MappedBinaryImageFileUpload[T] = new MyIcon(this)
  
   protected class MyIcon(obj : T) extends MappedBinaryImageFileUpload(obj) {
  
  override def defaultImage = fieldOwner.defaultIcon
    
  override def imageDisplayName = fieldOwner.iconDisplayName
  
  override def imageDbColumnName = fieldOwner.iconDbColumnName
    
  override def baseServingPath = fieldOwner.baseServingPath
  
  override def iconPath = fieldOwner .iconPath

  override val fieldId = Some(Text("bin"+imageDbColumnName ))

  }
  
}

trait AddIconMeta[ModelType <: ( AddIcon[ModelType] with MatchByID[ModelType]) ] extends BaseMetaEntity[ModelType] { //
    self: ModelType  =>

      type TheIconType = ModelType
    
      //self.find
  //object TheImage extends ObjectById[AddIconMeta[TheIconType]](self) 

    object MatchServePath extends MatchString(baseServingPath)
    object MatchIconPath extends MatchString(iconPath)

      //User.a
  // TODO: Refactor to use the matchedstring
  abstract override def init : Unit = {
    LiftRules.dispatch.append({
      case r @ Req(MatchServePath(servePath) :: MatchIconPath(iconPath)  :: MatchItemByID(iconTypeID) ::
                 Nil, _, GetRequest) => ()  =>  // if (id != Empty)
                   Full(InMemoryResponse(iconTypeID.icon.get,
                               List("Content-Type" -> "image/jpeg",
                                    "Content-Length" ->
                                    iconTypeID.icon.get.length.toString), Nil, 200))
                                    })
    
  }
    // also perform all the other init operations
    super.init 
  
}

class MappedBinaryImageFileUpload[T <: BaseEntity[T]](fieldOwner : T) extends MappedBinary[T](fieldOwner) {
	    
    
  def defaultImage = "/public/images/noimage.jpg"
    
  def imageDisplayName = S.?("image")
  
  def imageDbColumnName = "image"
    
  def baseServingPath = "serve"
    
  def iconPath = "image"
    
  val maxWidth = 792
  val maxHeight = 445
  val jpegQuality : Float = 85 / 100.toFloat
  
  	        // TODO  implement later, as Crudify and Megaprotouser can not be mixed in at the same time
	    override def displayName = imageDisplayName
	    	  /**Genutzter Spaltenname in der DB-Tabelle*/
	    override def dbColumnName = imageDbColumnName
  
  def setFromUpload(fileHolder: Box[FileParamHolder]) = 
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
  
  def url  = {
    if (this.get != null && this.get.length > 0)
	      	"/%s/%s/%s".format(baseServingPath,iconPath,this.fieldOwner.asInstanceOf[LongKeyedMapper[T]].primaryKeyField.get.toString)
	      else
	        defaultImage
  }
	
	  override def asHtml:Node = {
	      <img src={url}></img>
	    }
	    //style={"max-width:" + maxWidth + ";max-height:"+maxHeight}
	  
	  override def _toForm: Box[Elem] = Full(SHtml.fileUpload(fu=>setFromUpload(Full(fu)))) //fu=>setFromUpload(Full(fu)) setFromUpload(Full(fu))))
}

  class ObjectById[T <: KeyedMetaMapper[Long,T]](obj : T) extends FieldOwner[T](obj) { 
    //self: T =>
    def unapply(in: String): Option[T] = 
      fieldOwner.find( // As[LongKeyedMetaMapper[TheType]]
    		    By(fieldOwner.primaryKeyField, 
    		        in.toInt ))
  }

abstract class FieldOwner[V](fldOwnr: V){
  def fieldOwner : V = fldOwnr
  def fieldOwnerAs[U] : U = fldOwnr.asInstanceOf[U]
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
