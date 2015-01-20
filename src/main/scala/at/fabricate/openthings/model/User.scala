package at.fabricate.openthings
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.common._
import net.liftweb.http.S
import scala.xml.Node
import scala.xml.Elem
import net.liftweb.http.SHtml
import net.liftweb.http.FileParamHolder
import java.util.Calendar
import net.liftweb.http.SessionVar
import net.liftweb.http.LiftSession
import net.liftweb.http.LiftRules
import scala.xml.NodeSeq
import net.liftweb.http.S.LFuncHolder
import net.liftweb.util.Mailer.From
import net.liftweb.util.Mailer.Subject
import net.liftweb.util.Mailer.To
import net.liftweb.util.Mailer.BCC
import at.fabricate.liftdev.common.model.BaseMetaEntity
import at.fabricate.liftdev.common.model.BaseMetaEntityWithTitleDescriptionAndIcon
import at.fabricate.liftdev.common.model.CustomizeUserHandling
import at.fabricate.liftdev.common.model.BaseEntity
import at.fabricate.liftdev.common.model.BaseEntityWithTitleDescriptionAndIcon
import at.fabricate.liftdev.common.lib.EnumWithDescriptionAndObject
import at.fabricate.liftdev.common.lib.MappedEnumWithDescription
import at.fabricate.liftdev.common.model.EqualityByID
//import net.liftweb.mapper.OneToMany.Owned
//import net.liftweb.mapper.OneToMany.Cascade

/**
 * The singleton that has methods for accessing the database
 */

object User extends User with MetaMegaProtoUser[User] with CustomizeUserHandling[User] with BaseMetaEntity[User] with BaseMetaEntityWithTitleDescriptionAndIcon[User] {
  
  
  // provide a path to a  custom page for the edit feature
  //override lazy val editPath = "designer" :: "edit" :: Nil
  
  override val basePath = "user" :: Nil
    
  override def dbTableName = "users" // define the DB table name
 
  override def screenWrap = Full(
      <lift:surround with="default" at="content">
		  <section class="mainContent standardPage left">
			       <lift:bind />
		  </section>
	  </lift:surround>)
			       
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, icon, firstName, lastName, email,
  locale, timezone, password, description)
  
  // define the order fields will appear in the edit page
  override def editFields = List(icon, firstName, lastName, email,
  locale, timezone, description) 

  // comment this line out to require email validations
//  override def skipEmailValidation = true
  
  // just an idea for different signup process
  override def signupFields = firstName :: lastName :: email :: password :: Nil 
  
  //override def afterCreate = super.afterCreate
  
  // get a link to the user
  def getLinkToUser(userId : Long) : Elem = userId.toString match {
    // First case is needed because a negative user id is matched to the actual user
//    case _ if userId < 0 => <span>User not found!</span>
    case MatchItemByID(theUser) => {
//      .map(_.fullName).openOr("k.A."))
      <a href={ "/designer/%s".format(theUser.id.toString ) }>{ theUser.fullName }</a>
    }
    case _ => <span>User not found!</span>
  }  
  
//  object getProjectsByUser extends FieldOwner(this) {
//    private def getProjectList = Project.findAll(By(Project.byUserId, fieldOwner.id))
//    def asHtml : Elem = <ul><li>Project 1 </li></ul>
//      
//    def short : Elem = <ul>{ getProjectList.map(project => <li>{ project.title }</li>) }</ul>
//  }
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] with BaseEntity[User] with BaseEntityWithTitleDescriptionAndIcon[User] 
with EqualityByID[User] 
with OneToMany[Long, User] 
with ManyToMany
{
  def getSingleton = User // what's the "meta" server
  
   override def firstNameDisplayName = S.?("firstname")
   override def lastNameDisplayName = S.?("lastname")
  
    // define WithImage
  
  // override def works, val gives the known nullpointer exception

  override def defaultIcon = "/public/images/nouser.jpg"
    
  override def iconDisplayName = S.?("user\u0020icon")//S.?("user\u0020image") -> Throws an exception
  
  override def iconDbColumnName = "user_image"
    
  override def iconPath = "userimage"
    
    
  override def maxIconWidth = 1024
  
  override def maxIconHeight = 576
  
  override def applyIconCropping = true
  
  object createdProjects extends MappedOneToMany(Project, Project.createdByUser, OrderBy(Project.primaryKeyField, Descending)) with Owned[Project]

    // a link to all the created Tutorials
//  val mappingToProjects : LongKeyedMetaMapper[Project.TheMapping] = Project.getUserMapper
//  
//  object createdProjects extends MappedManyToMany(mappingToProjects, mappingToProjects.byUser, mappingToProjects.createdItem, Project)

  
  object tools extends MappedManyToMany(UserHasTools, UserHasTools.user, UserHasTools.tool, Tool)
  
  object mailSettingsConst {
    def PUB = <span>public</span>
    def PRIV = <span>private</span>
  }
  	// add email settings for privacy
    object mailSettingsEnum extends EnumWithDescriptionAndObject[Elem] {
    
    
	val pub = Value("Other users can ask me via mail!",mailSettingsConst.PUB)
	val priv = Value("I dont want to get Mails from other users!",mailSettingsConst.PRIV)
//	val average = Value("Average",wrapSpanWithClass("icon-difficulty3"))
//	val advanced = Value("Advanced",wrapSpanWithClass("icon-difficulty4"))
//	val expert = Value("Expert",wrapSpanWithClass("icon-difficulty5"))
//	val genius = Value("Genius",wrapSpanWithClass("icon-difficulty6"))
	}
  
   /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object emailSettings extends MappedEnumWithDescription[Elem,User](this,mailSettingsEnum)
  
  
  
  object personalWebsite extends MappedString(this,100)
  
  
  /*
    /**Datumsfeld fuer die Anmeldung des Users */
  object registrationDate extends MappedDateTime(this){
    /**Genutzter Spaltenname in der DB-Tabelle*/
    override def dbColumnName = "registration_date"  
    
    /**Name des Datenfeldes für CRUD-Seiten*/
    override def displayName = S.?("registration\u0020date")
    
    /**Liste der durchzufuehrenden Validationen*/  
    //override def validations =  FieldValidations.isValidDate(this) _ :: Nil
    //override def validSelectValues: Box[List[(Long, String)]] = 
    //	DependencyFactory.inject[Date].map(d => List[(Long,String)] (d.toString(), d.toGMTString()))
    override def defaultValue = Calendar.getInstance.getTime
  }
  */
  

  
  // 
  def fullName: String = "%s %s".format(this.lastName,this.firstName)
}

