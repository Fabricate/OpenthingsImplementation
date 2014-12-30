package at.fabricate
package snippet

import model.Project
import net.liftweb.util.CssSel

object ProjectSnippet extends BaseEntitySnippet[Project] with BaseRichEntitySnippet[Project]   {
  
  override val TheItem = Project
  override def itemBaseUrl = "project"
  override def itemViewUrl = "view"
  override def itemListUrl = "list"
  override def itemEditUrl = "edit"
  override def snippetView = "viewProject"
  override def snippetList = "listProject"
  override def snippetEdit = "editProject"
  override def menuNameView = "View Project"
  override def menuNameList = "List Project"
  override def menuNameEdit = "Edit Project"
    
    override def asHtml(item : ItemType) : CssSel = {
    		println("chaining asHtml from ProjectSnippet")
    		
//    		println("finished cssselector: "+super.asHtml(item).toString)
    		
    		super.asHtml(item)
  }

}