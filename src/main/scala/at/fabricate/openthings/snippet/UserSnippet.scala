package at.fabricate.openthings
package snippet

import net.liftweb.common.Logger
import net.liftweb.http.DispatchSnippet
import model.User
import net.liftweb.http.S
import scala.xml.NodeSeq
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import net.liftweb.mapper.Descending
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.MaxRows
import net.liftweb.mapper.StartAt
import model.Project
import scala.xml.Text
import net.liftweb.http.SHtml
import model.Tool
import net.liftweb.http.js.JsCmds
import at.fabricate.liftdev.common.snippet.{AddSkillsSnippet, BaseEntityWithTitleAndDescriptionSnippet, BaseEntityWithTitleDescriptionAndIconSnippet}
import at.fabricate.liftdev.common.lib.UrlLocalizer
import at.fabricate.liftdev.common.model.TheGenericTranslation

object UserSnippet extends BaseEntityWithTitleAndDescriptionSnippet[User] with BaseEntityWithTitleDescriptionAndIconSnippet[User]
with AddSkillsSnippet[User]  {
  
  override val TheItem = User
  override def itemBaseUrl = "designer"
  override def viewTemplate = "viewDesigner"
  override def listTemplate = "listDesigner"
  override def editTemplate = "editDesigner"
  override def viewTitle = "View Designer"
  override def listTitle = "List Designer"
  override def editTitle = "Edit Designer"
    
    
      
      val contentLanguage = UrlLocalizer.contentLocale
    
   private def bindToolsCSS(toolname : String, checkbox: NodeSeq) = 
	      ":checkbox" #>  checkbox &
	      "id=toolname" #>  toolname
    
  override def toForm(item : ItemType) : CssSel = {
	     var userTools = item.tools.map(tool => tool.name.toString)         

         def toolSelected(toolName: String)(selected: Boolean) = {
           val tool = Tool.findByName(toolName).head
            if ( selected ) item.tools += tool
            else item.tools -= tool
          }
         
          def listTools(template: NodeSeq) : NodeSeq  =             
            Tool.findAll.map(tool => tool.name.toString).
            	flatMap( toolName =>
           		bindToolsCSS(toolName, SHtml.checkbox(userTools.contains(toolName), value => toolSelected(toolName)(value) ))(template) 
           		)          

//              bindFieldsCSS(xhtml)
   (
       "#firstname" #> item.firstName .toForm &
       "#lastname" #> item.lastName  .toForm &       
       "#showicon" #> item.icon.asHtml &      
       "#emailsettings" #> item.emailSettings.toForm &      
       "#personalwebsite" #> item.personalWebsite.toForm &
       "#listtools" #> listTools _  
   ) &
        (super.toForm(item))
   }
  
   //   abstract override
   override def asHtml(item : ItemType) : CssSel = {
      
          def listTools(template: NodeSeq) : NodeSeq  =             
            item.tools.map(tool => tool.name.toString).
            	flatMap( toolName =>
           		bindToolsCSS(toolName, SHtml.checkbox(true, (_) => () ))(template) 
           		)                         
   (
       "#personalwebsite [href]" #> item.personalWebsite.get &
       "#listtools" #> listTools _ &
       "#projectbydesigner" #> item.createdProjects.map(project => {
         project.doWithTranslationFor(contentLanguage.get)( translation  =>
                   "#projectbydesignertitle *" #> translation.title.asHtml &
                   "#projectbydesignerdescription *" #> translation.teaser.asHtml &
                   "#projectbydesignerlink [href]" #> ProjectSnippet.urlToViewItem(project,translation.language.isAsLocale) 
        		 )( defaultTranslation =>
                   "#projectbydesignertitle *" #> defaultTranslation.title.asHtml &
                   "#projectbydesignerdescription *" #> defaultTranslation.teaser.asHtml &
                   "#projectbydesignerlink [href]" #> ProjectSnippet.urlToViewItem(project,defaultTranslation.language.isAsLocale)    
        		   )(
        		    // delete everything if we can not find a translation - maybe do something else instead
        		    "#projectbydesignertitle" #> "" &
                   "#projectbydesignerdescription" #> "" &
                   "#projectbydesignerlink" #> ""    
        				   )
       }) &
       "#licence *"  #> "" &
       "#initiator *"  #> "" &
        "#difficulty"  #>  ""
   ) &
   (super.asHtml(item))
  }

}
