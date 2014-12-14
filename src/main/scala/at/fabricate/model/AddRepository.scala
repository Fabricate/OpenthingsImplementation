package at.fabricate
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

trait AddRepository [T <: (AddRepository[T] with LongKeyedMapper[T]) ] extends KeyedMapper[Long, T]  {

  self: T =>
    
 	lazy val repository : GitWrapper[T] = new MyGitWrapper(this)
 	
   protected class MyGitWrapper(obj : T) extends GitWrapper(obj) {
     
     override def pathToRepository = fieldOwner.pathToRepository
  
 	  /*
  override def defaultImage = fieldOwner.defaultIcon
    
  override def imageDisplayName = fieldOwner.iconDisplayName
  
  override def imageDbColumnName = fieldOwner.iconDbColumnName
    
  override def baseServingPath = fieldOwner.baseServingPath

  override def fieldId = Some(Text("bin"+imageDbColumnName ))
*/
  }
  
   def pathToRepository : String = "projects/"+primaryKeyField+"/repository/.git"
   // file structure
  // projects 
   // 	- ProjectID
   //		- repository
   //		- previewImages
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

trait AddRepositoryMeta [ModelType <: ( AddRepository[ModelType] with LongKeyedMapper[ModelType]) ] extends KeyedMetaMapper[Long, ModelType]  { //
    self: ModelType  =>

      type TheRepoType = ModelType
   	// maybe afterSave ??, if ID is not available?
	override def afterSave = {
      println("afterSave")
 	  createNewRepo
 	  
 	  super.afterCommit
 	}  
      
    
    /*
    override def create = {
      createNewRepo
      super.create
    }
    * 
    */
    
    override def beforeSave = {
      
      println("beforeSave")
      createNewRepo
      super.beforeCreate
    }
      
    private def createNewRepo = {
      // create a new repository on the filesystem
 	  val newRepo = repository.createNewRepo
 	  println("repository created: "+repository .pathToRepository+"\n"+newRepo.getDirectory().toString())
    }
}

class GitWrapper[T <: (AddRepository[T] with LongKeyedMapper[T]) ](fieldOwner : T) extends FieldOwner[T](fieldOwner) {
  
	// may be overridden in subclasses for other behaviour
	def pathToRepository : String = "repository/.git"
  
	// may be overridden in subclasses for other behaviour
   	//def repositoryID : String = fieldOwner.primaryKeyField.toString
  
	private lazy val repo : Repository = openExistingRepo
	// FileRepo is just one option, maybe put the stuff to the database ??
	
	// TODO: Think about a server Interface where you can push and pull - would be amazing!!
	
	
	private val git : Git = new Git(repo)
 	
   	
   	
  // add a new file to the repo
  def addAFileToTheRepo(filepattern : String) =   git.add.addFilepattern(filepattern).call()

  // commit the repository
  def commit(message : String) = git.commit().call()
  
  // get the file-object of the git repo
  private def getRepoFile : File = new File(pathToRepository ) // +repositoryID
  
    // create a new repository
  def createNewRepo() : Repository = {
	  getRepoFile.getParentFile().mkdirs()
	  getRepoFile.createNewFile()
	  // Path must exist
	  FileRepositoryBuilder.create( getRepoFile )
	}
  
  // open an exiting repository
  private def openExistingRepo() : Repository =  new FileRepositoryBuilder().setGitDir( getRepoFile ).build
  
  // get a list of commits
  def getAllCommits : List[(Long, String)] = List[(Long, String)] ()
  
  // get one special commit
  def getCommit(id : String) = Unit
}


