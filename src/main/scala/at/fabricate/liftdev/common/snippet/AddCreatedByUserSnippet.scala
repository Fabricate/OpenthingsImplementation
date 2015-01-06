package at.fabricate.liftdev.common
package snippet

import model.AddRating
import model.AddRatingMeta
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
import at.fabricate.liftdev.common.model.AddCreatedByUser
import net.liftweb.common.Full
import net.liftweb.common.Box

trait AddCreatedByUserSnippet[T <: BaseEntityWithTitleAndDescription[T] with AddCreatedByUser[T]] extends BaseEntityWithTitleAndDescriptionSnippet[T] {
  
  def theUserSnippet : BaseEntityWithTitleAndDescriptionSnippet[_]
  
//  var listOfRatingOptionsCSStoInt : List[(String,Int)] = List(
//		  	"rating1"->1,
//		  	"rating2"->2,
//		  	"rating3"->3,
//		  	"rating4"->4,
//		  	"rating5"->5
//		  	
//		  )
//  
// 	 def bindNewRatingCSS(item : ItemType) : CssSel = {
// 	   	 def createNewItem : item.TheRating = item.TheRating.create.ratedItem(item) 
//		 
//		 listOfRatingOptionsCSStoInt.map({AddRating
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
////	     "#newcomtitle" #> SHtml.text(newComment.title.get, value => {newComment.title.set(value);JsCmds.Noop}, "placeholder"->"Title" )&
////	     "#newcomauthor" #> SHtml.text(newComment.author.get, value => {newComment.author.set(value);JsCmds.Noop}, "placeholder"->"Name"  )&     
////	     "#newcommessage" #> SHtml.textarea(newComment.comment.get, value => {newComment.comment.set(value);JsCmds.Noop}, "placeholder"->"Your comment" ) & // rows="6"
////	     "#newcomsubmithidden" #> SHtml.hidden(() => {
////	       saveAndDisplayAjaxMessages(newComment, 
////	           () => {
////	            var newCommentHtml = bindCommentCSS(newComment)(commentTemplate)
////	        	newComment = createNewItem
////				 // add the new comment to the list of comments
////				AppendHtml("comments", newCommentHtml) &
////				 // clear the form
////				JsCmds.SetValById("newcomtitle", "") &
////				JsCmds.SetValById("newcommessage", "")
////	           }, 
////	           errors => {
////	             JsCmds.Alert("adding comment '"+newComment.title.get+"' failed" )
////	           },"commentMessages")
////			          } ) 
//  
//  def generateDisplayRating(item : ItemType) : NodeSeq = {
//		val ratingSum : Double = item.ratings.foldLeft(0)(_ + _.rating.get)
////		val rating : Double = ratingSum / item.ratings.length
//		if (item.ratings.length > 0)
//			Text((ratingSum / item.ratings.length).toString)
//		else
//		  Text("no ratings available")
//  }
  
  abstract override def asHtml(item : ItemType) : CssSel = {
		 
//		println("chaining asHtml from AddCommentSnippet")
    val boxedUser : Box[item.TheUserType] = item.createdByUser
    val initiatorSelectors : CssSel = boxedUser match {
      case Full(initiatingUser) => ("#initiator *+" #> <a href={theUserSnippet.urlToViewItem(initiatingUser)}>{"%s %s".format(initiatingUser.firstName , initiatingUser.lastName )}</a>  &
     "#contactinitiator [href]" #> theUserSnippet.urlToViewItem(initiatingUser))
      case _ => ("#initiator *+" #> "Unknown Initiator!"  &
     "#contactinitiator [href]" #> "")
    }
     initiatorSelectors &
     // chain the css selectors 
     (super.asHtml(item))
  }
}
