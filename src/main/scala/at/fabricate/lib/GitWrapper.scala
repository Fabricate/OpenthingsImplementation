package at.fabricate
package lib

import net.liftweb.mapper.MappedBoolean
import at.fabricate.model.AddRepository
import net.liftweb.http.LiftRules
import net.liftweb.mapper.LongKeyedMapper
import java.io.File
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import net.liftweb.common.Empty
import java.util.zip.ZipOutputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.io.BufferedInputStream
import java.io.FileInputStream
import net.liftweb.http.FileParamHolder
import org.eclipse.jgit.revwalk.RevCommit
import java.io.FileFilter
import org.eclipse.jgit.lib.Ref
import net.liftweb.common.Full
import scala.collection.JavaConversions._
import at.fabricate.model.MatchByID


class GitWrapper[T <: (AddRepository[T] with MatchByID[T]) ](owner : T) extends MappedBoolean[T](owner) { 
	// may be overridden in subclasses for other behaviour
    // repositoryID might be used as well
	def pathToRepository : String = basePathToItemFolder + "/repository/"
	
	def pathToData : String = basePathToItemFolder + "/data/"

//	def basePathToItemFolder : String = webappRoot+"repository/"+repositoryID
	def basePathToItemFolder : String = "/item"
	
	// default path if the ID is not set
	def pathToDefaultRepository : String = basePathToItemFolder+"/DefaultRepository"
	
	def initRepoMessage : String = "Initialized repository!"
  
	// may be overridden in subclasses for other behaviour
//   	def repositoryID : String = fieldOwner.asInstanceOf[LongKeyedMapper[T]].primaryKeyField.get.toString
 
   	// ensure that it is default false, as the value indicates if the repository has alredy been created
   	override def defaultValue = false
   	
   	// get the .git config file inside the repository folder
   	// this is needed for ghe repository object
   	def getRepositoryDotGitDirectory : File = {
      // problem with looping - do it with just strings
      //getRepo.getDirectory
      new File(pathToRepository, ".git")
    }
   	
    // get the repository directory
    // this is the file where all the data is stored inside
   	def getRepositoryDirectory : File = {
      getRepositoryDotGitDirectory.getParentFile()
    }
   	
   	private def isRepositoryInitialized : Boolean = get
   	
   	/*{
   	  // not exactly - maybe ask git.log not empty, but maybe that crashes!
   	  getRepositoryDotGitDirectory.exists() && getRepositoryDotGitDirectory.isDirectory()
   	}
   	* 
   	*/
   	
   	private def createDirectory(path : String) = {
   	  val repoConfigFile =   new File(path, "dummyfile" )
   	  repoConfigFile.getParentFile().mkdirs()   	  
   	  println("created directory for: "+path)
   	}
   	
   	private def initializeRepository : Repository= {
   	  // delete the dir if it already exists
   	  getRepositoryDotGitDirectory.delete()
   	  // create the directory structure
   	  createDirectory(getRepositoryDotGitDirectory.getAbsolutePath())

   	  // initialize the repository
   	  Git.init().setDirectory(getRepositoryDirectory).call()
   	  
   	  val repo = FileRepositoryBuilder.create(  getRepositoryDotGitDirectory )
   	  val commit = new Git(repo).commit().setMessage(initRepoMessage).call()
   	  
   	  // create a zip file for that commit - just for convenience
   	  createZipForCommit(commit.getName())
   	  
   	  // reset the value so that the variable indicates that the repository is initialized
   	  // important: save the fieldOwner afterwards, otherwise it will not be saved
   	  set(true)
   	  fieldOwner.save
   	  
   	  
   	  println("inizialized repository for id "+fieldOwner.primaryKeyField)
   	  repo
   	}
   	/*
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
	* 
	*/

   	private def isIDSet : Boolean = 
   			if (fieldOwner.primaryKeyField != Empty && fieldOwner.primaryKeyField != -1 )
   				true
   			else
   				false
     
   	
  // add a new file to the repo
   				// TODO:
  // pay attention: maybe there are some nasty caracters in the filename:
   				// for example ../../../ 
   				// and then the file is stored somewhere else!
   				// or deleted
   				
  def deleteFileFromRepository(file : File) =   {
   			  file.delete()
   			}
   	
  def createZipForCommit(commitName : String) = {
    val pathToZipDir = pathToData+File.separator+
    			commitName
//            (getAllCommits.length+1).toString
    val pathToZipFile = pathToZipDir+File.separator+
            fieldOwner.primaryKeyField.get+".zip"
    println("creating zip at: "+pathToZipFile)
    createDirectory(pathToZipDir)
    
    val zipoutstream = new ZipOutputStream(
        new FileOutputStream(pathToZipFile) )
//    var clazz = ZippingFileExample.class
//    var inputStream = this.getres
    
    // configure zip compression
    zipoutstream.setMethod(ZipOutputStream.DEFLATED)
    zipoutstream.setLevel(5)

    getAllFilesInRepository.map(file => {      
      zipoutstream.putNextEntry( new ZipEntry(file.getName()) )
      val in = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()))
      var b = in.read()
      while (b > -1) {
        zipoutstream.write(b)
        b = in.read()
      }
      in.close()
      zipoutstream.closeEntry()
    }
    )
     
