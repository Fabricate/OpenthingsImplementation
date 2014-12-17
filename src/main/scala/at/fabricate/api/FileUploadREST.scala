package at.fabricate
package api

import net.liftweb.http.rest.RestHelper
import net.liftweb.http._
import at.fabricate.model.ObjectById
import at.fabricate.model.Project
import java.io.File
import java.io.FileOutputStream

object FileUploadREST extends RestHelper {
  
  //object TheProject extends ObjectById[Project](Project) 


  serve {

    case "api" ::"upload" ::"file" :: Project.FindByID(project) :: Nil Post req =>
      for (file <- req.uploadedFiles) {
        println("Received: "+file.fileName)
        
        // copy the file to the repository

        project.repository.copyAndAddFileToRepository(file)


      }
      OkResponse()
      
    case "api" ::"upload" ::"file" :: any :: Nil Post req => {
      println("upload file :: id is not matching a Project")
      println(any)
      BadResponse()
    }
    
    case "api" ::"upload" ::"file" :: Nil Post req => {
      println("upload file :: project id missing")
      BadResponse()
    }

    //case _ Post req => BadResponse()
  }

}