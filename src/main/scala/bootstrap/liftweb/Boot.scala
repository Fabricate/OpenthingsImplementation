package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._
import common._
import http._
import js.jquery.JQueryArtifacts
import sitemap._
import Loc._
import mapper._
import net.liftmodules.JQueryModule
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import at.fabricate.openthings.snippet._
import at.fabricate.openthings.model._
import org.apache.commons.fileupload.FileUploadBase.FileUploadIOException
import at.fabricate.openthings.lib.AccessControl
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.internet.MimeMessage

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    
    // Set up the database connection
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(util.DefaultConnectionIdentifier, vendor)
    }
    
    // configure mailing (setup authentification)
    Mailer.authenticator = for {
    	user <- Props.get("mail.user")
    	pass <- Props.get("mail.password")
    } yield new Authenticator {
    	override def getPasswordAuthentication =
    			new PasswordAuthentication(user,pass)
    }
    
//    // just do something else if a mail wants to be sent
//    Mailer.devModeSend.default.set( (m: MimeMessage) =>
//    	println("Would have sent: "+m.getContent)
//    )
    
    //AutoComplete.init

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    
    // DID THAT KILL LIFT COMPILATION???? -> Yea, this was mixing between types
    // has to be a list of BaseMetaMapper entities
    val itemsToSchemify : List[BaseMetaMapper] = Project.getItemsToSchemify ::: 
    User.getItemsToSchemify ::: List[BaseMetaMapper](Tool, UserHasTools) 
    Schemifier.schemify(true, Schemifier.infoF _, itemsToSchemify :_*)
        
    // initialize the Mapper instances
    // stuff that can only be done at the Startup phase
    // adds for example serving of images and uploading of files to REST API to the LiftRules
    
    User.init
    Project.init
    
    // where to search snippet
    LiftRules.addToPackages("at.fabricate.openthings")
    
    LiftRules.snippetDispatch.append {
      case "Search" => SearchSnippet
      case "Project" => ProjectSnippet
      case "User" => UserSnippet
      case "Login" => LoginSnippet
    }
    
    //LiftRules.dispatch.append(ImageLogic.matcher)
    //LiftRules.statelessDispatch.append(FileUploadREST)
//    LiftRules.dispatch.append(FileUploadREST)
    
//    val userRewrites : PartialFunction[RewriteRequest,RewriteResponse] = {
//      case RewriteRequest(ParsePath(List("designer", "edit"), _, _, _), _, _) =>
//	      RewriteResponse("editDesigner" :: Nil)
//      case RewriteRequest(ParsePath(List("designer", "search"), _, _, _), _, _) =>
//	      RewriteResponse("searchDesigner" :: Nil)	      
//      case RewriteRequest(ParsePath(List("designer", "list"), _, _, _), _, _) =>
//	      RewriteResponse("listDesigner" :: Nil)	 
//      case RewriteRequest(ParsePath(List("designer", "list", "random"), _, _, _), _, _) =>
//	      RewriteResponse("listDesigner" :: Nil, Map("type" -> urlDecode("random")))		      
//      case RewriteRequest(ParsePath(List("designer", designerID), _, _, _), _, _) =>
//	      RewriteResponse("viewDesigner" :: Nil, Map("id" -> urlDecode(designerID)))
//    }
    
//    val logonRewrites = Login.generateRewrites
    
//    val projectRewrites : PartialFunction[RewriteRequest,RewriteResponse] = {
//      	case RewriteRequest(ParsePath(List("project", "index"), _, _, _), _, _) =>
//      		RewriteResponse("listProject" :: Nil)
//      	case RewriteRequest(ParsePath(List("project", "list"), _, _, _), _, _) =>
//      		RewriteResponse("listProject" :: Nil)
//      	case RewriteRequest(ParsePath(List("project", "edit",projectID), _, _, _), _, _) =>
//      		RewriteResponse("editProject" :: Nil, Map("id" -> urlDecode(projectID)))
//      	case RewriteRequest(ParsePath(List("project", "edit"), _, _, _), _, _) =>
//      		RewriteResponse("editProject" :: Nil)
//      	case RewriteRequest(ParsePath(List("project","view", projectID), _, _, _), _, _) =>
//      		RewriteResponse("viewProject" :: Nil, Map("id" -> urlDecode(projectID)))
//    }
    
    val userRewrites = UserSnippet.generateRewrites
    
    val projectRewrites =  ProjectSnippet.generateRewrites
    
    val searchRewrites = SearchSnippet.generateRewrites
   
    // Set up some rewrites
    LiftRules.statelessRewrite.append (userRewrites.orElse
        (searchRewrites).orElse
        (projectRewrites) )
