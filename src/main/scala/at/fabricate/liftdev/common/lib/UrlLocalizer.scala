package at.fabricate.liftdev.common
package lib

import net.liftweb._
import net.liftweb.http._
import net.liftweb.http.provider._
import net.liftweb.common._
import net.liftweb.util._
import net.liftweb.util.Helpers._
import java.util.Locale

object UrlLocalizer {
  // capture the old localization function
  val oldLocalizeFunc = LiftRules.localeCalculator
  
  var isInitialized = false
  
  val available_locales_templates = List(new Locale("en"),new Locale("nl"),new Locale("de"))
  
  
  // will be used if we can find no locale!
  val defaultLocale = Locale.ENGLISH

  // will go to the requestLocale once it is set
//  @volatile 
  object sessionSiteLocale extends SessionVar[Box[Locale]](Full(calcLocaleFromRequest))
  
  // will be used to display content in different
  object contentLocale extends RequestVar(defaultLocale)

  /**
   * What are the available locales?
   */
  val locales: Map[String, Locale] =
    Map(Locale.getAvailableLocales.map(l => l.getLanguage -> l) :_*)


  val allLanguages = Locale.getISOLanguages().toList.map(new Locale(_))

  /**
   * Extract the locale
   */
  def unapply(in: String): Option[Locale] = locales.get(in)

  /**
   * Calculate the Locale
   */
  def getContentLocale : Locale = 
    if (contentLocale.set_?) {
      contentLocale.get
    }
    else {
      defaultLocale 
    }
  
  def setSiteLocale(aLocale : Box[Locale]) = sessionSiteLocale.set(aLocale)
    
  def getSiteLocale : Locale = 
    sessionSiteLocale.openOr(defaultLocale )
    
  def calcLocaleFromURL(in: Box[HTTPRequest]): Locale = getContentLocale
  
  def getLocaleFromRequest(request: Box[HTTPRequest]) : Locale = sessionSiteLocale.is.openOr { calcLocaleFromRequest }

  def calcLocaleFromRequest() : Locale = {
    
//    println("session locale not found")
      val requestLocale = 
      (for {
        listOfLanguages <- tryo(S.getRequestHeader("Accept-Language").openOrThrowException("Empty Box opened").split(Array(',')).toList.map(_.split(Array('_', '-'))))
      } yield listOfLanguages.map {
        case Array(lang) => new Locale(lang)
        case Array(lang, country) => new Locale(lang, country)
      })

      requestLocale match {
        case Full(aListOfLocales) => {
          // use a locale if the template translation is available for it
          // or use english as a default          
          var locale_found = false;
          var the_locale = defaultLocale//.getDefault
          for (aLocale : Locale <- aListOfLocales){
//            println("Locale: "+aLocale)
            if (! locale_found && available_locales_templates.find{ oneLocale => oneLocale.getLanguage ==  aLocale.getLanguage}.isDefined ){
//              println("requested locales: "+aListOfLocales.mkString(","))
//              println("use locale: "+aLocale.getDisplayLanguage(aLocale))
              setSiteLocale(Full(aLocale)) 
              the_locale = aLocale
              locale_found = true
            }
          }
//          if (!locale_found) {
//            println("no templates exist for required locales")
//          }
          the_locale
        }
        case _ => {
//          println("no locale in request")
          //Locale.getDefault
          setSiteLocale(Full(defaultLocale))
          defaultLocale
        }
      }
    }

  /**
   * Initialize the locale
   */
  def init() {
    // hook into Lift
    LiftRules.localeCalculator = getLocaleFromRequest _
    

    // rewrite requests with a locale at the head
    // of the path
    LiftRules.statelessRewrite.append {
      case RewriteRequest(ParsePath(UrlLocalizer(locale) :: rest, _, _, _), _, _) => {
        contentLocale.set(locale)
        RewriteResponse(rest)
      }
    }
    isInitialized = true
  }
}