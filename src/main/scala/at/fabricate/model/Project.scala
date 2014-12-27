package at.fabricate
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
 
/**Meta(Kompagnion)-Objekt für die Projekt-Klasse. Enthaelt instanzuebergreifende Einstellungen.
* @author Johannes Fischer **/
//BEGIN(crud)
object Project extends Project with BaseRichEntityMeta[Project] with AddRepositoryMeta[Project] 
//END(crud)
//with CRUDify[Long, Project] 
{
  
  

  /**Name der genutzten Tabelle in der Datenbank*/
//  override def dbTableName = "project"
  /**Anordnung der Eingabefelder in automatisch generierten Formularen (CRUDFify)*/
//  override def fieldOrder = List(teaser, creationDate, byUserId)
  /**Name des Menuepunktes für die Ansicht aller Objekte fuer CRUDify-Seiten*/
//  override def showAllMenuName = S.?("projects")
  /**Name des Menuepunktes für das Erstellen eines neuen Objekts auf CRUDify-Seiten*/
//  override def createMenuName = S.?("create\u0020new\u0020project")

  
  // create a multipart upload form for Crudify
//  override def _createTemplate = super._createTemplate %  	new UnprefixedAttribute("multipart",Text("yes"),Null)
  
//  override def _editTemplate = super._editTemplate %  	new UnprefixedAttribute("multipart",Text("yes"),Null)

//  override def viewTemplate = 
    
    //LiftRules.loadResourceAsXml("/viewProject.html")  openOr <p>Error loading template</p>
  
  //BEGIN(pageWrapper)
  /**Einbettung aller News-CRUD-Seiten in das default-Template mit Admin-Menue und Titelanzeige 
  * eines "Erstellen"-Links*/
  /*
  override def pageWrapper (body: NodeSeq) = {
    <lift:surround with="default" at="content">
      <lift:embed what="admin_menu"/>
      <h1><lift:Menu.title/></h1>
       
      <div id="formBox">
        {body}
      </div>
    </lift:surround>
  }
  //END(pageWrapper)
  */
}//object


/**Beschreibt eine Projekt-Instanz
* @author Johannes Fischer **/
class Project extends BaseRichEntity[Project] with AddRepository[Project] {
  
      // define WithImage
  
  // override def works, val gives the known nullpointer exception
  
//      object FindByID extends ObjectById[Project](this)


  override def defaultIcon = "/public/images/noproject.jpg"
    
  override def iconDisplayName = S.?("project\u0020icon")//S.?("user\u0020image") -> Throws an exception
  
  override def iconDbColumnName = "project_image"
    
  override def baseServingPath = "projectimage"

  /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object byUserId extends MappedLongForeignKey(this, User){

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
    //  Full(User.allTeachers.map(u => (u.id.is, u.lastName.is)))
  }
  //END(crudModify)


  /**Liefert das Meta-Objekt zur eigenen Modellklasse.*/
  def getSingleton = Project

}