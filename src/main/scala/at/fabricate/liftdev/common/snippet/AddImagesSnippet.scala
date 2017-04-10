package at.fabricate.liftdev.common
package snippet

import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import scala.xml.{Null, UnprefixedAttribute, NodeSeq, Text}
import model.BaseEntity
import model.BaseMetaEntityWithTitleAndDescription
import model.BaseEntityWithTitleAndDescription
import net.liftweb.http.js.JsCmds.{Replace, SetHtml}
import net.liftweb.common.Empty
import at.fabricate.liftdev.common.model._
import net.liftweb.http.js.JsCmd
import at.fabricate.openthings.model.Project
import net.liftweb.http.RequestVar
import net.liftweb.http.S
import at.fabricate.liftdev.common.model.GeneralTag
import at.fabricate.liftdev.common.model.TheGenericTranslation
import at.fabricate.liftdev.common.lib.UrlLocalizer
import java.util.Locale
import net.liftweb.mapper.By
import net.liftweb.http.js.JE.JsFunc
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsObj
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.js.JsCmds.SetHtml



trait AddImagesSnippet[T <: (BaseEntityWithTitleAndDescription[T] with AddImages[T])] extends BaseEntityWithTitleAndDescriptionSnippet[T] {
  
  
  val contentLanguage : RequestVar[Locale]
  
    var listImagesTemplate : NodeSeq = 			NodeSeq.Empty

    var singleImageTemplate : NodeSeq = NodeSeq.Empty 
  
  abstract override def edit(xhtml: NodeSeq) :  NodeSeq  =  {
    // get just the comment section
    listImagesTemplate = ("#listImages ^^" #> "str").apply(xhtml)    
    singleImageTemplate = ("#singleImage ^^" #> "str").apply(xhtml)
    super.edit(xhtml)
  }
  
  

  
    abstract override def toForm(item : ItemType) : CssSel = {
      this.localImagesToForm(item)  &
     // chain the css selectors 
     (super.toForm(item))
    }
    
      def localImagesToForm(item : ItemType) : CssSel = {
        var imageTitle = ""
        var imageDescription = ""
          
        
        def addImage(localItem:ItemType)() : JsCmd = {
          val newImage = localItem.addNewImageToItem(contentLanguage.get, imageTitle, imageDescription)
          saveAndDisplayAjaxMessages(newImage,
            () => { 

              JsCmds.Run("uploadImageToServer('#imageUpload','/api/upload/image/"+newImage.id.toString()+"')") &
              JsCmds.After(TimeSpan(5000), AppendHtml("listImages", listAnImage(localItem)(newImage).apply(singleImageTemplate))) &
                // clear the forms
                JsCmds.SetValById("newImageIitle", "") &
                JsCmds.SetValById("newImageDescription", "") 
            },
            errors => {
              JsCmds.Alert("adding Image '"+imageTitle+"' failed" )
            },"image_messages")
        }
        
        def listAnImage(localItem : ItemType)(singleImage : localItem.TheImageType) :CssSel = {
             (
   "#thumbnailImage [src]" #> "/serve/image/%s/thumb".format(singleImage.id.toString()) &
   "#resizedImage [src]" #> "/serve/image/%s".format(singleImage.id.toString()) &
   "#originalImage [src]" #> "/serve/image/%s/orig".format(singleImage.id.toString()) &
   "#viewImageTitle" #> singleImage.defaultTranslation.openOrThrowException("Opened empty Box").title.get &
   "#viewImageDescription" #> singleImage.defaultTranslation.openOrThrowException("Opened empty Box").description.get &
   "#insertButton [onclick]" #> "insertAtCursor($('.wysiwyg-descriptionfield-editform')[0],'!/serve/image/%s!')".format(singleImage.id)
       )
        }
        
        def listAllImages(localItem : ItemType) : CssSel = "#singleImage *" #> localItem.getAllImagesForThisItem.map(anImage => listAnImage(localItem)(anImage).apply(singleImageTemplate))
        
        "#listImages *" #> listAllImages(item) &
        "#newImageIitle" #> SHtml.ajaxText("", value => {imageTitle = value}, "placeholder"->"A title for the image") &
        "#newImageDescription" #> SHtml.ajaxText("", value => {imageDescription = value}, "placeholder"->"A description for the image") &
        "#uploadImage [onclick]" #> SHtml.ajaxInvoke(addImage(item)) &
        "#testMe [onclick]" #> SHtml.ajaxInvoke(() => {println(listImagesTemplate.toString());SetHtml("listImages", listAllImages(item).apply(listImagesTemplate))}) 
      }

}
