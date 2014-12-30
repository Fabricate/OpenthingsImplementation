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
		     var commitLabel = "Describe the key features of this project revision"

//		println("chaining asHtml from AddRepositorySnippet")
    
	    	  	def commit() : JsCmd = {
		          		    //project.repository.createNewRepo
		        	item.repository.commit(commitLabel)
		           
	    	  	    JqId("commits") ~> JqHtml( getNewCommitList ) 

//		            SetHtml("fruitbat", listAllCommits(commitTemplate) ) &
//		            SetHtml("fruitbat", //  this one//  this one should also work)
//				    JsCmds.Alert("Commited project "+id+ " with commit message "+commitLabel)
	    	  	}
	    	  	 def updateFileList : JsCmd = {
//	    	  	   val commits = project.repository.getAllCommits
		            // Thread.sleep(1000)
		            JqId("files") ~> JqHtml( getNewFileList ) 
//				    JsCmds.Alert("Created repo for project "+id+"\n commits: "+commits)
	    	  	   
	    	  	 }
	    	  	 
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
	    	  	   var commitId=1
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
		      "#commitlabel" #> SHtml.ajaxText(commitLabel, (str) => {commitLabel = str})&
			  "#listfiles" #>  listAllFiles &
			  "#listcommits" #> listAllCommits &	      
		      "#commitrepo [onclick]" #> SHtml.ajaxInvoke(commit) &
		      //"#commitrepo " #> SHtml.hidden(commit(id)) &
//		      "#testbutton [onclick]" #> SHtml.ajaxInvoke(callback) &
		      "#fileupload [data-url]" #> "/api/upload/file/%s".format(item.primaryKeyField) &
		      "#uploadconfig *+" #> Function(
        "UpdateFilelist", List("dummy"),SHtml.ajaxCall(JsVar("dummy"), 
            (dummy: String) => updateFileList 
            )._2.cmd
		   )
				) &
     // chain the css selectors 
     (super.asHtml(item))
  }
}