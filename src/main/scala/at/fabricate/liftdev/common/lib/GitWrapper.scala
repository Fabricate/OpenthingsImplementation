package at.fabricate.liftdev.common
package lib

import net.liftweb.mapper.MappedBoolean
import model.AddRepository
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
import model.MatchByID
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.api.ResetCommand.ResetType
import org.eclipse.jgit.lib.PersonIdent
import scala.util.Properties
import scala.sys.process.Process
import org.eclipse.jgit.lib.ConfigConstants
import org.eclipse.jgit.merge.MergeStrategy
import org.eclipse.jgit.treewalk.WorkingTreeIterator
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.AbstractTreeIterator


class GitWrapper[T <: (AddRepository[T] with MatchByID[T]) ](owner : T) extends MappedBoolean[T](owner) { 
	// may be overridden in subclasses for other behaviour
    // repositoryID might be used as well
	def pathToRepository : String = basePathToItemFolder + "/repository/"
	
	def pathToData : String = basePathToItemFolder + "/data/"

	def basePathToItemFolder : String = "/item"
	
	// default path if the ID is not set
	def pathToDefaultRepository : String = basePathToItemFolder+"/DefaultRepository"
	
	def initRepoMessage : String = "Initialized repository!"
	  
	def defaultCommitterName = "openthings"
	def defaultCommitterMail = "openthings@openthings.openthings"
  
 
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
   	  val gitRepository = getGitRepositoryAndSetDefaults(repo)
   	  
   	  val commit = gitRepository.commit().setMessage(initRepoMessage).call()
   	  
   	  // create a zip file for that commit - just for convenience
   	  createZipForCommit(commit.getName())
   	  
   	  // reset the value so that the variable indicates that the repository is initialized
   	  // important: save the fieldOwner afterwards, otherwise it will not be saved
   	  set(true)
   	  fieldOwner.save
   	  
   	  
   	  println("inizialized repository for id "+fieldOwner.primaryKeyField)
   	  repo
   	}


   	private def isIDSet : Boolean = 
   			if (fieldOwner.primaryKeyField != Empty && fieldOwner.primaryKeyField != -1 )
   				true
   			else
   				false
     
  
   				
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
    
    // configure zip compression
//    zipoutstream.setMethod(ZipOutputStream.DEFLATED)
//    zipoutstream.setLevel(5)

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

   	}
   	

  // commit the repository
  def commit(message : String, committerName : String = defaultCommitterName, committerMail : String = defaultCommitterMail) = 
    withGitReopsitory[RevCommit]{
   		(git, repository) => {

   		  // add all files that are in the repository to be added at the commit
   		  getAllFilesInRepository.map(file => git.add().addFilepattern(file.getName()).call())
   		  val commit = git.commit().setAll(true).
   		  setCommitter(new PersonIdent(committerName, committerMail)).
   		  setMessage(message).call()
   		  // create a zip file with the actual content
   		  createZipForCommit(commit.getName())
   		  commit
   		}
  }

  object IgnoreDotGitDirectory extends FileFilter {
    override def accept( file : File) : Boolean = {
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
   		(git, repository) => git.log().all.call().iterator().toList
   	}
   	
   	// Revert the changes of a given commit
   	// What happens with uncommited changes ?
   	def revertChangesOfCommit(commit : RevCommit)  = {
   	  val revertingCommit = withGitReopsitory[RevCommit]{
   	    // how to set a commiter here ?
   		(git, repository) => git.revert().
   		include(commit). // which commit to revert
   		setStrategy(MergeStrategy.RESOLVE).
   		call()
   	}   	
   	  createZipForCommit(commit.getName())
   	}
   	
   	def resetToCommit(commit : RevCommit) = 
//   	  ResetType.HARD
//   	  ResetType.SOFT
//   	  ResetType.MIXED
   	  withGitReopsitory[Ref]{
   	  	(git, repository) => git.reset().
   	  	setMode(ResetType.HARD).
   	  	setRef(commit.getName()).
   	  	call()
   	}
   	
   	def differenceBetweenCommits(commit1 : RevCommit, commit2 : RevCommit)  = 
   	  // idea for a solution -> UNTESTED
   	  withGitReopsitory[List[DiffEntry]]{

   	  (git, repository) => {
   	    git.diff().
   	    setOldTree(getAbstractTreeIteratorForCommit(commit2,repository)).
   	    setNewTree(getAbstractTreeIteratorForCommit(commit1,repository)).
   	    call().toList
   	  }
   	}  
   	
   	private def getAbstractTreeIteratorForCommit(commit : RevCommit, repository : Repository) : AbstractTreeIterator = {
//	// from the commit we can build the tree which allows us to construct the TreeParser
	val walk = new RevWalk(repository);
//	RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
	val tree = walk.parseTree(commit.getTree().getId());
	val oldTreeParser = new CanonicalTreeParser();
	val oldReader = repository.newObjectReader();
	try {
		oldTreeParser.reset(oldReader, tree.getId());
	} finally {
		oldReader.release();
	}
	walk.dispose()
	oldTreeParser
   	}
  
   	
   	def getStatus() = { 
   	  withGitReopsitory[Status]{
   	  (git, repository) => git.status().
   	  call()
   	} 
   	}
   	

   	private def withGitReopsitory[U](op: (Git, Repository) => U ) : U  = {

   	  val repository = 
   	    if (isRepositoryInitialized)
   	      	openExistingRepo
   	      else
   	        initializeRepository
   	        
   	  val gitRepository = getGitRepositoryAndSetDefaults(repository)
   	  
   	  try {
   	    op(gitRepository,repository)
   	  } finally {
   	    repository.close()
   	  }
   	}
   	
  private def getGitRepositoryAndSetDefaults(repository : Repository) : Git = {
       	  val gitRepository : Git = new Git(repository)
	   	  val config = gitRepository.getRepository().getConfig()
	   	  config.setString(ConfigConstants.CONFIG_USER_SECTION, null, ConfigConstants.CONFIG_KEY_NAME, defaultCommitterName)
	   
	   	  config.setString(ConfigConstants.CONFIG_USER_SECTION, null, ConfigConstants.CONFIG_KEY_EMAIL, defaultCommitterMail)
	   	  config.save()
	   	  
	   	  gitRepository
  }
   	
   	  // open an exiting repository
  private def openExistingRepo() : Repository =  
    new FileRepositoryBuilder().
	  setGitDir( getRepositoryDotGitDirectory ).
	  readEnvironment.
	  findGitDir.
	  build
	  
	    
  
  // get one special commit
  def getCommit(id : String) = Unit
  
  private def linkToRepo(linkText : String) = <a href={"/item/repository/"+fieldOwner.primaryKeyField.get}>{linkText}</a>
  
  override def asHtml = linkToRepo("view the repository") 

  override def toForm = Full(linkToRepo("edit the repository"))

}
