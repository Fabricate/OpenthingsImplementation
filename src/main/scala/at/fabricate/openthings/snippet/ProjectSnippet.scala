package at.fabricate.openthings
package snippet

import model.Project
import net.liftweb.util.CssSel
import at.fabricate.liftdev.common.snippet.AddRepositorySnippet
import at.fabricate.liftdev.common.snippet.BaseEntityWithTitleAndDescriptionSnippet
import at.fabricate.liftdev.common.snippet.BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet
import at.fabricate.liftdev.common.lib.UrlLocalizer
import scala.xml.NodeSeq
import at.fabricate.liftdev.common.snippet.LazyLoginForSave
import at.fabricate.openthings.model.User

object ProjectSnippet extends BaseEntityWithTitleAndDescriptionSnippet[Project] with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[Project] with AddRepositorySnippet[Project] with LazyLoginForSave[Project]  {
  
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
    
    // configuration for lazy login
      def checkIfUserCanSave = User.canEditContent
      val loginLocation = "/"+LoginSnippet.loginTemlate // "/"+
    
      def theUserSnippet = UserSnippet
      
//     override def create(xhtml: NodeSeq) : NodeSeq  = create(xhtml)  &
//     	"#" #> 


      val contentLanguage = UrlLocalizer.contentLocale
    
//    override def asHtml(item : ItemType) : CssSel = {
//    		println("chaining asHtml from ProjectSnippet")
//    		
////    		println("finished cssselector: "+super.asHtml(item).toString)
//    		
//    		super.asHtml(item)
//  }

}
