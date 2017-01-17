package at.fabricate.openthings
package snippet

import model.Project
import net.liftweb.util.CssSel
import at.fabricate.liftdev.common.snippet._
import at.fabricate.liftdev.common.lib.UrlLocalizer
import scala.xml.NodeSeq
import at.fabricate.openthings.model.User
import net.liftweb.mapper.Mapper
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.util.Helpers._

object ProjectSnippet extends BaseEntityWithTitleAndDescriptionSnippet[Project] with BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[Project]
with AddRepositorySnippet[Project]
with LazyLoginForSave[Project]
with AddSkillsSnippet[Project]  
with AddImagesSnippet[Project]  
{
  
  override val TheItem = Project
  override def itemBaseUrl = "project"
  override def viewTemplate = "viewProject"
  override def listTemplate = "listProject"
  override def editTemplate = "editProject"
  override def viewTitle = "View Project"
  override def listTitle = "List Project"
  override def editTitle = "Edit Project"
    
    // configuration for lazy login
      def checkIfUserCanSave[T <: Mapper[T]](item : T) = User.canEditContent(item)

      val loginLocation = "/"+LoginSnippet.loginTemlate // "/"+
      
          
      def theUserSnippet = UserSnippet
      


      val contentLanguage = UrlLocalizer.contentLocale
    

   override def asHtml(item : ItemType) : CssSel = { 
   (   
       "#personalwebsite" #> "" 
   ) &
   (super.asHtml(item))
  }
  
  override def edit(xhtml : NodeSeq) : NodeSeq = (   
      // Hint: remove the license from the edit page to prevent later changes on that one!
       "#licence" #> NodeSeq.Empty & 
       "#licencelabel" #> NodeSeq.Empty 
   ).apply(super.edit(xhtml))
}
