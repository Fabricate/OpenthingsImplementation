

import at.fabricate.openthings.model.Waagproject
import net.liftweb.db.StandardDBVendor
import net.liftweb.util.Props
import net.liftweb.http.LiftRules
import net.liftweb.util.DefaultConnectionIdentifier
import net.liftweb.mapper.DB
import at.fabricate.openthings.model.Waaguser
import com.github.tkqubo.html2md.Html2Markdown
import scala.collection.immutable.Nil
import at.fabricate.openthings.model.Waagprojectfile
import at.fabricate.openthings.model.Waagprojectimage
import at.fabricate.openthings.model.User
import java.util.Locale
import net.liftweb.mapper.By
import net.liftweb.common.Box
import at.fabricate.openthings.model.Project
import net.liftweb.http.FileParamHolder
import net.liftweb.common.Full
import net.liftweb.http.OnDiskFileParamHolder
import java.io.File

object ImportHelper{
  
  def setupDBConnection() = {
          // Set up the database connection
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }
  }
  
  def userExists(eMail : String) : Long =  {
    val found = findUserByMail(eMail)
    if (found.isEmpty) return -1
    else found.open_!.id.get
  }
  
  def findUserByMail(eMail : String) : Box[User] = User.find(By(User.email,eMail))
  
  def createNewUser(username : String, email: String, password: String, language : Locale = Locale.ENGLISH) : User = 
    User.createNewEntity(language, username).email(email).password(password).validated(true).saveMe()    

  def createNewProject(title : String, teaser: String, description: String, creator : User, language : Locale = Locale.ENGLISH) : Project = 
    Project.createNewEntity(Locale.ENGLISH, title, teaser, description).createdByUser(creator).saveMe()
}


object ImportWaagProjects extends App {
  
    ImportHelper.setupDBConnection()
      
    val filePrefix = ""

    var password = ""
    
    var description = "";
    var images : List[Waagprojectimage] = Nil;
    var files : List[Waagprojectfile] = Nil;
    var user : User = null
    var project : Project = null
    var icon : Waagprojectimage = null
    var fileholder : FileParamHolder = null
    var absoluteFilePath = ""
    var file : File = null
    var boxedUser : Box[User]= null
    var teaserMarkdown = ""
    var descriptionMarkdown = ""
      Waaguser.findAll()
      .map { aUser => {
        println("## Processing User: "+aUser.name+" ##") 
        // load the user from the db or create a new one
        boxedUser = ImportHelper.findUserByMail(aUser.mail)
        if (boxedUser.isDefined)
          user = boxedUser.open_!
        else{
          user = ImportHelper.createNewUser(aUser.name,aUser.mail, password)
          
        }
          
          
        // iterate over all projects of the user
        aUser.projects.map { aProject => {
          // delete all arrays and text holders
          description = ""
          images  = Nil
          files  = Nil
          teaserMarkdown = ""
          descriptionMarkdown = ""
          println("### Processing Project: "+aProject.title+" ###")   
          // collect all the data from the project
          description += aProject.body
          aProject.images.map(anImage => {
              //println(" ..Projectimage: "+anImage.filename)              
              absoluteFilePath = filePrefix +"/"+ anImage.filepath
              description += "<img src='"+absoluteFilePath+"' />"
              //images = anImage :: images
              }
              )
          aProject.files.map(aFile =>
              //println(" ..Projectfile: "+afile.filename)
              files = aFile :: files
              )              
          aProject.documents.map(aDocument => {
                //println(" ..Projectdocument: "+aDocument.title)
                description += "<h3>"+aDocument.title+"</h3>"
                aDocument.images.map(anImage =>{
                //println(" ..Projectimage: "+anImage.filename)              
                absoluteFilePath = filePrefix +"/"+ anImage.filepath
                description += "<img src='"+absoluteFilePath+"' />"
                //images = anImage :: images
                }
                )
              aDocument.files.map(aFile =>
                //println(" ...Projectdocumentfile: "+afile.filename)
                  files = aFile :: files
                )  
              description += aDocument.body
               }
              )
              
           // prepare the data to be valid for a project and create a new one
           teaserMarkdown = Html2Markdown.toMarkdown(aProject.teaser)
           if (teaserMarkdown.length() > 500) teaserMarkdown = teaserMarkdown.substring(0, 495)+"..."
           descriptionMarkdown = Html2Markdown.toMarkdown(description)
           project = ImportHelper.createNewProject(aProject.title, 
               teaserMarkdown, 
               descriptionMarkdown, 
               user, 
               Locale.ENGLISH)
           icon = aProject.images.head
           absoluteFilePath = filePrefix +"/"+ icon.filepath
           file = new File(absoluteFilePath)
           if (file.exists()){
             fileholder =  new OnDiskFileParamHolder(icon.filename,icon.filemime,icon.filename, file)
             project.icon.setFromUpload(Full(fileholder))
           } else {
             println("File "+icon.filename+" does not exist for Project "+aProject.title)
           }
          }
        }
        }
      }
}