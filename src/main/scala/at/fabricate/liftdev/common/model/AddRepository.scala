package at.fabricate.liftdev.common
package model

import org.eclipse.jgit.lib._
import org.eclipse.jgit.api._
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import net.liftweb.mapper.KeyedMapper
import net.liftweb.mapper.KeyedMetaMapper
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.MappedString
import scala.xml.Text
import net.liftweb.http.S
import net.liftweb.common.Box
import net.liftweb.common.Full
import net.liftweb.mapper.MetaMapper
import net.liftweb.http.LiftRules
import java.net.URL
import java.net.URISyntaxException
import net.liftweb.common.Empty
import net.liftweb.mapper.MappedBoolean
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.dircache.DirCache
import net.liftweb.http.FileParamHolder
import java.io.FileOutputStream
import java.io.FileFilter
import scala.collection.JavaConversions._
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry
import java.io.FileInputStream
import java.io.BufferedInputStream
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.OkResponse
import net.liftweb.http.BadResponse
import lib.MatchString
import net.liftweb.util.Helpers._
import lib.GitWrapper

trait AddRepository [T <: (AddRepository[T] with MatchByID[T]) ] extends BaseEntity[T]  {

  self: T =>
    

      // defines the path for uploading files to the rest api
      // example path : URL_TO_THE_SERVER/api/upload/repository    
      def apiPath : String = "api"
        
      def uploadPath : String = "upload"
        
      def repositoryPath : String = "repository"
        
      // defines the path for uploading files to the rest api
      // example path : webapp/items/upload/repository         	    
 	   def basePathToRepository : String = "items"
 	     
 	   def endPathToRepository : String = "repository"
 	     
 	   def endPathToData : String = "data"
 	   
 	   def repositoryID : String = primaryKeyField.get.toString
 	   
 	   def initRepoMessage = "Initialized repository!"
 	     
 	   def defaultCommitterName = "openthings"
	   def defaultCommitterMail = "openthings@openthings.openthings"

    
 	lazy val repository : GitWrapper[T] = new MyGitWrapper(this)
 	
   protected class MyGitWrapper(obj : T) extends GitWrapper(obj) {
        
       private val webappRootURL : URL = LiftRules.getResource("/") .openOrThrowException("Webapp Root not found!") 

       private val webappRoot = webappRootURL.getPath() // TODO : maybe use urlDecode to convert the "%20" to " "
       
       private val basePathToRepository = fieldOwner.basePathToRepository
 	   private val endPathToRepository = fieldOwner.endPathToRepository
 	   private val endPathToData = fieldOwner.endPathToData
 	  
 	 override def basePathToItemFolder = webappRoot + basePathToRepository + 
     File.separator + repositoryID
     
     override def pathToRepository =  basePathToItemFolder + 
     File.separator + endPathToRepository//fieldOwner.pathToRepository
     
     override def pathToData =  basePathToItemFolder + 
     File.separator + endPathToData//fieldOwner.pathToRepository
  
     def repositoryID : String = fieldOwner.repositoryID
     
     override def initRepoMessage = fieldOwner.initRepoMessage
     
     override def defaultCommitterName = fieldOwner .defaultCommitterName
	 override def defaultCommitterMail = fieldOwner .defaultCommitterMail
       
//     override def urlToRepo = 
//       
//     override def urlToData = 
       
 	  /*
  override def defaultImage = fieldOwner.defaultIcon
    
  override def imageDisplayName = fieldOwner.iconDisplayName
  
  override def imageDbColumnName = fieldOwner.iconDbColumnName
    
  override def baseServingPath = fieldOwner.baseServingPath

  override def fieldId = Some(Text("bin"+imageDbColumnName ))
*/
  }
 	/*
 	def basePath : String = { // //LiftRules.context.ctx.getRealPath("/")
 	   			   println(LiftRules.calculateContextPath.toString)
//LiftRules.excludePathFromContextPathRewriting
 	   			   
 	  if (!LiftRules.context.path.endsWith(File.separator) )
 			 LiftRules.context.path+File.separator
 			 else
 			   LiftRules.context.path
 			   
 	}
 	* 
 	*/

  
// items/"+primaryKeyField+"/repository/.git
   /*
	def pathToRepository(repositoryID : String) : String = {
    val url = base.getPath+"items/"+repositoryID+"/repository/.git"
     println("URL: "+url)
     println("LiftCoreResourceName: "+LiftRules.liftCoreResourceName)
     println("ResourceServePath: "+LiftRules.resourceServerPath)
     url
   }
   * 
   */
     //basePath+"items/"+primaryKeyField+"/repository/.git"
   // file structure (where to put the zip file?)
  // items 
   // 	- <ItemID>/
   //		- <ItemID>.zip
   //		- repository/
   //		- previewImages/
	//	   		- <maybe> commitID
   //				- images
   //		- previewData
	//	   		- <maybe> commitID
   //				- previewData (subsampled STL for Example)

