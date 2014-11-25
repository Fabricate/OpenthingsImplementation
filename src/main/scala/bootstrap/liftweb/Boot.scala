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
import at.fabricate.model._
import net.liftmodules.JQueryModule
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import at.fabricate.snippet.Designer
import at.fabricate.lib.ImageLogic
import net.liftmodules.widgets.autocomplete.AutoComplete



/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(util.DefaultConnectionIdentifier, vendor)
    }
    
    //AutoComplete.init

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, Project, Tool, UserHasTools)

    // where to search snippet
    LiftRules.addToPackages("at.fabricate")
    
    LiftRules.snippetDispatch.append {
      case "Designer" => Designer
      //case "ListDesigners" => ListDesigners
    }
    
    LiftRules.dispatch.append(ImageLogic.matcher)

    
    // Set up some rewrites
    LiftRules.statelessRewrite.append {
      case RewriteRequest(ParsePath(List("designer", "edit"), _, _, _), _, _) =>
	      RewriteResponse("editDesigner" :: Nil)
      case RewriteRequest(ParsePath(List("designer", "list"), _, _, _), _, _) =>
	      RewriteResponse("listDesigner" :: Nil)	      
      case RewriteRequest(ParsePath(List("designer", designerID), _, _, _), _, _) =>
	      RewriteResponse("viewDesigner" :: Nil, Map("id" -> urlDecode(designerID)))

      case RewriteRequest(ParsePath(List("login"), _, _, _), _, _) =>
	      RewriteResponse("user" :: "login" :: Nil)
      case RewriteRequest(ParsePath(List("logout"), _, _, _), _, _) =>
	      RewriteResponse("user" :: "logout" :: Nil)
	      
      case RewriteRequest(ParsePath(List("sign_up"), _, _, _), _, _) =>
	      RewriteResponse("user" :: "sign_up" :: Nil)
      case RewriteRequest(ParsePath(List("lost_password"), _, _, _), _, _) =>
	      RewriteResponse("user" :: "lost_password" :: Nil)	  	      
    
    }
    
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
    
    
    def menu: List[Menu] = 
    List[Menu](Menu.i("Home") / "index" >> Hidden,
               Menu.i("Edit my profile") / "editDesigner" >> Hidden >> AccessControl.loggedIn ,  //>> IfLoggedIn,
               Menu.i("View Designer") / "viewDesigner" / ** >> Hidden,
               Menu.i("List Designers") / "listDesigner" / ** >> Hidden,               
               Menu.i("Public Data") / "public" / ** >> Hidden,
               Menu.i("SASS") / "sass" / ** >> Hidden,
//               Menu.i("Navigation Menu") / "navigation"  >> Hidden,


               Menu.i("About Us") / "about_us" / **
               //Menu(Loc("Static", Link(List("static"), true, "/about_us/index"), "About us"))
               ) :::  User.sitemap ::: Tool.menus ::: Project.menus
  
    LiftRules.setSiteMap(SiteMap(menu :_*))
    
    
    
    
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
