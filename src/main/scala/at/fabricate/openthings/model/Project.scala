package at.fabricate.openthings
package model

import net.liftweb.mapper._
import java.text.SimpleDateFormat
import java.util.{Locale, Date}
import net.liftweb.http._
import net.liftweb.util._
import net.liftweb.common._
import scala.xml.{NodeSeq,Text}
import java.util.Calendar
import scala.xml.UnprefixedAttribute
import scala.xml.Null
import at.fabricate.liftdev.common.lib.{EnumWithKeyAndValue, EnumWithStringKeyAndValue, EnumWithDescriptionAndObject }
import at.fabricate.liftdev.common.model._

/**Meta(Kompagnion)-Objekt für die Projekt-Klasse. Enthaelt instanzuebergreifende Einstellungen.
* @author Johannes Fischer **/

object Project extends Project with BaseMetaEntity[Project] with BaseMetaEntityWithTitleDescriptionIconAndCommonFields[Project] with AddRepositoryMeta[Project]
with AddSkillsMeta[Project] {
  
  
}


/**Beschreibt eine Projekt-Instanz
* @author Johannes Fischer **/
class Project extends BaseEntity[Project] with BaseEntityWithTitleDescriptionIconAndCommonFields[Project] with AddRepository[Project]
with AddSkills[Project] {

    // definitions for AddTag
  type TheTagType = Tag
  def theTagObject = Tag

  // definitions for AddSkill
  type TheSkillType = Skill
  def theSkillObject = Skill

    // definitions for AddCategories
  type TheCategoryType = Category
  def theCategoryObject = Category
  
  // definitions for AddCreatedBy and maybe some others
  type TheUserType = User
  def theUserObject = User
  
  override def getCurrentUser = User.currentUser

  // override icon-image settings
  override def defaultIcon = "/public/images/noproject.jpg"
    
  override def iconDisplayName = S.?("project\u0020icon")
  
  override def iconDbColumnName = "project_image"
    
  override def iconPath = "projectimage"    
    
  override def maxIconWidth = 1024
  
  override def maxIconHeight = 576
  
  override def applyIconCropping = true
    
        
      override def repositoryPath = "projectrepository"
        // this is the location where all the projects are
        // eg. webapp/projects/<projectID>
       override def basePathToRepository : String = "projects"
       // this is the location where the repository is inside the project dir
       // eg. webapp/projects/<projectID>/repository
 	   override def endPathToRepository : String = "repository"
       // this is the location where the data (eg. zip for Repo Commit) is inside the project dir
       // eg. webapp/projects/<projectID>/data
 	   override def endPathToData : String = "data"
  

  /**Liefert das Meta-Objekt zur eigenen Modellklasse.*/
  def getSingleton = Project

}