   lazy val commitToUse : MappedString[T] = new MyCommitIDToUse(this, 32)

  protected class MyCommitIDToUse(obj: T, size: Int) extends MappedString(obj, size) {
    override def displayName = fieldOwner.commitIDToUseName
    override val fieldId = Some(Text("txtRepoCommitID"))
    
//   setting to private did not work
//    override def set(value : String) = super.set(value)
    
    /*
        /**Namen-Auswahlliste für CRUD-Seiten*/
    override def validSelectValues: Box[List[(Long, String)]] = {
      fieldOwner.repository.getAllCommits
      val currentUser : List[User] = User.currentUser.toList
      Full(currentUser.map(u => (u.id.get, u.lastName.get) ) )
    }
    * 
    */
  }
   
   def commitIDToUseName = S.?("version")
  
}

trait AddRepositoryMeta [ModelType <: ( AddRepository[ModelType] with MatchByID[ModelType]) ] extends BaseMetaEntity[ModelType]  { //
    self: ModelType  =>

      type TheRepoType = ModelType
      
   	// maybe afterSave ??, if ID is not available?
      // -> ID is not available, even after save or commit!!!!
      
	/*override def afterSave = {
      println("afterSave")
      println("id:"+this.primaryKeyField.get)
 	  createNewRepo
 	  
 	  super.afterSave
 	}  
 	*  
      override def afterCommit = {
      println("afterCommit")
      println("id:"+this.primaryKeyField.get)
 	  createNewRepo
 	  
 	  super.afterCommit
 	} 
 	*/
    
    /*
    override def create = {
      createNewRepo
      super.create
    }
    * 
    */
    
   /* override def beforeSave = {
      
      println("beforeSave")
      createNewRepo
      super.beforeCreate
    }
    
      
    private def createNewRepo = {
      // create a new repository on the filesystem
 	  val newRepo = repository.createNewRepo
 	  println("repository created: "+repository .pathToRepository+"\n"+newRepo.getDirectory().toString())
    }
    
    * 
    */
//        Not needed - matching does not work and simple strings (should) work fine
//      object MatchAPIPath extends MatchString(apiPath)
//      
//      object MatchUploadPath extends MatchString(uploadPath)
//      
//      object MatchRepositoryPath extends MatchString(repositoryPath)

      
      
      abstract override def init : Unit = {
//        serve ( "simple5" / "item" prefix {
//// all the inventory
//case Nil JsonGet _ => Item.inventoryItems: JValue
//case Nil XmlGet _ => Item.inventoryItems: Node
//55
//56
//57
//58
//59
//// a particular item
//case Item(item) :: Nil JsonGet _ => item: JValue
//case Item(item) :: Nil XmlGet _ => item: Node
//})

        object FileUploadREST extends RestHelper {

		  serve ( apiPath / uploadPath / repositoryPath prefix {
		
		    case MatchItemByID(item) :: Nil Post req =>
		      for (file <- req.uploadedFiles) {
		        println("Received: "+file.fileName)
		        // copy the file to the repository
		        item.repository.copyFileToRepository(file)
		      }
		      OkResponse()
		      
		    case any :: Nil Post req => {
		      println("upload file :: id is not matching a item")
		      println(any)
		      BadResponse()
		    }
		    
		    case Nil Post req => {
		      println("upload file :: item id missing")
		      BadResponse()
		    }
		
		  })
		
		}
        LiftRules.dispatch.append(FileUploadREST)
        super.init
      }
}




