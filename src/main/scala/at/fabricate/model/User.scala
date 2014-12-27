package at.fabricate
package model

import net.liftweb.mapper._
import net.liftweb.util._
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


/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] with RedirectAfterLogin[User] with LongKeyedMetaMapper[User] with AddIconMeta[User] with CreatedUpdated {
  
  
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
	  
//  override def homePage = "/"
    /*
   override def signupXhtml(user: TheUserType) = {
	(<form method="post" action={S.uri}><table><tr><td
colspan="2">{ S.??("sign.up") }</td></tr>
	{localForm(user, false, signupFields)}
<tr><td>&nbsp;</td><td><user:submit/></td></tr>
</table></form>)
	}
   override def loginXhtml = {
	(<form method="post" action={S.uri}><table><tr><td
colspan="2">{S.??("log.in")}</td></tr>
<tr><td>{userNameFieldString}</td><td><user:email /></td></tr>
<tr><td>{S.??("password")}</td><td><user:password /></td></tr>
<tr><td><a href={lostPasswordPath.mkString("/", "/", "")}
>{S.??("recover.password")}</a></td><td><user:submit /></td></tr></table>
</form>)
	}
   override  def lostPasswordXhtml = {
	(<form method="post" action={S.uri}>
<table><tr><td
colspan="2">{S.??("enter.email")}</td></tr>
<tr><td>{userNameFieldString}</td><td><user:email /></td></tr>
<tr><td>&nbsp;</td><td><user:submit /></td></tr>
</table>
</form>)
	}
   override def passwordResetXhtml = {
	(<form method="post" action={S.uri}>
<table><tr><td colspan="2">{S.??("reset.your.password")}</td></tr>
<tr><td>{S.??("enter.your.new.password")}</td><td><user:pwd/></td></tr>
<tr><td>{S.??("repeat.your.new.password")}</td><td><user:pwd/></td></tr>
<tr><td>&nbsp;</td><td><user:submit/></td></tr>
</table>
</form>)
	}
    def changePasswordXhtml = {
	(<form method="post" action={S.uri}>
<table><tr><td colspan="2">{S.??("change.password")}</td></tr>
<tr><td>{S.??("old.password")}</td><td><user:old_pwd /></td></tr>
<tr><td>{S.??("new.password")}</td><td><user:new_pwd /></td></tr>
<tr><td>{S.??("repeat.password")}</td><td><user:new_pwd /></td></tr>
<tr><td>&nbsp;</td><td><user:submit /></td></tr>
</table>
</form>)
	}
//    net.liftweb.http.Templates.findAnyTemplate("modules"::"user"::Nil) match {
//    case Full(template) => new Elem(template.theSeq.head)
//  }
  //Finder.findAnyTemplate("modules"::""::Nil) openOr(super.loginXhtml)
//    LiftSession.f
 *    
 */
			       
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, icon, firstName, lastName, email,
  locale, timezone, password, aboutMe)
  
  // define the order fields will appear in the edit page
  override def editFields = List(icon, firstName, lastName, email,
  locale, timezone, aboutMe) 

  // comment this line out to require email validations
  override def skipEmailValidation = true
  
  // just an idea for different signup process
  override def signupFields = firstName :: lastName :: email :: password :: Nil 
  
  //override def afterCreate = super.afterCreate
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] with LongKeyedMapper[User] with MatchByID[User] with AddIcon[User] with CreatedUpdated with OneToMany[Long,User] with ManyToMany {
  def getSingleton = User // what's the "meta" server
  
   override def firstNameDisplayName = S.?("firstname")
   override def lastNameDisplayName = S.?("lastname")
  
    // define WithImage
  
  // override def works, val gives the known nullpointer exception

  override def defaultIcon = "/public/images/nouser.jpg"
    
  override def iconDisplayName = S.?("user\u0020icon")//S.?("user\u0020image") -> Throws an exception
  
  override def iconDbColumnName = "user_image"
    
  override def baseServingPath = "userimage"
  
  //object image extends MappedBinaryImageFileUpload(this)
  
  // define an additional field for a personal essay
  object aboutMe extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    // TODO  implement later, as Crudify and Megaprotouser can not be mixed in at the same time
    override def displayName = S.?("about\u0020me")
    	  /**Genutzter Spaltenname in der DB-Tabelle*/
    override def dbColumnName = "about_me"
  }
  
  object tools extends MappedManyToMany(UserHasTools, UserHasTools.user, UserHasTools.tool, Tool)
  
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
  
  // necessary to remove duplicate elements from lists
  
  override def equals(other:Any) = other match {
    case u:User if u.id.get == this.id.get => true
    case _ => false
  }
  
  override def hashCode = this.id.get.hashCode
  
  // 
  def fullName: String = "%s %s".format(this.lastName,this.firstName)
}

