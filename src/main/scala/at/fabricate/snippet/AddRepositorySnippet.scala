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
import net.liftweb.http.js.JsCmd._
import net.liftweb.http.js.jquery.JqJsCmds._
import net.liftweb.http.js._
import net.liftweb.http.js.jquery.JqJE._
import net.liftweb.http.js.jquery._
import net.liftweb.http.js.JsCmds.Function
import net.liftweb.http.js.JE.JsVar
import net.liftweb.http.js.JsCmds.SetHtml


trait AddRepositorySnippet[T <: BaseEntity[T] with AddRepository[T]] extends BaseEntitySnippet[T] {
  
  var filesTemplate : NodeSeq = NodeSeq.Empty 
  var commitTemplate : NodeSeq = NodeSeq.Empty 
  
  abstract override def view(xhtml: NodeSeq) :  NodeSeq  =  {
    // get just the comment section
    filesTemplate = ("#listfiles ^^" #> "str").apply(xhtml)    
    commitTemplate = ("#listcommits ^^" #> "str").apply(xhtml)

    super.view(xhtml)
  }
  
  abstract override def asHtml(item : ItemType) : CssSel = {
		     var commitLabel = ""

//		println("chaining asHtml from AddRepositorySnippet")
    
	    	  	def commit() : JsCmd = {
		        	item.repository.commit(commitLabel)
	    	  	    SetHtml("commits", getNewCommitList ) &
	    	  	     // clear the form
					JsCmds.SetValById("commitlabel", "")
	    	  	    
	    	  	}
		     
	    	  	 def updateFileList : JsCmd = SetHtml("files", getNewFileList ) 
	    	  	 
	    	  	 def getNewFileList : NodeSeq = ("#listfiles" #>  listAllFiles).apply(filesTemplate) 
	    	  	 
	    	  	 def listAllFiles : List[CssSel] = {
	    	  	   item.repository.getAllFilesInRepository.map(
			      file => (
			          "#filename" #> file.getName() & 
			          "#deletefile [onclick]" #> SHtml.ajaxInvoke(() => {
			            item.repository.deleteFileFromRepository(file)
			            updateFileList &
			            JsCmds.Alert("deleted file "+file.getName())
			          } ) ) 
			          )
	    	  	 }
	    	  	 
	    	  	 def getNewCommitList : NodeSeq = ("#listcommits" #>  listAllCommits).apply(commitTemplate) 

	    	  	 def listAllCommits() : List[CssSel] = {
	    	  	   var repoId=item.primaryKeyField.get
	    	  	   item.repository.getAllCommits.map(
			      commit => (
			          "#commitname" #> commit.getFullMessage() & 
			          "#downloadcommit [href]" #> "/projects/%d/data/%s/%d.zip".format(repoId,commit.getName(),repoId) & 
			          "#resetcommit [onclick]" #> SHtml.ajaxInvoke(() => {
			            item.repository.revertToCommit(commit)
			            JsCmds.Alert("Rolled back to commit "+commit.getFullMessage())
			          } ) ) 
			          )
	    	  	 }
	    	  	 
				(
//		      "#createrepo [onclick]" #> SHtml.ajaxInvoke(callback) &
		      //"#commitlabel" #> SHtml.text(commitLabel, commitLabel = _) &
		      "#commitlabel" #> SHtml.text("", (str) => {commitLabel = str; JsCmds.Noop}, "default"->"Describe the key features of this project revision")&
			  "#listfiles" #>  listAllFiles &
			  "#listcommits" #> listAllCommits &	      
		      "#commitrepohidden" #> SHtml.hidden(commit) &
		      //"#commitrepo " #> SHtml.hidden(commit(id)) &
//		      "#testbutton [onclick]" #> SHtml.ajaxInvoke(callback) &
		      "#fileupload [data-url]" #> "/api/upload/file/%s".format(item.primaryKeyField) &
		      "#uploadconfig *+" #> Function(
        "UpdateFilelist", List("newfile"),SHtml.ajaxCall(JsVar("dummy"), 
            (newfile: String) => {
              println("received "+newfile); 
              DisplayMessage("repositoryMessages", <span class="message">{"Uploading %s was successful!".format(newfile)}</span>, 5 seconds, 1 second) &
              updateFileList }
            )._2.cmd
		   )
				) &
     // chain the css selectors 
     (super.asHtml(item))
  }
}