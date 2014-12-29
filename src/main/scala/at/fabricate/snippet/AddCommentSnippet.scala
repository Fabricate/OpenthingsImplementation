package at.fabricate
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
import model.BaseEntityMeta
import model.BaseRichEntityMeta
import model.BaseRichEntity

trait AddCommentSnippet[T <: BaseRichEntity[T] with AddComment[T]] extends BaseRichEntitySnippet[T] {
  
  var commentTemplate : NodeSeq = NodeSeq.Empty 
  
  abstract override def view(xhtml: NodeSeq) :  NodeSeq  =  {
//    var commentTemplate = xhtml
    // just grab the template for the comments
    var theTemplate = xhtml
    // get just the comment section
    ("#comment" #> {(node : NodeSeq) => {
      commentTemplate = node
      Text("removed")}}).apply(theTemplate)
      
      super.view(xhtml)
  }
  
  abstract override def asHtml(item : ItemType) : CssSel = {
		 
		 def createNewItem = item.TheComment.create.commentedItem(item)
		 
		 var newComment: item.TheComment = createNewItem
		 
    	 def bindCommentCSS(comment: item.TheComment) : CssSel= 
		     "#commenttitle *" #> comment.title.asHtml &
		     "#commentauthor *" #> "Posted by: %s".format(comment.author.asHtml) &     
		     "#commentmessage *" #> comment.comment.asHtml
		   
    	 def bindNewCommentCSS : CssSel= 
		     "#newcomtitle" #> SHtml.text(newComment.title.get, value => {newComment.title.set(value);JsCmds.Noop}, "default"->"Title" )&
		     "#newcomauthor" #> SHtml.text(newComment.author.get, value => {newComment.author.set(value);JsCmds.Noop}, "default"->"Name"  )&     
		     "#newcommessage" #> SHtml.textarea(newComment.comment.get, value => {newComment.comment.set(value);JsCmds.Noop}, "default"->"Your comment" ) & // rows="6"
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
    	 
     "#comment" #> item.comments.map(comment => bindCommentCSS(comment))  &
     "#newcomment" #> bindNewCommentCSS &
     // chain the css selectors 
     super.toForm(item)
  }
}