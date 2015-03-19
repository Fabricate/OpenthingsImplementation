package at.fabricate.liftdev.common
package snippet

import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import scala.xml.{Null, UnprefixedAttribute, NodeSeq, Text}
import model.BaseEntity
import model.BaseMetaEntityWithTitleAndDescription
import model.BaseEntityWithTitleAndDescription
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.common.Empty
import at.fabricate.liftdev.common.model.AddSkills
import at.fabricate.liftdev.common.model.MatchByID
import at.fabricate.liftdev.common.model.GeneralSkill
import at.fabricate.liftdev.common.model.GeneralSkillMeta
import net.liftweb.http.js.JsCmd
import at.fabricate.liftdev.common.model.AddSkillsMeta
import at.fabricate.openthings.model.Project
import net.liftweb.http.RequestVar
import net.liftweb.http.S
import at.fabricate.liftdev.common.model.GeneralSkill
import at.fabricate.liftdev.common.model.TheGenericTranslation
import at.fabricate.liftdev.common.lib.UrlLocalizer
import java.util.Locale
import net.liftweb.mapper.By
import net.liftweb.http.js.JsCmds.SetHtml


trait AddSkillsSnippet[T <: (BaseEntityWithTitleAndDescription[T] with AddSkills[T])] extends BaseEntityWithTitleAndDescriptionSnippet[T] {
  
  
  val contentLanguage : RequestVar[Locale]
  
    var selectSkillsTemplate : NodeSeq = NodeSeq.Empty
    var listSkillsTemplate : NodeSeq = NodeSeq.Empty
    var selectSingleSkillTemplate : NodeSeq = NodeSeq.Empty
  
  abstract override def view(xhtml: NodeSeq) :  NodeSeq  =  {
    // get just the comment section
    listSkillsTemplate = ("#listskills ^^" #> "str").apply(xhtml)
    selectSkillsTemplate = ("#selectskills ^^" #> "str").apply(xhtml)
    selectSingleSkillTemplate = ("#selectsingleskill ^^" #> "str").apply(xhtml)
    super.view(xhtml)
  }
  


  def listAllSkillsForItem(localItem : ItemType) : CssSel =
    localItem.getAllSkillsForThisItem match {
      case Nil => ("#singleskill" #> (None: Option[String]) )

      // dummy for now
      case alist => ("#singleskill *" #> alist.map(aSkill => <a>{aSkill.defaultTranslation.getObjectOrHead.title.get}</a> % new UnprefixedAttribute("href","/skill/%s".format(aSkill.defaultTranslation.getObjectOrHead.id.get), Null)  ))

    }
  
  abstract override def asHtml(item : ItemType) : CssSel = {
     
     	listAllSkillsForItem(item) &
         // add the onsite editing stuff
         this.localSkillsToForm(item) &
     // chain the css selectors 
     (super.asHtml(item))
  }
  

  
    abstract override def toForm(item : ItemType) : CssSel = {
      this.localSkillsToForm(item)  &
     // chain the css selectors 
     (super.toForm(item))
    }
      def localSkillsToForm(item : ItemType) : CssSel = {
        
        def skillSelected(localItem:ItemType)(singleSkill:localItem.TheSkillType)(selected:Boolean) : JsCmd = {
	        val skillMapper = localItem.getSkillMapper
	          selected match {
	              case true => {
	                localItem.skills += skillMapper.create.taggedItem(localItem).theSkill(singleSkill).saveMe
	              }
	              case false => {
	                skillMapper.find(By(localItem.TheSkills.taggedItem,localItem),
	                    By(localItem.TheSkills.theSkill,singleSkill)).map(skillMappingFound => {
	                      localItem.skills -= skillMappingFound
	                      skillMappingFound.delete_!
	                    })
	              }
	        }
	        SetHtml("listskills",listAllSkillsForItem(item).apply(listSkillsTemplate ))
          } 
        
        def addSkill(localItem:ItemType)(skillName:String) : JsCmd = {
          println("addSkill with name "+skillName)
          val newSkill = localItem.addNewSkillToItem(contentLanguage.get, skillName)
          newSkill.save
 					AppendHtml("selectskills", listASkill(localItem)(newSkill).apply(selectSingleSkillTemplate)) &
					 // clear the form
					JsCmds.SetValById("newskillname", "")
        }
        
        def listASkill(localItem : ItemType)(singleSkill : localItem.TheSkillType) :CssSel = {
          (
              // dummy for now
            "#skilllabel *" #> singleSkill.defaultTranslation.getObjectOrHead.title.get &
            "#skillselect" #> SHtml.ajaxCheckbox(localItem.getAllSkillsForThisItem.contains(singleSkill),skillSelected(localItem)(singleSkill) )
            )        
        }
        
        def listAllSkills(localItem : ItemType) : CssSel = "#selectsingleskill *" #> localItem.getAllAvailableSkills.map(aSkill => listASkill(localItem)(aSkill))
        

        "#selectskills *" #> listAllSkills(item) &
        "#newskillname" #> SHtml.text("", addSkill(item))
      }


}
