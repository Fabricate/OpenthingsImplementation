package at.fabricate
package snippet

import model.Project
import net.liftweb.util.CssSel

object ProjectSnippet extends BaseEntitySnippet[Project]with AddCommentSnippet[Project] with BaseRichEntitySnippet[Project]   {
  
  override val TheItem = Project
  override val itemBaseUrl = "project"
    
    override def asHtml(item : ItemType) : CssSel = {
    		println("chaining asHtml from ProjectSnippet")
    		super.asHtml(item)
  }

}