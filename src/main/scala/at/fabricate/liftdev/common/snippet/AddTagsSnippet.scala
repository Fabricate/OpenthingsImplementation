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
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.common.Empty
import at.fabricate.liftdev.common.model.AddTags
import at.fabricate.liftdev.common.model.MatchByID
import at.fabricate.liftdev.common.model.GeneralTag
import at.fabricate.liftdev.common.model.GeneralTagMeta
import net.liftweb.http.js.JsCmd
import at.fabricate.liftdev.common.model.AddTagsMeta
import at.fabricate.openthings.model.Project
import net.liftweb.http.RequestVar
import net.liftweb.http.S
import at.fabricate.liftdev.common.model.GeneralTag
import at.fabricate.liftdev.common.model.TheGenericTranslation
import at.fabricate.liftdev.common.lib.UrlLocalizer
import java.util.Locale
import net.liftweb.mapper.By
import net.liftweb.http.js.JsCmds.SetHtml


trait AddTagsSnippet[T <: (BaseEntityWithTitleAndDescription[T] with AddTags[T])] extends BaseEntityWithTitleAndDescriptionSnippet[T] {
  
  
  val contentLanguage : RequestVar[Locale]
  
    var selectTagsTemplate : NodeSeq = NodeSeq.Empty
    var listTagsTemplate : NodeSeq = NodeSeq.Empty 
    var selectSingleTagTemplate : NodeSeq = NodeSeq.Empty 
  
  abstract override def view(xhtml: NodeSeq) :  NodeSeq  =  {
    // get just the comment section
    listTagsTemplate = ("#listtags ^^" #> "str").apply(xhtml)    
    selectTagsTemplate = ("#selecttags ^^" #> "str").apply(xhtml)    
    selectSingleTagTemplate = ("#selectsingletag ^^" #> "str").apply(xhtml)
    super.view(xhtml)
  }
  


  def listAllTagsForItem(localItem : ItemType) : CssSel = 
    localItem.getAllTagsForThisItem match {
      case Nil => ("#singletag" #> (None: Option[String]) )

      // dummy for now
      case alist => ("#singletag *" #> alist.map(aTag => <a>{aTag.defaultTranslation.getObjectOrHead.title.get}</a> % new UnprefixedAttribute("href","/tag/%s".format(aTag.defaultTranslation.getObjectOrHead.id.get), Null)  ))

    }
  
  abstract override def asHtml(item : ItemType) : CssSel = {
     
     	listAllTagsForItem(item) &
         // add the onsite editing stuff
         this.localTagsToForm(item) &
     // chain the css selectors 
     (super.asHtml(item))
  }
  

  
    abstract override def toForm(item : ItemType) : CssSel = {
      this.localTagsToForm(item)  &
     // chain the css selectors 
     (super.toForm(item))
    }
      def localTagsToForm(item : ItemType) : CssSel = {
        
        def tagSelected(localItem:ItemType)(singleTag:localItem.TheTagType)(selected:Boolean) : JsCmd = {
	        val tagMapper = localItem.getTagMapper
	          selected match {
	              case true => {
	                localItem.tags += tagMapper.create.taggedItem(localItem).theTag(singleTag).saveMe
	              }
	              case false => {
	                tagMapper.find(By(localItem.TheTags.taggedItem,localItem),
	                    By(localItem.TheTags.theTag,singleTag)).map(tagMappingFound => {
	                      localItem.tags -= tagMappingFound
	                      tagMappingFound.delete_!
	                    })
	              }
	        }
	        SetHtml("listtags",listAllTagsForItem(item).apply(listTagsTemplate ))
          } 
        
        def addTag(localItem:ItemType)(tagName:String) : JsCmd = {
          println("addTag with name "+tagName)
          val newTag = localItem.addNewTagToItem(contentLanguage.get, tagName)  
          newTag.save
 					AppendHtml("selecttags", listATag(localItem)(newTag).apply(selectSingleTagTemplate)) &
					 // clear the form
					JsCmds.SetValById("newtagname", "")
        }
        
        def listATag(localItem : ItemType)(singleTag : localItem.TheTagType) :CssSel = {
          (
              // dummy for now
            "#taglabel *" #> singleTag.defaultTranslation.getObjectOrHead.title.get &
            "#tagselect" #> SHtml.ajaxCheckbox(localItem.getAllTagsForThisItem.contains(singleTag),tagSelected(localItem)(singleTag) ) 
            )        
        }
        
        def listAllTags(localItem : ItemType) : CssSel = "#selectsingletag *" #> localItem.getAllAvailableTags.map(aTag => listATag(localItem)(aTag))
        

        "#selecttags *" #> listAllTags(item) &
        "#newtagname" #> SHtml.text("", addTag(item)) 
      }


}
