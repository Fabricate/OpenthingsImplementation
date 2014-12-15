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
import net.liftweb.http.LiftRules
import java.net.URL
import java.net.URISyntaxException
import net.liftweb.common.Empty
import net.liftweb.mapper.MappedBoolean

trait AddRepository [T <: (AddRepository[T] with LongKeyedMapper[T]) ] extends KeyedMapper[Long, T]  {

  self: T =>
    
 	lazy val repository : GitWrapper[T] = new MyGitWrapper(this)
 	
   protected class MyGitWrapper(obj : T) extends GitWrapper(obj) {
     
     override def pathToRepository = webappRoot + basePathToRepository + 
     File.separator + repositoryID + 
     File.separator + endPathToRepository//fieldOwner.pathToRepository
  
     override def repositoryID = //{
       fieldOwner.asInstanceOf[LongKeyedMapper[T]].primaryKeyField.get.toString
       
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
 	    
 	   def basePathToRepository : String = "projects"
 	   def endPathToRepository : String = "repository"+File.separator+".git"
  
// projects/"+primaryKeyField+"/repository/.git
   /*
	def pathToRepository(repositoryID : String) : String = {
    val url = base.getPath+"projects/"+repositoryID+"/repository/.git"
     println("URL: "+url)
     println("LiftCoreResourceName: "+LiftRules.liftCoreResourceName)
     println("ResourceServePath: "+LiftRules.resourceServerPath)
     url
   }
   * 
   */
     //basePath+"projects/"+primaryKeyField+"/repository/.git"
   // file structure (where to put the zip file?)
  // projects 
   // 	- <ProjectID>/
   //		- <ProjectID>.zip
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

trait AddRepositoryMeta [ModelType <: ( AddRepository[ModelType] with LongKeyedMapper[ModelType]) ] extends KeyedMetaMapper[Long, ModelType]  { //
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
    * 
    */
      
    private def createNewRepo = {
      // create a new repository on the filesystem
 	  val newRepo = repository.createNewRepo
 	  println("repository created: "+repository .pathToRepository+"\n"+newRepo.getDirectory().toString())
    }
}

class GitWrapper[T <: (AddRepository[T] with LongKeyedMapper[T]) ](owner : T) extends MappedBoolean[T](owner) {
  
    val webappRoot = LiftRules.getResource("/") .openOrThrowException("Webapp Root not found!") .getPath
    
	// may be overridden in subclasses for other behaviour
    // repositoryID might be used as well
	def pathToRepository : String = webappRoot+"repository/"+repositoryID+"/.git"
	
	// default path if the ID is not set
	def pathToDefaultRepository : String = webappRoot+"DefaultRepository/.git"
  
	// may be overridden in subclasses for other behaviour
   	def repositoryID : String = fieldOwner.asInstanceOf[LongKeyedMapper[T]].primaryKeyField.get.toString
  
	private def repo : Repository = {
      if (isIDSet) {
	      if (existsdOnFilesystem) {
	        println ("opened repo for project "+repositoryID)
	        openExistingRepo
	      }
	      else {
	        println("created new repo for project "+repositoryID)
	        set(true)
	        fieldOwner.save
	        //fieldOwner.repository.s
	        createNewRepo
	      }
      } else {
        println("primary key open")
        // throw an exception here?
        new FileRepositoryBuilder().setGitDir( new File(pathToDefaultRepository)).build
      }
    }
	// FileRepo is just one option, maybe put the stuff to the database ??
	
    
    def getRepo : Repository = repo
    
	// TODO: Think about a server Interface where you can push and pull - would be amazing!!
	
	
	private def git : Git = new Git(repo)
 	
    // boolean value of the entity represents the status on the filesystem
    def existsdOnFilesystem = get
    
    def isIDSet : Boolean = 
    if (fieldOwner.asInstanceOf[LongKeyedMapper[T]].primaryKeyField != Empty && fieldOwner.asInstanceOf[LongKeyedMapper[T]].primaryKeyField != -1 )
    	 true
       else
         false
     
   	
  // add a new file to the repo
  def addAFileToTheRepo(filepattern : String) =   git.add.addFilepattern(filepattern).call()
  
  def initialCommit = {
         Git.init().setDirectory(repo.getDirectory().getParentFile()).call()
         commit("Repository initialized")
       }

  // commit the repository
  def commit(message : String) = git.commit().setMessage(message).call()
  
  // get the file-object of the git repo
  //private def getRepoFile : File = new File(pathToRepository )
    /*
   {
         if (pathToRepository.endsWith(File.separator))
        	 new File(pathToRepository ) // +repositoryID
         else 
           new File(pathToRepository+File.separator )
       }
       * 
       */
  
    // create a new repository
  def createNewRepo() : Repository = {
      val repoConfigFile =   new File(pathToRepository + File.separator + "config" )
      val repoDir = new File(pathToRepository)
	  repoConfigFile.getParentFile().mkdirs()
	  //getRepoFile.createNewFile()
	  // Path must exist
	  Git.init().setDirectory(repoDir.getParentFile()).call()	  
	  val repo = FileRepositoryBuilder.create(  new File(pathToRepository) )
	  
	  println("repo dir: "+repo.getDirectory())
	  // dont do that - endless call!!!
	  //initialCommit
	  repo.close()
	  
	  openExistingRepo
	}
  
  // open an exiting repository
  private def openExistingRepo() : Repository =  new FileRepositoryBuilder().
  setGitDir( new File(pathToRepository ) ).
  readEnvironment.
  findGitDir.
  build
  
  // get a list of commits
  def getAllCommits : List[(Long, String)] = List[(Long, String)] ()
  
  // get one special commit
  def getCommit(id : String) = Unit
  
  private def linkToRepo(linkText : String) = <a href={"/project/repository/"+fieldOwner.primaryKeyField.get}>{linkText}</a>
  
  override def asHtml = linkToRepo("view the repository") // TODO: plus additionally a download link for the zip

  override def toForm = Full(linkToRepo("edit the repository"))
}


