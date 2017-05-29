package at.fabricate.openthings
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.common._
import net.liftweb.http.S
import scala.collection.mutable
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
import at.fabricate.liftdev.common.model._
import at.fabricate.liftdev.common.lib.EnumWithDescriptionAndObject
import at.fabricate.liftdev.common.lib.MappedEnumWithDescription
import at.fabricate.openthings.snippet.ProjectSnippet
import java.util.Locale
import at.fabricate.liftdev.common.lib.FieldValidation

object User extends User with MetaMegaProtoUser[User] with CustomizeUserHandling[User] with BaseMetaEntity[User] with BaseMetaEntityWithTitleDescriptionAndIcon[User] 
with AddSkillsMeta[User] {



  override val basePath = "user" :: Nil

  override def dbTableName = "user" // define the DB table name

  override def screenWrap = Full(
      <lift:surround with="default" at="content">
		  <section class="mainContent standardPage left">
			       <lift:bind />
		  </section>
	  </lift:surround>)

  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, icon, firstName, lastName, email,
  locale, timezone, password)//, description)

  // define the order fields will appear in the edit page
  override def editFields = List(icon, firstName, lastName, email,
  locale, timezone)

  // just an idea for different signup process
  override def signupFields = firstName :: lastName :: email :: password :: Nil

	// comment this line out to require email validations
	//override def skipEmailValidation = true
  
  // get a link to the user
  def getLinkToUser(userId : Long) : Elem = userId.toString match {
    // First case is needed because a negative user id is matched to the actual user
    case MatchItemByID(theUser) => {
      <a href={ "/designer/%s".format(theUser.id.toString ) }>{ theUser.fullName }</a>
    }
    case _ => <span>User not found!</span>
  }


  // some basic user rights management
  def canEditContent[T <: Mapper[T]](item : T) : Boolean = loggedIn_? && currentUser.openOrThrowException("Empty Box opened").permission == permissionsEnum.user
  def canModerateContent[T <: Mapper[T]](item : T) : Boolean = loggedIn_? && currentUser.openOrThrowException("Empty Box opened").permission == permissionsEnum.moderator
  def canAdministerContent[T <: Mapper[T]](item : T) : Boolean = loggedIn_? && currentUser.openOrThrowException("Empty Box opened").permission == permissionsEnum.administrator

  def canEditProject [T <: BaseEntityWithTitleDescriptionIconAndCommonFields[T]](item : T) : Boolean = canEditContent(item) && item.createdByUser.get == currentUser.openOrThrowException("Empty Box opened").id.get
  
  override def capturePreLoginState() = {
    val unsavedContents = ProjectSnippet.unsavedContent.get // is the SessionVar

    () => {
      // just save it here
      unsavedContents.map(content => {
        content match {
          case aproject : Project => {
            if (aproject.createdByUser.get > -1)
              ProjectSnippet.unsavedContent.set(Full(aproject))
            else
              currentUser.map(aUser => {
                ProjectSnippet.unsavedContent.set(Full(aproject.createdByUser(aUser)))
              })
          }
          case _ => println("unsaved content cannot be casted to a project!")
        }
      }
        )
    }
  }

  // applying create will crash the system - this is the first thing to set
  override def createNewUserInstance = createNewEntity(Locale.ENGLISH)

}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] with BaseEntity[User] with BaseEntityWithTitleDescriptionAndIcon[User]
with EqualityByID[User]
with OneToMany[Long, User]
with AddSkills[User]
with ManyToMany
{
    // important: user has to be saveable without login (for register, ...)
  override val userHasToBeLoggedInForSave = false

  // redefine the validations
  // TODO: there is a bug, that title is always less than 5 characters
  override val titleValidations : mutable.MutableList[String => List[FieldError]] = mutable.MutableList(FieldValidation.maxLength(TheTranslationMeta.title,titleLength) _ )

  // no minimumlenght for teaser, as it is not available on register!
  override val teaserValidations : mutable.MutableList[String => List[FieldError]] = mutable.MutableList( FieldValidation.maxLength(TheTranslationMeta.teaser,teaserLength) _ )

  // no minimumlenght for description, as it is not available on register!
  override val descriptionValidations : mutable.MutableList[String => List[FieldError]] = mutable.MutableList(FieldValidation.maxLength(TheTranslationMeta.description,descriptionLength) _ )


  // definitions for AddSkill
  type TheSkillType = Skill
  def theSkillObject = Skill

  def getSingleton = User // what's the "meta" server

   override def firstNameDisplayName = S.?("firstname")
   override def lastNameDisplayName = S.?("lastname")

    // define WithImage

  // override def works, val gives the known nullpointer exception

  override def defaultIcon = "/public/images/nouser.jpg"

  override def iconDisplayName = S.?("user\u0020icon")

  override def iconDbColumnName = "user_image"

  override def iconPath = "userimage"



  override def maxIconWidth = 1024

  override def maxIconHeight = 576

  override def applyIconCropping = true

  object permissionsEnum extends Enumeration{
    val banned = Value(0,"Banned")
    val user = Value(1,"User")
    val moderator = Value(2,"Moderator")
    val administrator = Value(3,"Administrator")
  }

  object permission extends MappedEnum(this,permissionsEnum){
    override  def defaultValue = permissionsEnum.user
    //override def dbNotNull_? = true
  }

  object createdProjects extends MappedOneToMany(Project, Project.createdByUser, OrderBy(Project.primaryKeyField, Descending)) with Owned[Project]

  object tools extends MappedManyToMany(UserHasTools, UserHasTools.user, UserHasTools.tool, Tool)

  object mailSettingsConst {
    def PUB = <span>public</span>
    def PRIV = <span>private</span>
  }
  	// add email settings for privacy
    object mailSettingsEnum extends EnumWithDescriptionAndObject[Elem] {

	val pub = Value("Other users can ask me via mail!",mailSettingsConst.PUB)
	val priv = Value("I dont want to get Mails from other users!",mailSettingsConst.PRIV)

	}

   /**Beschreibt Datenfeld für den Ersteller eines Projektes als Fremdschluessel fuer Relation zu User-Objekten*/
  object emailSettings extends MappedEnumWithDescription[Elem,User](this,mailSettingsEnum)



  object personalWebsite extends MappedString(this,100)



  def fullName: String = "%s %s".format(this.lastName,this.firstName)
}
