package at.fabricate
package snippet

import model.Project
import net.liftweb.util.CssSel

object ProjectSnippet extends BaseEntityWithTitleAndDescriptionSnippet[Project] with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[Project] with AddRepositorySnippet[Project]  {
  
  override val TheItem = Project
  override def itemBaseUrl = "project"
//    Dont change anything as it is hardcoded atm
//  override def itemViewUrl = "view"
//  override def itemListUrl = "list"
//  override def itemEditUrl = "edit"
  override def viewTemplate = "viewProject"
  override def listTemplate = "listProject"
  override def editTemplate = "editProject"
  override def viewTitle = "View Project"
  override def listTitle = "List Project"
  override def editTitle = "Edit Project"
    
//    override def asHtml(item : ItemType) : CssSel = {
//    		println("chaining asHtml from ProjectSnippet")
//    		
////    		println("finished cssselector: "+super.asHtml(item).toString)
//    		
//    		super.asHtml(item)
//  }

}