//    
    // TODO : aufraumen, sauberes Menue !!!
    /*
    val homeMenu = Menu(Loc("Home Page", "index" :: Nil, "Home"))
      
    val projectMenu = Project.menus
    
    val toolMenu = Tool.menus
    
    val userMenu = User.menus
    
    val designerMenu = Menu(Loc("Designer Page", "viewDesigner" :: Nil, "Designers"))
    val editDesignerMenu = Menu(Loc("Edit Designer", "editDesigner" :: Nil, "Edit Designer"))

      //Menu.i("View Account") / "viewAcct" /
    
    val menus = List[Menu](homeMenu, designerMenu, editDesignerMenu) ::: userMenu ::: toolMenu ::: projectMenu
    
    def sitemap = SiteMap(menus : _* )
    
        LiftRules.setSiteMap(sitemap)
    
    */
    
    val IfLoggedIn = If(() => User.currentUser.isDefined, "You must be logged in")
    
    // links can be defined more often than once
//    SiteMap.enforceUniqueLinks = false
    
    def menu: List[Menu] = 
    List[Menu](Menu.i("Home") / "index" >> Hidden,
//               Menu.i("Edit my profile") / "editDesigner" >> Hidden >> AccessControl.loggedIn ,  //>> IfLoggedIn,
//               Menu.i("View Designer") / "viewDesigner" / ** >> Hidden,
//               Menu.i("List Designers") / "listDesigner" / ** >> Hidden,
               Menu.i("Test page") / "testpage" / ** >> Hidden,
               Menu.i("Search Designers") / "searchDesigner",                 
               Menu.i("Public Data") / "public" / ** >> Hidden,
               Menu.i("SASS") / "sass" / ** >> Hidden,
//               Menu.i("Navigation Menu") / "navigation"  >> Hidden,

               Menu.i("Page not found!") / "404"  >> Hidden,
               



               Menu.i("Static") / "static" / ** >> Hidden
               //Menu(Loc("Static", Link(List("static"), true, "/about_us/index"), "About us"))
               ) :::  LoginSnippet.getMenu ::: ProjectSnippet.getMenu ::: UserSnippet.getMenu ::: SearchSnippet.getMenu ::: Tool.menus ::: User.menus
  
    LiftRules.setSiteMap(SiteMap(menu :_*))
    
    
    // make the messages disappear after some time
    LiftRules.noticesAutoFadeOut.default.set((noticeType: NoticeType.Value) => Full((1 seconds, 2 seconds)))
    
    // use a custom 404 page (404.html or 404[locale].html)
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => 
        NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })
    
    // Build SiteMap
    /*
    def sitemap = SiteMap(
      Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))
	       * 
	       */

    def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    //LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))
    
    /*
    def progressPrinter(bytesRead: Long, contentLength: Long, fieldIndex: Int) {
    	println("Read %d of %d for %d" format (bytesRead, contentLength, fieldIndex))
    }

    LiftRules.progressListener = progressPrinter
    * 
    */
    
    // increase the filesize for uploads (in Bytes)
    LiftRules.maxMimeFileSize = 200000000
    LiftRules.maxMimeSize = 400000000
    
    // catch exceptions at max fileupload size
    // **** Did not work:
//    [warn] Class javax.servlet.http.HttpServletRequest not found - continuing with a stub.
    LiftRules.exceptionHandler.prepend {
		case (_, _, x : FileUploadIOException) =>
			ResponseWithReason(BadResponse(), "Unable to process file. Too large?")
	}

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery191
    JQueryModule.init()

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))    

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
  }
}
