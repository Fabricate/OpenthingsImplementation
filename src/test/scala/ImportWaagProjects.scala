

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
import net.liftweb.http.InMemFileParamHolder
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Files
import scala.xml._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import at.fabricate.openthings.model.Image
import at.fabricate.liftdev.common.model.StateEnum
import at.fabricate.liftdev.common.model.DifficultyEnum

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
    else found.openOrThrowException("Opened empty Box").id.get
  }
  
  def findUserByMail(eMail : String) : Box[User] = User.find(By(User.email,eMail))
  
  def createNewUser(username : String, email: String, password: String, language : Locale = Locale.ENGLISH) : User = 
    User.createNewEntity(language, username).email(email).password(password).validated(true).saveMe()    

  def createNewProject(title : String, teaser: String, description: String, creator : User, language : Locale = Locale.ENGLISH) : Project = 
    Project.createNewEntity(Locale.ENGLISH, title, teaser, description).createdByUser(creator).saveMe()
    
  def convertStrToXML(html : String): Elem = xml.XML.loadString(html)
  
  def combinePath (filePrefix : String, pathDelimiter: String, filePath : String) : String = filePrefix +pathDelimiter+ filePath
  
  def loadStringToFileParam(content : String, fileName: String, fileMime : String) : Box[FileParamHolder] = {
             val data = content.getBytes;
             Full(new InMemFileParamHolder(fileName,fileMime,fileName, data))
  }
  
  def loadFileToFileParam(absoluteFilePath : String, fileName: String, fileMime : String) : Box[FileParamHolder] = {
           val file = new File(absoluteFilePath)
           if (file.exists() && file.isFile()){
             //fileholder =  new OnDiskFileParamHolder(icon.filename,icon.filemime,icon.filename, file)
             val path = Paths.get(absoluteFilePath);
             val data = Files.readAllBytes(path);
             Full(new InMemFileParamHolder(fileName,fileMime,fileName, data))
           } else {
             Empty
           }
  }
}


object ImportWaagProjects extends App {
  
    ImportHelper.setupDBConnection()
      
    val filePrefix = ""    

    var password = ""
    
