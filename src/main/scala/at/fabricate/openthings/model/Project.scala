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
import at.fabricate.liftdev.common.model.BaseMetaEntity
import at.fabricate.liftdev.common.model.AddRepositoryMeta
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleDescriptionIconAndCommonFields
import at.fabricate.liftdev.common.model.BaseEntityWithTitleDescriptionIconAndCommonFields
import at.fabricate.liftdev.common.model.AddRepository
import at.fabricate.liftdev.common.model.BaseEntity
 
/**Meta(Kompagnion)-Objekt für die Projekt-Klasse. Enthaelt instanzuebergreifende Einstellungen.
* @author Johannes Fischer **/

object Project extends Project with BaseMetaEntity[Project] with BaseMetaEntityWithTitleDescriptionIconAndCommonFields[Project,User] with AddRepositoryMeta[Project] {
  
  

  /**Name der genutzten Tabelle in der Datenbank*/
//  override def dbTableName = "project"
  /**Anordnung der Eingabefelder in automatisch generierten Formularen (CRUDFify)*/
//  override def fieldOrder = List(teaser, creationDate, byUserId)

  /*
   * tried to implement the lazy save feature, did not work
   * Try using loginFirst of the user!!
//  abstract 
  override def save = {
    if (byUserId > 0 )
      super.save      
      else
        S.redirectTo("/login")
  }
  * 
  */
}


/**Beschreibt eine Projekt-Instanz
* @author Johannes Fischer **/
class Project extends BaseEntity[Project] with BaseEntityWithTitleDescriptionIconAndCommonFields[Project,User] with AddRepository[Project] {

    // definitions for AddTag
  type theTagType = Tag
  def theTagObject = Tag
  
  
  // definitions for Comment
  override def theUserObject = User
  
  override def getCurrentUser = User.currentUser

  // override icon-image settings
  override def defaultIcon = "/public/images/noproject.jpg"
    
  override def iconDisplayName = S.?("project\u0020icon")//S.?("user\u0020image") -> Throws an exception
  
  override def iconDbColumnName = "project_image"
    
  override def iconPath = "projectimage"
    
    // override repository settings
//       override def apiPath = "api"
//        
//      override def uploadPath = "upload"
        
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

//      object createdByUser extends MappedManyToMany(self,){ }

  
  /*  override def s
   * 
  object myEnum extends EnumWithDescription {
  override var _values = List(("icon-difficulty2"->"Starter"), ("icon-difficulty3"->"Avarage"), ("icon-difficulty4"->"Advanced"), ("icon-difficulty5"->"Expert"), ("icon-difficulty6"->"Genius"))
  }
  * 
  */
//  class DifficultyValue(name: String, description: String) extends ValueWithDescription
  
  /*
  MappedEnumWithDescription(this,myEnum)
  
    protected class MappedEnumWithDescription(obj : MapperType, theEnum: EnumWithDescriptionAndObject[String]) extends MappedEnum(obj, theEnum  ){
  
    
//    val values = List(("icon-difficulty2"->"Starter"), ("icon-difficulty3"->"Avarage"), 
 *    ("icon-difficulty4"->"Advanced"), ("icon-difficulty5"->"Expert"), ("icon-difficulty6"->"Genius")))

    /*
	  /**Genutzter Spaltenname in der DB-Tabelle*/
    override def dbColumnName = "project_initiator"
    
      
    /**Name des Datenfeldes für CRUD-Seiten*/
    override def displayName = S.?("project\u0020initiator")
    
      
    /**Darstellung des Feldes auf CRUD-Seiten. Anstelle der Id wird Nachname und Vorname des Autors
     * angezeigt bzw. "k.A." für "keine Angabe", wenn es zu dieser User-Id keinen User gibt. */
    override def asHtml = Text(User.find(this).map(_.fullName).openOr("k.A."))
    
        
    /**Namen-Auswahlliste für CRUD-Seiten*/
    override def validSelectValues: Box[List[(Long, String)]] = {
      val currentUser : List[User] = User.currentUser.toList
      Full(currentUser.map(u => (u.id.get, u.lastName.get) ) )
    }
    * 
    */
    override def asHtml = <span class={super.asHtml}></span>
    
    
	/**
	* Build a list for the select. Return a tuple of (String, String) where the first string
	* is the id.string of the Value and the second string is the Text name of the Value.
	*/
	def buildDisplayList: List[(Int, String)] = theEnum.nameDescriptionList.map(item : EnumWithDescription => item)
//	  enum.values.toList.map(a => (a.id, a.toString))
  }
  * 
  */
  

  /**Liefert das Meta-Objekt zur eigenen Modellklasse.*/
  def getSingleton = Project

}
