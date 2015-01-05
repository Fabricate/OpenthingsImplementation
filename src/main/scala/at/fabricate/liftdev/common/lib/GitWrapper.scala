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
	  
	def defaultCommitterName = "openthings"
	def defaultCommitterMail = "openthings@openthings.openthings"
  
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
   	  val gitRepository = new Git(repo)
   	  // TODO: DRY!!!
   	  val config = gitRepository.getRepository().getConfig()
   	  config.setString(ConfigConstants.CONFIG_USER_SECTION, null, ConfigConstants.CONFIG_KEY_NAME, defaultCommitterName)
   
   	  config.setString(ConfigConstants.CONFIG_USER_SECTION, null, ConfigConstants.CONFIG_KEY_EMAIL, defaultCommitterMail)
   	  config.save()
   	  
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
  def commit(message : String, committerName : String = defaultCommitterName, committerMail : String = defaultCommitterMail) = 
    withGitReopsitory[RevCommit]{
   		git => {

   		  // add all files that are in the repository to be added at the commit
   		  // TODO: might not work with directories, maybe use file.getAbsolutePath() then
   		  getAllFilesInRepository.map(file => git.add().addFilepattern(file.getName()).call())
   		  val commit = git.commit().setAll(true).
   		  setCommitter(new PersonIdent(committerName, committerMail)).
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
   	
   	// TODO: What happens with uncommited changes ?
   	def revertChangesOfCommit(commit : RevCommit)  = {
//   	  withGitReopsitory[Ref]{
//   	   //this solution creates "head detached" problem
//   		git => git.checkout().setName(commit.getName()).call()
//   		// no problem with head detached, but files are not deleted
//   		git => git.checkout().setAllPaths(true).setName(commit.getName()).call()
//   	  // other solution -> UNTESTED
   	  val revertingCommit = withGitReopsitory[RevCommit]{
   	    // how to set a commiter here ?
   		git => git.revert().
   		include(commit). // which commit to revert
   		setStrategy(MergeStrategy.RESOLVE).
   		call()
   	}   	
   	  createZipForCommit(commit.getName())
   	}
   	
   	def resetToCommit(commit : RevCommit) = 
   	  // untested, just taken from the jgit test cases at
   	  // https://github.com/eclipse/jgit/tree/master/org.eclipse.jgit.test/tst/org/eclipse/jgit/api
//   	  ResetType.HARD
//   	  ResetType.SOFT
//   	  ResetType.MIXED
   	  withGitReopsitory[Ref]{
   	  	git => git.reset().
   	  	setMode(ResetType.HARD).
   	  	setRef(commit.getName()).
   	  	call()
   	}
   	
   	def differenceBetweenCommits(commit1 : RevCommit, commit2 : RevCommit)  = 
   	  // idea for a solution -> UNTESTED
   	  withGitReopsitory[List[DiffEntry]]{

   	  git => git.diff().setSourcePrefix(commit1.getName()).setDestinationPrefix(commit2.getName()).call().toList
   	}  
   	
//   	 public static void main(String[] args) throws IOException, GitAPIException {
//	Repository repository = CookbookHelper.openJGitCookbookRepository();
//	// the diff works on TreeIterators, we prepare two for the two branches
//	AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, "09c65401f3730eb3e619c33bf31e2376fb393727");
//	AbstractTreeIterator newTreeParser = prepareTreeParser(repository, "aa31703b65774e4a06010824601e56375a70078c");
//	// then the procelain diff-command returns a list of diff entries
//	List<DiffEntry> diff = new Git(repository).diff().
//	setOldTree(oldTreeParser).
//	setNewTree(newTreeParser).
//	setPathFilter(PathFilter.create("README.md")).
//	call();
//	for (DiffEntry entry : diff) {
//	System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
//	DiffFormatter formatter = new DiffFormatter(System.out);
//	formatter.setRepository(repository);
//	formatter.format(entry);
//	}
//	repository.close();
//	}
//	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException,
//	MissingObjectException,
//	IncorrectObjectTypeException {
//	// from the commit we can build the tree which allows us to construct the TreeParser
//	RevWalk walk = new RevWalk(repository);
//	RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
//	RevTree tree = walk.parseTree(commit.getTree().getId());
//	CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
//	ObjectReader oldReader = repository.newObjectReader();
//	try {
//	oldTreeParser.reset(oldReader, tree.getId());
//	} finally {
//	oldReader.release();
//	}
//	walk.dispose();0 differences by openthings
//	return oldTreeParser;
//	}
   	
//	public class ShowChangedFilesBetweenCommits {
//	public static void main(String[] args) throws IOException, GitAPIException {
//	Repository repository = CookbookHelper.openJGitCookbookRepository();
//	// The {tree} will return the underlying tree-id instead of the commit-id itself!
//	// For a description of what the carets do see e.g. http://www.paulboxley.com/blog/2011/06/git-caret-and-tilde
//	// This means we are selecting the parent of the parent of the parent of the parent of current HEAD and
//	// take the tree-ish of it
//	ObjectId oldHead = repository.resolve("HEAD^^^^{tree}");
//	ObjectId head = repository.resolve("HEAD^{tree}");
//	System.out.println("Printing diff between tree: " + oldHead + " and " + head);
//	// prepare the two iterators to compute the diff between
//	ObjectReader reader = repository.newObjectReader();
//	CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
//	oldTreeIter.reset(reader, oldHead);
//	CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
//	newTreeIter.reset(reader, head);
//	// finally get the list of changed files
//	List<DiffEntry> diffs= new Git(repository).diff()
//	.setNewTree(newTreeIter)
//	.setOldTree(oldTreeIter)
//	.call();
//	for (DiffEntry entry : diffs) {
//	System.out.println("Entry: " + entry);
//	}
//	System.out.println("Done");
//	repository.close();
//	}
//	}   
   	
//	 Repository repository = CookbookHelper.openJGitCookbookRepository();
//	Status status = new Git(repository).status().call();
//	System.out.println("Added: " + status.getAdded());
//	System.out.println("Changed: " + status.getChanged());
//	System.out.println("Conflicting: " + status.getConflicting());
//	System.out.println("ConflictingStageState: " + status.getConflictingStageState());
//	System.out.println("IgnoredNotInIndex: " + status.getIgnoredNotInIndex());
//	System.out.println("Missing: " + status.getMissing());
//	System.out.println("Modified: " + status.getModified());
//	System.out.println("Removed: " + status.getRemoved());
//	System.out.println("Untracked: " + status.getUntracked());
//	System.out.println("UntrackedFolders: " + status.getUntrackedFolders());
//	repository.close();   	
   	
   	
//	public class CreateArchive {
//	public static void main(String[] args) throws IOException, GitAPIException {
//	Repository repository = CookbookHelper.openJGitCookbookRepository();
//	// make the included archive formats known
//	ArchiveFormats.registerAll();
//	try {
//	write(repository, ".zip", "zip");
//	write(repository, ".tar.gz", "tgz");
//	write(repository, ".tar.bz2", "tbz2");
//	write(repository, ".tar.xz", "txz");
//	} finally {
//	ArchiveFormats.unregisterAll();
//	}
//	repository.close();
//	}
//	private static void write(Repository repository, String suffix, String format) throws IOException, GitAPIException {
//	// this is the file that we write the archive to
//	File file = File.createTempFile("test", suffix);
//	try (OutputStream out = new FileOutputStream(file)) {
//	// finally call the ArchiveCommand to write out using the various supported formats
//	new Git(repository).archive()
//	.setTree(repository.resolve("master"))
//	.setFormat(format)
//	.setOutputStream(out)
//	.call();
//	}
//	System.out.println("Wrote " + file.length() + " bytes to " + file);
//	}
//	}
   	
//	public class AddAndListNoteOfCommit {
//	public static void main(String[] args) throws IOException, GitAPIException {
//	Repository repository = CookbookHelper.openJGitCookbookRepository();
//	Ref head = repository.getRef("refs/heads/master");
//	System.out.println("Found head: " + head);
//	RevWalk walk = new RevWalk(repository);
//	RevCommit commit = walk.parseCommit(head.getObjectId());
//	System.out.println("Found Commit: " + commit);
//	new Git(repository).notesAdd().setMessage("some note message").setObjectId(commit).call();
//	System.out.println("Added Note to commit " + commit);
//	List<Note> call = new Git(repository).notesList().call();
//	System.out.println("Listing " + call.size() + " notes");
//	for(Note note : call) {
//	// check if we found the note for this commit
//	if(!note.getName().equals(head.getObjectId().getName())) {
//	System.out.println("Note " + note + " did not match commit " + head);
//	continue;
//	}
//	System.out.println("Found note: " + note + " for commit " + head);
//	// displaying the contents of the note is done via a simple blob-read
//	ObjectLoader loader = repository.open(note.getData());
//	loader.copyTo(System.out);
//	}
//	walk.dispose();
//	repository.close();
//	}
//	}   
   	

//	final long now = mockSystemReader.getCurrentTime();
//	final int tz = mockSystemReader.getTimezone(now);
//	author = new PersonIdent("J. Author", "jauthor@example.com");
//	author = new PersonIdent(author, now, tz);
//	committer = new PersonIdent("J. Committer", "jcommitter@example.com");
//	committer = new PersonIdent(committer, now, tz);   	
	// check that all commits came in correctly

//	// do 4 commits
//	Git git = new Git(db);
//	git.commit().setMessage("initial commit").call();
//	git.commit().setMessage("second commit").setCommitter(committer).call();
//	git.commit().setMessage("third commit").setAuthor(author).call();
//	git.commit().setMessage("fourth commit").setAuthor(author)
//	.setCommitter(committer).call();
//	Iterable<RevCommit> commits = git.log().call();
//	// check that all commits came in correctly
//	PersonIdent defaultCommitter = new PersonIdent(db);
//	PersonIdent expectedAuthors[] = new PersonIdent[] { defaultCommitter,
//	committer, author, author };
//	PersonIdent expectedCommitters[] = new PersonIdent[] {
//	defaultCommitter, committer, defaultCommitter, committer };
//	String expectedMessages[] = new String[] { "initial commit",
//	"second commit", "third commit", "fourth commit" };
//	int l = expectedAuthors.length - 1;
//	for (RevCommit c : commits) {
//	assertEquals(expectedAuthors[l].getName(), c.getAuthorIdent()
//	.getName());
//	assertEquals(expectedCommitters[l].getName(), c.getCommitterIdent()
//	.getName());
//	assertEquals(c.getFullMessage(), expectedMessages[l]);
//	l--;
//	}
//	assertEquals(l, -1);
//	ReflogReader reader = db.getReflogReader(Constants.HEAD);
//	assertTrue(reader.getLastEntry().getComment().startsWith("commit:"));
//	reader = db.getReflogReader(db.getBranch());
//	assertTrue(reader.getLastEntry().getComment().startsWith("commit:"));	

   	private def withGitReopsitory[U](op: Git => U ) : U  = {
//      val repoWasInitialized = isRepositoryInitialized   	  
   	  // create the repository if it does not already exitst
//   	  if (!repoWasInitialized)
//   		  initializeRepository
   	  
   	  // try to set the system environment variables
//   	  sys.env.
//   	  	env.put("GIT_DIR", db.getDirectory().getAbsolutePath());
//   	  GIT_" + type + "_NAME
//   	  	env.put("GIT_" + type + "_NAME", who.getName());
//env.put("GIT_" + type + "_EMAIL", who.getEmailAddress());
//env.put("GIT_" + type + "_DATE", date);
//   	  Process(Seq("bash", "-c", "echo $asdf"), None, 
//   	      "GIT_DIR" -> "Hello, world!", "asdf" -> "Hello, world!")
//   	  Properties.
   	  
   	  val repository = 
   	    if (isRepositoryInitialized)
   	      	openExistingRepo
   	      else
   	        initializeRepository
   	        
//   	  println("repository: "+repository.getDirectory().getAbsolutePath())
   	        
   	  val gitRepository : Git = new Git(repository)
   	  val config = gitRepository.getRepository().getConfig()
   	  config.setString(ConfigConstants.CONFIG_USER_SECTION, null, ConfigConstants.CONFIG_KEY_NAME, defaultCommitterName)
   
   	  config.setString(ConfigConstants.CONFIG_USER_SECTION, null, ConfigConstants.CONFIG_KEY_EMAIL, defaultCommitterMail)
   	  config.save()
//   	  gitRepository.describe().setTarget("committer").setTarget(new PersonIdent(defaultCommitterName,defaultCommitterMail))
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
