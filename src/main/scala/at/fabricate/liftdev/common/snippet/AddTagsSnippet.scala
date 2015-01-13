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

trait AddTagsSnippet[T <: (BaseEntityWithTitleAndDescription[T] with AddTags[T])] extends BaseEntityWithTitleAndDescriptionSnippet[T] {
  
//  type LocalTheTagType = TheTagType
  
  val theTagObject = TheItem.asInstanceOf[AddTags[T]].theTagObject
  
  var allTags = theTagObject.findAll.map(item => (item.primaryKeyField.toString, item.title.toString))
  
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
  def loadTag( id : String)  = theTagObject.findByKey(id.toLong)

  
  abstract override def asHtml(item : ItemType) : CssSel = {
		 
//		println("chaining asHtml from AddCommentSnippet")
     ("#aTag *" #> item.tags.map(itm => itm.theTag.toString)
         )
     // chain the css selectors 
     (super.asHtml(item))
  }
  
    abstract override def toForm(item : ItemType) : CssSel = {
		 
//		println("chaining asHtml from AddCommentSnippet")
//     ("#tags" #> SHtml.multiSelect(allTags, List(), item.tags ++= loadTag(item)(_) )
//     ("#tags" #> SHtml.multiSelect(allTags, List(), selected => selected.map(tag => loadTag(tag).map(item.tags +=  _:item.TheTags ) ))
//         
//         )
     // chain the css selectors 
     (super.toForm(item))
  }
}