//    */
//    zipoutstream.putNextEntry( new ZipEntry(getRepositoryDirectory.getAbsolutePath()) )
    zipoutstream.close()
    
  }
   	
  def createSafeFileName(name: String) : String = name.replaceAll("[^a-zA-Z0-9.-]", "_")
   	
  def copyFileToRepository(file : FileParamHolder) =   {
    // remove special characters from the path for security reasons
        val filePathInRepository = new File(getRepositoryDirectory,createSafeFileName(file.fileName ))
        var output = new FileOutputStream(filePathInRepository)
        try {
          output.write(file.file)
        } catch {
          case e : Throwable => {
            println("Exception while copying file to disk: ")
            println(e)
          } 
        } finally {
            output.close
            output = null
          }
        /*
	    withGitReopsitory[RevCommit]{
	   		git => {
	   		  git.add.addFilepattern(filePathInRepository.getAbsolutePath()).call()
	   		  git.commit().setMessage("added file %s to the repository".format(file.fileName )).call()
	   		}
   		}
   		* 
   		*/
   	}
   	

  // commit the repository
  def commit(message : String) = 
    withGitReopsitory[RevCommit]{
   		git => {

   		  // add all files that are in the repository to be added at the commit
   		  // TODO: might not work with directories, maybe use file.getAbsolutePath() then
   		  getAllFilesInRepository.map(file => git.add().addFilepattern(file.getName()).call())
   		  val commit = git.commit().setAll(true).
//   		  setAuthor(new PersonIdent("openthings")).
   		  setMessage(message).call()
   		  // create a zip file with the actual content
   		  // ToDo: maybe use a nicer name?
   		  createZipForCommit(commit.getName())
   		  commit
   		}
  }

  object IgnoreDotGitDirectory extends FileFilter {
    override def accept( file : File) : Boolean = {
//      println("file: "+file.getPath()+ "\n starts with .git : "+file.getPath().endsWith(".git"))
    	if (file.getPath().endsWith(".git")) // maybe file.isDirectory && 
    	  false
        else
          true
    }
  }
   	
   	def getAllFilesInRepository : List[java.io.File] = 
   	  if (getRepositoryDirectory.exists)
   	    getRepositoryDirectory.listFiles(IgnoreDotGitDirectory).toList
   	    else
   	      List[java.io.File]()
   	
   	
   	def getAllCommits : List[RevCommit] = withGitReopsitory[List[RevCommit]]{
   		git => git.log().all.call().iterator().toList
   	}
   	
   	def revertToCommit(commit : RevCommit)  = withGitReopsitory[Ref]{
   		git => git.checkout().setName(commit.getName()).call()
   	}   	
   	
   	
   	private def withGitReopsitory[U](op: Git => U ) : U  = {
//      val repoWasInitialized = isRepositoryInitialized   	  
   	  // create the repository if it does not already exitst
//   	  if (!repoWasInitialized)
//   		  initializeRepository
   	  
   	  val repository = 
   	    if (isRepositoryInitialized)
   	      	openExistingRepo
   	      else
   	        initializeRepository
   	        
//   	  println("repository: "+repository.getDirectory().getAbsolutePath())
   	        
   	  val gitRepository : Git = new Git(repository)
   	  
   	  // perform an initial commit as a baseline
//   	  if (!repoWasInitialized)
//   	    gitRepository.commit().setMessage("Created repository with ID "+repositoryID+" !").call()
   	  try {
   	    op(gitRepository)
   	  } finally {
   	    repository.close()
   	  }
   	}
   	
   	  // open an exiting repository
  private def openExistingRepo() : Repository =  
    new FileRepositoryBuilder().
	  setGitDir( getRepositoryDotGitDirectory ).
	  readEnvironment.
	  findGitDir.
	  build
	  
	    
  // get a list of commits
  //def getAllCommits : List[(Long, String)] = List[(Long, String)] ()
  
  // get one special commit
  def getCommit(id : String) = Unit
  
  private def linkToRepo(linkText : String) = <a href={"/item/repository/"+fieldOwner.primaryKeyField.get}>{linkText}</a>
  //State: {get}</span>
  
  override def asHtml = linkToRepo("view the repository") // TODO: plus additionally a download link for the zip

  override def toForm = Full(linkToRepo("edit the repository"))
   	
   	/*
	private def repo : Repository = {
      if (isIDSet) {
	      if (existsdOnFilesystem) {
	        println ("opened repo for item "+repositoryID)
	        openExistingRepo
	      }
	      else {
	        println("created new repo for item "+repositoryID)
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
	
	
	//private def git : Git = new Git(repo)
 	

  
  
  private def initialCommit = {
         Git.init().setDirectory(repo.getDirectory().getParentFile()).call()
       }
  
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
	  repo.close()))
	  
	  openExistingRepo
	}
  
*/

}