    var description = "";
    val dummydescription = "This is a dummy description that will be replaced later on with the correct description content! Stay tuned!"
    var images : List[Waagprojectimage] = Nil;
    var files : List[Waagprojectfile] = Nil;
    var user : User = null
    var project : Project = null
    var icon : Waagprojectimage = null
    var fileBox : Box[FileParamHolder] = Empty
    var boxedUser : Box[User]= Empty
    var teaserPlaintext = ""
    var descriptionMarkdown = ""
    val markdownConverter = new LiftMarkdownConverter()
    val plaintextConverter = new PlaintextConverter()
      Waaguser.findAll()
      .map { aUser => {
        println("## Processing User: "+aUser.name+" ##") 
        // load the user from the db or create a new one
        boxedUser = ImportHelper.findUserByMail(aUser.mail.get)
        if (boxedUser.isDefined)
          user = boxedUser.openOrThrowException("Opened empty Box")
        else{
          user = ImportHelper.createNewUser(aUser.name.get,aUser.mail.get, password)
          val userIcon = aUser.picture
          fileBox = ImportHelper.loadFileToFileParam(ImportHelper.combinePath(filePrefix, "/", userIcon.get), userIcon.get, "image/jpg") 
          if (fileBox.isDefined){
             user.icon.setFromUpload(fileBox)
             user.saveMe()
           } else {
             println("file "+userIcon+" does not exist")
           }
        }
          
          
        // iterate over all projects of the user
        aUser.projects.map { aProject => {
          // delete all arrays and text holders
          description = ""
          images  = Nil
          files  = Nil
          teaserPlaintext = ""
          descriptionMarkdown = ""
          println("### Processing Project: "+aProject.title+" ###")   
          // collect all the data from the project
          description += aProject.body
          aProject.images.map(anImage => {
              //println(" ..Projectimage: "+anImage.filename)              
              description += "<img src='"+anImage.filepath+"' />"
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
                description += "<img src='"+anImage.filepath+"' />"
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
           teaserPlaintext = Html2Markdown.toMarkdown(aProject.teaser.get,plaintextConverter)
           if (teaserPlaintext.length() > 500) teaserPlaintext = teaserPlaintext.substring(0, 495)+"..."
           val title = if (aProject.title.get.length() > 60) aProject.title.get.substring(0, 56)+"..."
           else aProject.title.get
           project = ImportHelper.createNewProject(title, 
               teaserPlaintext, 
               dummydescription, 
               user, 
               Locale.ENGLISH)
           project.save()
           // initialize repo
          project.repository.getStatus()
           // convert teaser to markdown
                      // analyze all images
           try {
           val descriptionElem = ImportHelper.convertStrToXML("<?xml version=\"1.0\"?><!DOCTYPE some_name [<!ENTITY nbsp \"&#160;\">]> <myDescription>"+description+"</myDescription>") 
           val modifiedDescriptionElem = (                             
               "img [src]" #> ((n: NodeSeq) => {
                val location = ImportHelper.combinePath(filePrefix, "/", n.\@("src"))
                val name = new File(location).getName
                //println(n.toString())
                //println(location)
                fileBox = ImportHelper.loadFileToFileParam(location, name, "image/jpeg") 
                if (fileBox.isDefined){
                  val image = project.addNewImageToItem(Locale.ENGLISH, name , name)
                  Image.setImageFromFileHolder(fileBox.openOrThrowException("Opened empty Box"), image)
                  project.saveMe()
                  "/serve/image/"+image.id
                } else {
                  println("Image:  "+name+" at "+location+" does not exist for Project "+aProject.title)                  
                  "#"
                }
                
              /*
                fileBox = ImportHelper.loadFileToFileParam(absoluteFilePath, "/", userIcon, userIcon, "image/jpg") 
               if (fileBox.isDefined){
                 user.icon.setFromUpload(fileBox)
                 user.saveMe()
               } else {
                 println("file "+absoluteFilePath+" does not exist")
               }
               * 
               */
             } ) &
              "myDescription ^*" #> ((n: NodeSeq) => {
             //lift the content of the new myDescription tag to the top again
             n
             } ) 
           ).apply(descriptionElem)           
           descriptionMarkdown = Html2Markdown.toMarkdown(modifiedDescriptionElem.toString(),markdownConverter)
           
           project.defaultTranslation.obj.map { translation => translation.description(descriptionMarkdown).save() }
           } catch  {
             case (e : SAXParseException) => {
               println("no well formed xml at description for project "+aProject.title+" failed")
               fileBox = ImportHelper.loadStringToFileParam(description,"description.txt","text/plain")
               project.repository.copyFileToRepository(fileBox.openOrThrowException("Opened empty Box"))
               fileBox = ImportHelper.loadStringToFileParam(e.getMessage,"errormessage.txt","text/plain")
               project.repository.copyFileToRepository(fileBox.openOrThrowException("Opened empty Box"))
               project.saveMe()
             }
             case (e : Exception) => {
               println("converting description for project "+aProject.title+" failed")
             }
           }
           /*
           (descriptionElem  "img").foreach{ imgTag =>
              println((imgTag  "@src").text + "n")
             
           }
           * 
           */
           icon = aProject.images.head
           fileBox = ImportHelper.loadFileToFileParam(ImportHelper.combinePath(filePrefix, "/", icon.filepath.get), icon.filename.get, icon.filemime.get) 
               if (fileBox.isDefined){
                 try {
                 project.icon.setFromUpload(fileBox)
                 project.saveMe()
                 } catch {
                   case e: Exception => println("Project Icon:  "+icon.filename+" at "+ImportHelper.combinePath(filePrefix, "/", icon.filepath.get)+" upload resulted in an error for Project "+aProject.title)
                 }
               } else {
                 println("Project Icon:  "+icon.filename+" at "+ImportHelper.combinePath(filePrefix, "/", icon.filepath.get)+" does not exist for Project "+aProject.title)
               }

          }
          files.map { aFile => {
               val fileLocation = ImportHelper.combinePath(filePrefix, "/", aFile.filepath.get)
               fileBox = ImportHelper.loadFileToFileParam(fileLocation, aFile.filename.get, aFile.filemime.get) 
               println("proceccing Project File "+ aFile.filename+" at "+fileLocation  )
               if (fileBox.isDefined){
                 project.repository.copyFileToRepository(fileBox.openOrThrowException("Opened empty Box"))
                 project.saveMe()
               } else {
                 println("Project File:  "+aFile.filename+" at "+ImportHelper.combinePath(filePrefix, "/", aFile.filepath.get)+" does not exist for Project "+aProject.title)
               }
             }
          }
          project.repository.commit("Initial project import!", "Martin Risseeuw", "martin@martinr.nl")
          project.state.set(StateEnum.undefined) // do unknown here
          project.difficulty.set(DifficultyEnum.unknown) // do unknown here
          project.save();
        }
        }
      }
}