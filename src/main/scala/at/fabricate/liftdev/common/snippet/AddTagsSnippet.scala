package at.fabricate.liftdev.common
package snippet

import net.liftweb.util.CssSel
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq
import scala.xml.Text
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

trait AddTagsSnippet[T <: (BaseEntityWithTitleAndDescription[T] with AddTags[T])] extends BaseEntityWithTitleAndDescriptionSnippet[T] {
  
//  val tagItem = TheItem.asInstanceOf[AddTags[T]]
//  
//  type LocalTheTagType = tagItem.TheTagType
  //Project.getTagMapper
  
//    val tagItem = TheItem.asInstanceOf[AddTagsMeta[T]]
    
//    if (tagItem == null) println("tag item is null!")
  
//  val tagToItemMapper = TheItem.getTagMapper
//  
//  val theTagObject  = TheItem.theTagObject  // TheItem.theTagObject //
//   with MatchByID[T]
//  : GeneralTagMeta[LocalTheTagType] with MatchByID[LocalTheTagType]
//  var allTags = ("-1"->"please select a valid tag")::theTagObject.findAll.map(item => (item.primaryKeyField.toString, item.title.toString))
  
// 	 def bindNewRatingCSS(item : ItemType) : CssSel = {
// 	   	 def createNewItem : item.TheRating = item.TheRating.create.ratedItem(item) 
//		 
//		 listOfRatingOptionsCSStoInt.map({
//		   case (css, value) => {
//		     val newRating = createNewItem
//		   	 newRating.rating.set(value)
//		     "#%s [onclick]".format(css) #> SHtml.ajaxInvoke(() => {
//	       saveAndDisplayAjaxMessages(newRating,
//	           () => {
//				 // update the ratings
//				SetHtml("rating", generateDisplayRating(item)) &
////				 // hide the form
//				SetHtml("newrating",NodeSeq.Empty )
////				JsCmds.SetValById("newcomtitle", "") &
////				JsCmds.SetValById("newcommessage", "")
//	           }, 
//	           errors => {
//	             errors.map(println(_))
//	             JsCmds.Alert("adding rating failed! " )
//	           },"ratingMessages")
//			          } )
//		   }
//		 }).reduce(_ & _)
// 	 }
  
//  def loadTags(item : ItemType)( ids : List[String]) : List[item.TheTags] = ids.map(id => item.theTagObject.findByKey(id.toLong).get)
//  def loadTag( id : String)  = theTagObject.findByKey(id.toLong)

  
  abstract override def asHtml(item : ItemType) : CssSel = {
		 
//		println("chaining asHtml from AddCommentSnippet")
     ("#aTag *" #> item.tags.map(itm => itm.theTag.toString)
         )
     // chain the css selectors 
     (super.asHtml(item))
  }
  
    abstract override def toForm(item : ItemType) : CssSel = {
		 var newTagTitle = ""
//		println("chaining asHtml from AddCommentSnippet")
//     ("#tags" #> SHtml.multiSelect(allTags, List(), item.tags ++= loadTag(item)(_) )
		
		//val theTagObject = item.theTagObject
		
		def getAllTags(localItem: ItemType) = localItem.theTagObject.findAll.map(item => (item.primaryKeyField.toString, item.title.toString))

		def loadTag(localItem: ItemType)( id : String)  = localItem.theTagObject.findByKey(id.toLong)

		   
		def createNewTag(localItem: ItemType)() : JsCmd = {
		   println("created tag "+newTagTitle)
		   localItem.theTagObject.create.title(newTagTitle).save
		   JsCmds.Noop
		 }
		def submitSelectedTags(localItem: ItemType)(selectedTags : List[String]) : JsCmd = {
		  println("received tag-list"+selectedTags.mkString(","))
		  selectedTags.map(
		      stringTag => loadTag(localItem)(stringTag).map(theSelectedTag => localItem.getTagMapper.create.taggedItem(localItem).theTag(theSelectedTag).save )
		      )
		  JsCmds.Noop
		}
		   
     ("#tags" #> SHtml.multiSelect(getAllTags(item), List(),submitSelectedTags(item),  "size"->"5") &
//         selected => selected.map(
//         _ match {
//       case item.theTagObject.
////       theTagObject.getSingleton.MatchItemByID()
//     }
//       tag => loadTag(tag).map(theSelectedTag => tagToItemMapper.create.taggedItem(item).theTag(theSelectedTag).save ) // +=  _:item.TheTags 
//       ),  "size"->"5") &
       "#newTagTitle" #> SHtml.ajaxText("", tagTitle => {newTagTitle=tagTitle;JsCmds.Noop}) &
       "#newTagButton [onclick]" #> SHtml.ajaxInvoke(createNewTag(item))
//     }
         
         )  &
     // chain the css selectors 
     (super.toForm(item))
  }
}
