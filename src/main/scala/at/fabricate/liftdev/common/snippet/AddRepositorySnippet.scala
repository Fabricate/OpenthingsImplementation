package at.fabricate.liftdev.common
package snippet

import model.AddRepository
import model.AddRepositoryMeta
import net.liftweb.util.CssSel
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq
import scala.xml.Text
import model.BaseEntity
import model.BaseMetaEntityWithTitleAndDescription
import java.io.File
import org.eclipse.jgit.revwalk.RevCommit
import at.fabricate.lib.GitWrapper
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.js.jquery.JqJsCmds.DisplayMessage
import net.liftweb.http.js.JsCmds.Function
import net.liftweb.http.js.JE.JsVar
import at.fabricate.model.BaseEntityWithTitleAndDescription
import net.liftweb.http.RewriteRequest
import net.liftweb.http.ParsePath
import net.liftweb.http.RewriteResponse
import net.liftweb.sitemap.Menu
import net.liftweb.sitemap.*
import net.liftweb.sitemap.Loc.Hidden


trait AddRepositorySnippet[T <: BaseEntityWithTitleAndDescription[T] with AddRepository[T]] extends BaseEntityWithTitleAndDescriptionSnippet[T] {
  
  var filesTemplate : NodeSeq = NodeSeq.Empty 
  var commitTemplate : NodeSeq = NodeSeq.Empty 

  def displayMessageAndHideLocal(message : String ) : JsCmd = displayMessageAndHide("repositoryMessages",message)
  
//  def zipDownloadMenu 
  
//  abstract override def getMenu : List[Menu] = (Menu.i("download zip ") / "projects" / * / "data" / * / *   >> Hidden) :: super.getMenu
  
//  abstract override def generateRewrites : PartialFunction[RewriteRequest,RewriteResponse] = {
////    projects/30/data/770fbca31f27a3480b5419f459f8c9e2e5cf3281/30.zip
//      case RewriteRequest(ParsePath(List(MatchItemPath(itemBasePath), "index"), _, _, _), _, _) =>
//	      RewriteResponse(listTemplate :: Nil)
//  } orElse super.generateRewrites
  
  abstract override def view(xhtml: NodeSeq) :  NodeSeq  =  {
    // get just the comment section
    filesTemplate = ("#listfiles ^^" #> "str").apply(xhtml)    
    commitTemplate = ("#listcommits ^^" #> "str").apply(xhtml)

    super.view(xhtml)
  }
  
  abstract override def asHtml(item : ItemType) : CssSel = {

//		println("chaining asHtml from AddRepositorySnippet")
      var commitLabel = ""
	    	  	def commitRepository(localItem : ItemType)() : JsCmd = {
		    		println("commiting the repository with label: "+commitLabel )
		        	localItem.repository.commit(commitLabel)
	    	  	    SetHtml("commits", getNewCommitList(localItem) ) &
	    	  	    displayMessageAndHideLocal("Commiting %s was successful!".format(commitLabel)) &
	    	  	     // clear the form
					JsCmds.SetValById("commitlabel", "")
	    	  	    
	    	  	}
		     
	    	  	 // update methods for the ajax stuff
	    	  	 def updateFileList(localItem : ItemType) : JsCmd = SetHtml("files", getNewFileList(localItem) ) 
	    	  	 
	    	  	 def getNewFileList(localItem : ItemType) : NodeSeq = ("#listfiles" #>  listAllFiles(localItem)).apply(filesTemplate) 
	    	  	 
	    	  	 def getNewCommitList(localItem : ItemType) : NodeSeq = ("#listcommits" #>  listAllCommits(localItem)).apply(commitTemplate) 

	    	  	 
	    	  	 def listAllFiles(localItem : ItemType) : List[CssSel] = 
	    	  	   localItem.repository.getAllFilesInRepository.map(
	    	  			   file => bindFileCSS(file,localItem) 
			          )
	    	  	 
	    	  	 def bindFileCSS(file : File, localItem : ItemType) : CssSel = 
	    	  	   "#filename" #> file.getName() & 
			          "#deletefile [onclick]" #> SHtml.ajaxInvoke(() => {
			            localItem.repository.deleteFileFromRepository(file)
			            updateFileList(localItem) &
			            displayMessageAndHideLocal("Deleted file "+file.getName())
			          } ) 
	    	  	   
	    	  	 def listAllCommits(localItem : ItemType) : List[CssSel] = 
	    	  	   localItem.repository.getAllCommits.map(
	    	  			   commit => bindCommitCss(commit, localItem) 
			          )
			          
			     def bindCommitCss(commit : RevCommit, localItem : ItemType) : CssSel =
			          "#commitname" #> commit.getFullMessage() & 
			          "#downloadcommit [href]" #> "/%s/%s/%s/%s/%s.zip".format(
			              localItem.basePathToRepository, 
			              localItem.repositoryID,
			              localItem.endPathToData,
			              commit.getName(),
			              localItem.repositoryID) & 
			          "#revertcommit [onclick]" #> SHtml.ajaxInvoke(() => {
			            localItem.repository.revertChangesOfCommit(commit)
			            updateFileList(localItem) &
			            displayMessageAndHideLocal("Undo changes of commit "+commit.getFullMessage())
			          } )& 
			          "#resetcommit [onclick]" #> SHtml.ajaxInvoke(() => {
			            localItem.repository.resetToCommit(commit)
			            updateFileList(localItem) &
			            displayMessageAndHideLocal("Rolled back to commit "+commit.getFullMessage())
			          } )
			          
				(
//		      "#createrepo [onclick]" #> SHtml.ajaxInvoke(callback) &
		      //"#commitlabel" #> SHtml.text(commitLabel, commitLabel = _) &
//		      "#commitlabel" #> SHtml.text("", (str) => {commitLabel = str; JsCmds.Noop}, "default"->"Describe the key features of this project revision")&
		      "#commitlabel" #> SHtml.ajaxText("", value => {commitLabel = value}, "placeholder"->"Describe the key features of this project revision") &
			  "#listfiles" #>  listAllFiles(item ) &
			  "#listcommits" #> listAllCommits(item ) &	      
//		      "#commitrepohidden" #> SHtml.hidden(() => commit(item, commitLabel  )) &
		      "#commitrepo [onclick]" #> SHtml.ajaxInvoke(commitRepository(item) ) &
		      //"#commitrepo " #> SHtml.hidden(commit(id)) &
//		      "#testbutton [onclick]" #> SHtml.ajaxInvoke(callback) &
		      "#fileupload [data-url]" #> "/%s/%s/%s/%s".format(
		          item.apiPath, 
		          item.uploadPath,
		          item.repositoryPath,
		          item.primaryKeyField) &
		      "#uploadconfig *+" #> Function(
			        "UpdateFilelist", List("newfile"),SHtml.ajaxCall(JsVar("newfile"), 
				            (newfile: String) => {
				              println("received %s and called snipped ".format(newfile)); 
				              displayMessageAndHideLocal("Uploading %s was successful!".format(newfile)) &
				              updateFileList(item ) 
				              }
			            )._2.cmd
		    		  )
				) &
     // chain the css selectors 
     (super.asHtml(item))
  }
  
}
