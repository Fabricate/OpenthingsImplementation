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
import at.fabricate.lib.{EnumWithKeyAndValue, EnumWithStringKeyAndValue, EnumWithDescriptionAndObject }
 
/**Meta(Kompagnion)-Objekt für die Projekt-Klasse. Enthaelt instanzuebergreifende Einstellungen.
* @author Johannes Fischer **/

object Project extends Project with BaseRichEntityMeta[Project] with AddRepositoryMeta[Project] {
  
  

  /**Name der genutzten Tabelle in der Datenbank*/
//  override def dbTableName = "project"
  /**Anordnung der Eingabefelder in automatisch generierten Formularen (CRUDFify)*/
//  override def fieldOrder = List(teaser, creationDate, byUserId)

}


/**Beschreibt eine Projekt-Instanz
* @author Johannes Fischer **/
class Project extends BaseRichEntity[Project] with AddRepository[Project] {


  override def defaultIcon = "/public/images/noproject.jpg"
    
  override def iconDisplayName = S.?("project\u0020icon")//S.?("user\u0020image") -> Throws an exception
  
  override def iconDbColumnName = "project_image"
    
  override def baseServingPath = "projectimage"

//      object createdByUser extends MappedManyToMany(self,){ }
    
  /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object byUserId extends MappedLongForeignKey(this, User){

    override def defaultValue = User.currentUser.map(_.id.get ) openOr(-2)
    
	  /**Genutzter Spaltenname in der DB-Tabelle*/
    override def dbColumnName = "project_initiator"
    
    /**Name des Datenfeldes für CRUD-Seiten*/
    override def displayName = S.?("project\u0020initiator")
    
      
    /**Darstellung des Feldes auf CRUD-  object createdByUser extends MappedManyToMany(self,){
    
  }Seiten. Anstelle der Id wird Nachname und Vorname des Autors
     * angezeigt bzw. "k.A." für "keine Angabe", wenn es zu dieser User-Id keinen User gibt. */
    override def asHtml = User.getLinkToUser(get)
    
  }

  /*
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