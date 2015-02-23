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

       private val webappRoot = webappRootURL.getPath()
       
       private val basePathToRepository = fieldOwner.basePathToRepository
 	   private val endPathToRepository = fieldOwner.endPathToRepository
 	   private val endPathToData = fieldOwner.endPathToData
 	  
 	 override def basePathToItemFolder = webappRoot + basePathToRepository + 
     File.separator + repositoryID
     
     override def pathToRepository =  basePathToItemFolder + 
     File.separator + endPathToRepository
     
     override def pathToData =  basePathToItemFolder + 
     File.separator + endPathToData
  
     def repositoryID : String = fieldOwner.repositoryID
     
     override def initRepoMessage = fieldOwner.initRepoMessage
     
     override def defaultCommitterName = fieldOwner .defaultCommitterName
	 override def defaultCommitterMail = fieldOwner .defaultCommitterMail
       

  }


   lazy val commitToUse : MappedString[T] = new MyCommitIDToUse(this, 32)

  protected class MyCommitIDToUse(obj: T, size: Int) extends MappedString(obj, size) {
    override def displayName = fieldOwner.commitIDToUseName
    override val fieldId = Some(Text("txtRepoCommitID"))
    
  }
   
   def commitIDToUseName = S.?("version")
  
}

trait AddRepositoryMeta [ModelType <: ( AddRepository[ModelType] with MatchByID[ModelType]) ] extends BaseMetaEntity[ModelType]  { //
    self: ModelType  =>

      type TheRepoType = ModelType
      

      
      
      abstract override def init : Unit = {

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




