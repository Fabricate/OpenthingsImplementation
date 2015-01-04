package at.fabricate.liftdev.common
package snippet

import model.AddComment
import model.AddCommentMeta
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

trait AddCommentSnippet[T <: BaseEntityWithTitleAndDescription[T] with AddComment[T]] extends BaseEntityWithTitleAndDescriptionSnippet[T] {
  
  var commentTemplate : NodeSeq = NodeSeq.Empty 
  
  abstract override def view(xhtml: NodeSeq) :  NodeSeq  =  {
    // get just the comment section
    commentTemplate = ("#comment ^^" #> "str").apply(xhtml)
    super.view(xhtml)
  }
  
  abstract override def asHtml(item : ItemType) : CssSel = {
		 
//		println("chaining asHtml from AddCommentSnippet")
    
		 def createNewItem = item.TheComment.create.commentedItem(item)
		 
		 var newComment: item.TheComment = createNewItem
		 
    	 def bindCommentCSS(comment: item.TheComment) : CssSel= 
		     "#commenttitle *" #> comment.title.asHtml &
		     "#commentauthor *" #> "Posted by: %s".format(comment.author.asHtml) &     
		     "#commentmessage *" #> comment.comment.asHtml
		   
    	 def bindNewCommentCSS : CssSel= 
		     "#newcomtitle" #> SHtml.text(newComment.title.get, value => {newComment.title.set(value);JsCmds.Noop}, "placeholder"->"Title" )&
		     "#newcomauthor" #> SHtml.text(newComment.author.get, value => {newComment.author.set(value);JsCmds.Noop}, "placeholder"->"Name"  )&     
		     "#newcommessage" #> SHtml.textarea(newComment.comment.get, value => {newComment.comment.set(value);JsCmds.Noop}, "placeholder"->"Your comment" ) & // rows="6"
		     "#newcomsubmithidden" #> SHtml.hidden(() => {
		       saveAndDisplayAjaxMessages(newComment, 
		           () => {
		            var newCommentHtml = bindCommentCSS(newComment)(commentTemplate)
		        	newComment = createNewItem
					 // add the new comment to the list of comments
					AppendHtml("comments", newCommentHtml) &
					 // clear the form
					JsCmds.SetValById("newcomtitle", "") &
					JsCmds.SetValById("newcommessage", "")
		           }, 
		           errors => {
		             JsCmds.Alert("adding comment '"+newComment.title.get+"' failed" )
		           },"commentMessages")
					          } ) 

     ("#comment" #> item.comments.map(comment => bindCommentCSS(comment))  &
     "#newcomment" #> bindNewCommentCSS) &
     // chain the css selectors 
     (super.asHtml(item))
  }
}
