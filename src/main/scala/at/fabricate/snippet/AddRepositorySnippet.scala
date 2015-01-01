package at.fabricate
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
import model.BaseEntityMeta
import model.BaseRichEntityMeta
import model.BaseRichEntity
import java.io.File
import org.eclipse.jgit.revwalk.RevCommit
import at.fabricate.model.GitWrapper
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.js.jquery.JqJsCmds.DisplayMessage
import net.liftweb.http.js.JsCmds.Function
import net.liftweb.http.js.JE.JsVar


trait AddRepositorySnippet[T <: BaseEntity[T] with AddRepository[T]] extends BaseEntitySnippet[T] {
  
  var filesTemplate : NodeSeq = NodeSeq.Empty 
  var commitTemplate : NodeSeq = NodeSeq.Empty 

  def displayMessageAndHideLocal(message : String ) : JsCmd = displayMessageAndHide("repositoryMessages",message)
  
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
			          "#downloadcommit [href]" #> "/projects/%s/data/%s/%s.zip".format(localItem.primaryKeyField.toString,commit.getName(),localItem.primaryKeyField.toString) & 
			          "#resetcommit [onclick]" #> SHtml.ajaxInvoke(() => {
			            localItem.repository.revertToCommit(commit)
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
		      "#fileupload [data-url]" #> "/api/upload/file/%s".format(item.primaryKeyField) &
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