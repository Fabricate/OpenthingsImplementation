package at.fabricate
package snippet

import net.liftweb.http.DispatchSnippet
import net.liftweb.common.Logger
import scala.xml.NodeSeq
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import net.liftweb.http.S
import at.fabricate.model.Project
import scala.xml.Text
import at.fabricate.lib.MapperBinder



object ProjectSnippet extends DispatchSnippet with Logger {
  
  def dispatch : DispatchIt = {
    case "list" => list(_)
    case "edit" => edit _
    case "view" => view(_)
  }
  
  private def withObject(op : Project => ((NodeSeq) => NodeSeq) ) : ((NodeSeq) => NodeSeq) = 
    S.param("id").get match {
      case Project.FindByID(project) => op(project)
      case _ => (node => Text("Object not found!"))
    }
	
  private def edit(xhtml: NodeSeq) : NodeSeq  =  { 
    // just a dummy implementation
        ("#designername *" #>  "%s %s".format("", "") &
    		"#designerimage" #>  "" &
    		"#designerpage [href]" #> "/designer/%d".format(0)
    )(xhtml)
  }
    private def list:  CssSel =   
    // just a dummy implementation
   "#item" #> { Project.findAll.map(project => MapperBinder.bindMapper(project, {
     "#link [href]" #> "/project/%d".format(project.id)
   }) _)}
   
   
  private def view :  CssSel  =  
    S.param("id").get match {
      case Project.FindByID(project) => "#dbcontent" #> {MapperBinder.bindMapper(project, {
     "#icon [src]" #> project.icon .url
   }) _}
      case _ => ("*" #> Text("Object not found!") )
    }
}