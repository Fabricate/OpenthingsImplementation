package at.fabricate 
package snippet 

import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import at.fabricate.lib._
import Helpers._
import net.liftweb.http.DispatchSnippet
import net.liftweb.http.S
import at.fabricate.model.User
import net.liftweb.mapper.By

object Designer extends DispatchSnippet with Logger {
  
  def dispatch : DispatchIt = {
    //case "manage" => manage _
    //case "edit" => edit _
    case "view" => view _
  }
    
  def view (xhtml: NodeSeq) : NodeSeq = S.param("id") match {
    case Full(AsLong(designerID)) => {
      User.findAll(By(User.id , designerID)) match {
        case designer :: Nil => {
          /*
          val tags = <a href={"/account/" + acct.name.is}>All tags</a> ++ Text(" ") ++
            acct.tags.flatMap({tag => <a href={"/account/" + acct.name.is + "/" + tag.name.is}>{tag.name.is}</a> ++ Text(" ")})

          // Some closure state for the Ajax calls
          var startDate : Box[Date] = Empty
          var endDate : Box[Date] = Empty
          var graphType = "history"
          val tag = S.param("tag")

          // Ajax utility methods. Defined here to capture the closure vars defined above
          def entryTable = buildExpenseTable(Expense.getByAcct(acct, startDate, endDate, Empty), tag, xhtml)

          def updateGraph() = {
            val dateClause : String = if (startDate.isDefined || endDate.isDefined) {
              List(startDate.map("start=" + Util.noSlashDate.format(_)),
                   endDate.map("end=" + Util.noSlashDate.format(_))).filter(_.isDefined).map(_.open_!).mkString("?","&","")
            } else ""

            val url = "/graph/" + acctName + "/" + graphType + dateClause

            JsCmds.SetHtml("entry_graph", <img src={url} />)
          }

          def updateTable() = {
            JsCmds.SetHtml("entry_table", entryTable)
          }

          def updateStartDate (date : String) = {
            startDate = Util.parseDate(date, Util.slashDate.parse)
            updateGraph() & updateTable()
          }

          def updateEndDate (date : String) = {
            endDate = Util.parseDate(date, Util.slashDate.parse)
            updateGraph() & updateTable()
          }

          def updateGraphType(newType : String) = {
            graphType = newType
            updateGraph()
          }
          * 
          */

          bind("dsigner", xhtml,
               //"atomLink" -> <link href={"/api/account/" + acct.id} type="application/atom+xml" rel="alternate" title={acct.name + " feed"} />,
               "firstname" -> designer.firstName.asHtml,
               "lastname" -> designer.lastName.asHtml,
               "aboutme" -> designer.aboutMe.asHtml,
               //"startDate" -> SHtml.ajaxText("", updateStartDate),
               //"endDate" -> SHtml.ajaxText("", updateEndDate),
               //"graphType" -> SHtml.ajaxSelect(graphChoices, Full("history"), updateGraphType),
               "image" -> designer.userImage.asHtml //<img src={"/graph/" + acctName + "/history"} />
               //"table" -> entryTable
               )
               //"image" -> <img src={"/graph/" + acctName + "/history"} />
        }
        case _ => warn("Couldn't locate designer \"%s\"".format(designerID)); Text("Could not locate designer " + designerID)
      }
    }
    case _ => Text("No account name provided")
  }
}

