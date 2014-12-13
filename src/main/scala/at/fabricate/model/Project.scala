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
 
/**Meta(Kompagnion)-Objekt für die Projekt-Klasse. Enthaelt instanzuebergreifende Einstellungen.
* @author Johannes Fischer **/
//BEGIN(crud)
object Project extends Project with LongKeyedMetaMapper[Project] with WithImageMeta[Project] with CRUDify[Long, Project]
//END(crud)
{
  /**Name der genutzten Tabelle in der Datenbank*/
  override def dbTableName = "project"
  /**Anordnung der Eingabefelder in automatisch generierten Formularen (CRUDFify)*/
  override def fieldOrder = List(teaser, creationDate, byUserId)
  /**Name des Menuepunktes für die Ansicht aller Objekte fuer CRUDify-Seiten*/
  override def showAllMenuName = S.?("projects")
  /**Name des Menuepunktes für das Erstellen eines neuen Objekts auf CRUDify-Seiten*/
  override def createMenuName = S.?("create\u0020new\u0020project")

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
class Project extends LongKeyedMapper[Project] with WithImage[Project] with IdPK {
  
      // define WithImage
  
  // override def works, val gives the known nullpointer exception

  override lazy val defaultImage = "/public/images/noproject.jpg"
    
  override lazy val imageDisplayName = "project icon"//S.?("user\u0020image") -> Throws an exception
  
  override lazy val imageDbColumnName = "project_image"
    
  override lazy val baseServingPath = "projectimage"

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

  /**Beschreibt Datenfeld fuer den Kurzbeschreibung eines Projektes*/  
  object teaser extends MappedTextarea(this, 2000){
    /**Genutzter Spaltenname in der DB-Tabelle*/
    override def dbColumnName = "teaser"
    
    /**Name des Datenfeldes für CRUD-Seiten*/
    override def displayName = S.?("project\u0020teaser")
    
    /**Liste der durchzufuehrenden Validationen*/  
    override def validations = FieldValidation.notEmpty(this) _ :: Nil
  }
  
  /**Datumsfeld fuer die Erstellung des Projektes */
  object creationDate extends MappedDateTime(this){
    /**Genutzter Spaltenname in der DB-Tabelle*/
    override def dbColumnName = "creation_date"  
    
    /**Name des Datenfeldes für CRUD-Seiten*/
    override def displayName = S.?("creation\u0020date")
    
    /**Liste der durchzufuehrenden Validationen*/  
    //override def validations =  FieldValidations.isValidDate(this) _ :: Nil
    //override def validSelectValues: Box[List[(Long, String)]] = 
    //	DependencyFactory.inject[Date].map(d => List[(Long,String)] (d.toString(), d.toGMTString()))
    override def defaultValue = Calendar.getInstance.getTime
  }

  /**Liefert das Meta-Objekt zur eigenen Modellklasse.*/
  def getSingleton = Project

  /*
  /**Liefert das Gültig-Bis-Datum als Zeichenkette im Format dd.MM.yyyy */
  def getGermanDateString : String = {
    val sdfGerman: SimpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH); 
    if (this.expirationDate.is != null){
      sdfGerman.format(this.expirationDate.is) 
    }else ""
  }
  * 
  */
  
  /**Liefert alle abonnierten Nachrichten eines Benutzers unter Nutzung eines nativen SQL-Statements.
   *
   * @param id Id des Benutzers
   * @param offset Offset-Wert (Paginierung)
   * @param maxRows Anzahl der zu beschaffenen Nachrichten (Paginierung)
   * @return Liste aller Nachrichten von Lehrkräften, die der Benutzers mit der Id id abonniert hat
   */
  //BEGIN(forSubscriber)
  /*
  def newsBySubscriber(id: Long, offset: Int, maxrows: Int): List[News] =
  News.findAllByPreparedStatement({  superconn => {
    val preparedStatement = superconn.connection.prepareStatement(
	     "SELECT n.* FROM nachricht AS n LEFT JOIN abonnement AS a " +
		   "ON (n.betrifft_person_id = a.nachrichtenquelle_person_id)" +
		   "WHERE a.interessent_person_id = ? ORDER BY n.gueltig_bis DESC LIMIT ?, ?"
    )
    preparedStatement.setInt(1, id.toInt)
    preparedStatement.setInt(2, offset.toInt)
    preparedStatement.setInt(3, maxrows.toInt)
    preparedStatement
  }
  //END(forSubscriber)
     
   /* Gleiches Statement in PostgreSQL Syntax:
  def newsBySubscriber(id : Long, offset: Int, maxrows: Int) : List[News] =
  News.findAllByPreparedStatement({  superconn => {
	   val preparedStatement = superconn.connection.prepareStatement(
	     "SELECT n.* FROM nachricht AS n LEFT JOIN abonnement AS a " +
		   "ON (n.betrifft_person_id = a.nachrichtenquelle_person_id)" +
		   "WHERE a.interessent_person_id = ? ORDER BY n.gueltig_bis DESC LIMIT ? OFFSET ?"
     )
     preparedStatement.setInt(1, id.toInt);
     preparedStatement.setInt(2, offset.toInt);
     preparedStatement.setInt(3, maxrows.toInt);
     preparedStatement;
  }
  */   
     
 })

  
  /**Prüft, ob eine Nachricht von einem Benutzer erstellt wurde
   * @param userId Id des Benutzers
   * @return Angabe, ob die Nachricht vom Benutzer erstellt wurde
   */ 
  def belongsToSubscribed(userId : Long): Boolean = {
    User.find(userId).openOr(User).subscriptions.exists(_.sourceId == this.byUserId)
  }
 
  * 
  */
}