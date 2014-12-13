package at.fabricate
package model

import org.eclipse.jgit.lib._
import org.eclipse.jgit.api._
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

trait AddRepository {
  val projectID = 1
  
	val repo : Repository = createNewRepo
	// FileRepo is just one option, maybe put the stuff to the database
	
	// TODO: Think about a server Interface where you can push and pull - would be amazing!!
	
	
	val git : Git = new Git(repo)
  
  // add a new file to the repo
  def addAFileToTheRepo(filepattern : String) =   git.add.addFilepattern(filepattern).call()

  def commit = git.commit().call()
  
  // get the file-object of the git repo
  def getRepoFile : File = new File("repo/"+projectID )
  
    // create a new repository
  def createNewRepo() : Repository = FileRepositoryBuilder.create( getRepoFile )
  
  // open an exiting repository
  def openExistingRepo() : Repository =  new FileRepositoryBuilder().setGitDir( getRepoFile ).build
}