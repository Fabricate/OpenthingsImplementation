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
import net.liftweb.http.SHtml
import net.liftweb.http.FileParamHolder
import net.liftmodules.imaging._
import net.liftmodules.textile.TextileParser

object Designer extends DispatchSnippet with Logger {
  
  def dispatch : DispatchIt = {
    //case "manage" => manage _
    case "edit" => edit _
    case "view" => view _
  }
  
  def edit (xhtml: NodeSeq) : NodeSeq =  { //      //case RewriteRequest(ParsePath(List("account", acctName, tag), _, _, _), _, _) =>
	  //    RewriteResponse("viewAcct" :: Nil, Map("name" -> urlDecode(acctName), "tag" -> urlDecode(tag)))

    //case Full(AsLong(designerID)) => {
      User.currentUser match {
        case Full(designer) => {
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
          //∗∗Speichert die Änderungen∗/
          var firstName = designer.firstName.toString
          var lastName = designer.lastName.toString
          var aboutMe = designer.aboutMe.toString
          var userImage : Box[FileParamHolder] = Empty
          
        def saveChanges = {
            designer.firstName.set(firstName)
            designer.lastName.set(lastName)
            designer.aboutMe.set(aboutMe)
            // TODO: what about deleting the image???
            userImage match {
              case Full(FileParamHolder(_,null,_,_)) =>  S.notice("Huch")
              case Full(FileParamHolder(_,mime,_,data))
                if mime.startsWith("image/") => 	{
                  val inputStream = userImage.openOrThrowException("User image should not be Empty!").fileStream
                  var metaImage = ImageResizer.getImageFromStream(inputStream)
                  metaImage = ImageResizer.removeAlphaChannel(metaImage)
                  val image = ImageResizer.max(metaImage.orientation, metaImage.image, User.userImage.maxWidth , User.userImage.maxHeight )
                  val jpg = ImageResizer.imageToBytes(ImageOutFormat.jpeg , image, User.userImage.jpegQuality)
                  designer.userImage.set(jpg)
                }
              case Full(_) => S.error("Invalid attachment")
              case _ => {
                S.error("No attachment")
                warn( "No Attachment: "+userImage )
              }
              
            }
            designer.save
        		//User.currentUser.openOr(User).subscriptions.foreach(_.delete_!)
        		//subscribedUsers.foreach{
        		//	u =>
        		//	Subscription.create.subscriberId(User.currentUser.openOr(User).id).sourceId(u.id ).save
        		//}
        	S.notice("Änderungen gespeichert")
        	S.redirectTo("/designer/"+designer.id.toString)
        }
          
          /*different image resizing from http://stackoverflow.com/questions/1404814/lift-image-upload-resize-store-in-database-display
           * def resize(is:java.io.InputStream, maxWidth:Int, maxHeight:Int):BufferedImage = {
    require (maxWidth > 0)
    require (maxHeight > 0)
    val originalImage:BufferedImage = ImageIO.read(is)

    var height = originalImage.getHeight
    var width = originalImage.getWidth

    // Shortcut to save a pointless reprocessing in case the image is small enough already
    if (width <= maxWidth && height <= maxHeight)
        originalImage
    else {          
        // If the picture was too big, it will either fit by width or height.
        // This essentially resizes the dimensions twice, until it fits
        if (width > maxWidth){
          height = (height.doubleValue() * (maxWidth.doubleValue() / width.doubleValue())).intValue
          width = maxWidth
        }
        if (height > maxHeight){
          width = (width.doubleValue() * (maxHeight.doubleValue() / height.doubleValue())).intValue
          height = maxHeight
        }
        val scaledBI = new BufferedImage(width, height,  BufferedImage.TYPE_INT_RGB)
        val g = scaledBI.createGraphics
        g.setComposite(AlphaComposite.Src)
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose
        scaledBI
    }
}
           */


          bind("dsigner", xhtml,
               //"atomLink" -> <link href={"/api/account/" + acct.id} type="application/atom+xml" rel="alternate" title={acct.name + " feed"} />,
               "firstname" -> SHtml.text(firstName, firstName = _) ,
               "lastname" -> SHtml.text(lastName, lastName = _) ,
               "aboutme" -> SHtml.textarea(aboutMe, aboutMe = _) ,
               //"startDate" -> SHtml.ajaxText("", updateStartDate),
               //"endDate" -> SHtml.ajaxText("", updateEndDate),
               //"graphType" -> SHtml.ajaxSelect(graphChoices, Full("history"), updateGraphType),
               "image" -> SHtml.fileUpload(fph => userImage = Full(fph)), //fu=>setFromUpload(Full(fu))  designer.userImage.asHtml, //<img src={"/graph/" + acctName + "/history"} />,
               "save" -> SHtml.submit( "Speichern", () => saveChanges ) //onSubmitUnit
               //"save" -> SHtml.onSubmitUnit( () => saveChanges )
               //"table" -> entryTable
               )
               //"image" -> <img src={"/graph/" + acctName + "/history"} />
               
          
        }
        case _ => warn("You must be logged in!"); Text("You must be logged in!")
      }
    //}
    //case _ => Text("No account name provided")
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
               "aboutme" -> TextileParser.toHtml(designer.aboutMe.get),
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

