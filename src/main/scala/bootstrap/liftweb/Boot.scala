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
import at.fabricate.openthings.rest._
import org.apache.commons.fileupload.FileUploadBase.FileUploadIOException
import at.fabricate.openthings.lib.AccessControl
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.internet.MimeMessage
import at.fabricate.liftdev.common.lib.UrlLocalizer
import at.fabricate.openthings.api.ProjectREST
import net.liftweb.http.provider.HTTPParam
import at.fabricate.liftdev.common.snippet.L10n

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
    
    // initialize the localizer
    UrlLocalizer.init() 
    
    // has to be a list of BaseMetaMapper entities
    val itemsToSchemify : List[BaseMetaMapper] = Project.getItemsToSchemify ::: 
    User.getItemsToSchemify ::: Tag.getItemsToSchemify ::: Skill.getItemsToSchemify ::: Image.getItemsToSchemify ::: 
      List[BaseMetaMapper](Tool, UserHasTools)
    Schemifier.schemify(true, Schemifier.infoF _, itemsToSchemify :_*)
        
    // initialize the Mapper instances
    // stuff that can only be done at the Startup phase
    // adds for example serving of images and uploading of files to REST API to the LiftRules
    User.init
    Project.init
    Image.init
    
    // initialize the rest api
    LiftRules.dispatch.append(ProjectREST)
    
    // enable CORS for the REST API
    LiftRules.supplementalHeaders.default.set(
      List(("Access-Control-Allow-Origin","*"),
        ("Access-Control-Allow-Credentials", "true"),
        ("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS"),
        ("Access-Control-Allow-Headers", "WWW-Authenticate,Keep-Alive,User-Agent,X-Requested-With,Cache-Control,Content-Type")
      ))
      /*
      * 
      */

      /*
       * 
       nginx config for CORS:
       // just send the headers for the rest api
        location / { 
         if ($request_method = 'OPTIONS') { 
           add_header 'Access-Control-Allow-Origin' '*'; 
           add_header 'Access-Control-Allow-Credentials' 'true'; 
           add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, OPTIONS'; 
           add_header 'Access-Control-Allow-Headers' 'Keep-Alive,User-Agent,X-Requested-With,.......'; 
           add_header 'Access-Control-Max-Age' 1728000; 
           add_header 'Content-Type' 'text/plain charset=UTF-8'; 
           add_header 'Content-Length' 0; 
           return 200; 
        } 
:
}

http://stackoverflow.com/questions/10034931/http-options-verb-handling-with-lift
https://www.assembla.com/spaces/liftweb/wiki/Cross_Origin_Resource_Sharing
       */
    
    // where to search snippet
    LiftRules.addToPackages("at.fabricate.openthings")
    
    LiftRules.snippetDispatch.append {
      case "Search" => SearchSnippet
      case "Tag" => TagSnippet
      //case "Image" => ImageSnippet
      case "Skill" => SkillSnippet
      case "Project" => ProjectSnippet
      case "User" => UserSnippet
      case "Login" => LoginSnippet
      case "Feedback" => FeedbackSnippet
      case "ContactMaker" => ContactMakerSnippet
      case "Featured" => FeaturedSnippet
      case "L10n" => L10n
    }
    
    
    val userRewrites = UserSnippet.generateRewrites
    
    val projectRewrites =  ProjectSnippet.generateRewrites
    
    val searchRewrites = SearchSnippet.generateRewrites

    val tagRewrites = TagSnippet.generateRewrites

    val skillRewrites = SkillSnippet.generateRewrites
       
    val loginRewrites = LoginSnippet.generateRewrites
       
    //val imageRewrites = ImageSnippet.generateRewrites

    // Set up some rewrites
    LiftRules.statelessRewrite.append (userRewrites.orElse
        (searchRewrites).orElse
        (tagRewrites).orElse
        (skillRewrites).orElse
        (projectRewrites).orElse
        //(imageRewrites).orElse
        (loginRewrites) )


    
    val IfLoggedIn = If(() => User.currentUser.isDefined, "You must be logged in")
    
    // links can be defined more often than once
//    SiteMap.enforceUniqueLinks = false

    // serve the sitemap
    LiftRules.statelessDispatch.append(Sitemap)
    //LiftRules.liftRequest.append {
    //    case Req("sitemap" :: Nil, "xml", _) => false
    //}
    
    def menu: List[Menu] = 
    List[Menu](Menu.i("Home") / "index" >> Hidden,
               Menu.i("Sitemap") / "sitemap" / ** >> Hidden,
               Menu.i("Search Designers") / "searchDesigner",                 
               Menu.i("Public Data") / "public" / ** >> Hidden,
               Menu.i("SASS") / "sass" / ** >> Hidden,
// this seems to be not necessary
               Menu.i("API") / "api" / ** >> Hidden,
               Menu.i("Page not found!") / "404"  >> Hidden,               
               // TODO: double definition, dont do that here
               //Menu.i("Validate") / "validate_user" / * >> Hidden,
               Menu.i("Static") / "static" / ** >> Hidden
               ) :::  LoginSnippet.getMenu ::: ProjectSnippet.getMenu  ::: 
               UserSnippet.getMenu ::: TagSnippet.getMenu ::: SkillSnippet.getMenu :::
               SearchSnippet.getMenu ::: Tool.menus :::
               FeedbackSnippet.getMenu

    LiftRules.setSiteMap(SiteMap(menu :_*))
    
    
    // make the messages disappear after some time
    LiftRules.noticesAutoFadeOut.default.set((noticeType: NoticeType.Value) => Full((10 seconds, 2 seconds)))
    
    // use a custom 404 page (404.html or 404[locale].html)
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => 
        NotFoundAsTemplate(ParsePath(List("404"),"html",false,false))
    })


    def sitemapMutators = User.sitemapMutator
    
    // increase the filesize for uploads (in Bytes)
    LiftRules.maxMimeFileSize = 100*1024*1024
    LiftRules.maxMimeSize = 105*1024*1024
    
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
    //LiftRules.htmlProperties.default.set((r: Req) =>
    //  new Html5Properties(r.userAgent))    
    LiftRules.htmlProperties.default.set({ request: Req =>
      request.path.partPath match {
        case "sitemap" :: Nil => OldHtmlProperties(request.userAgent)
        case _                => Html5Properties(request.userAgent)
      }
    })

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
  }
}
