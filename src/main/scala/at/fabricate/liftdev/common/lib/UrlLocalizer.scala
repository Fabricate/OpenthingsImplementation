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
  
//  // the locale used for the actual request -  
//  object requestLocale extends RequestMemoize[Int, Locale] {
//      override protected def __nameSalt = randomString(20)
//  }
  
  // will be used if we can find no locale!
  val defaultLocale = Locale.ENGLISH

  // will go to the requestLocale once it is set
  @volatile 
  object sessionSiteLocale extends SessionVar[Box[Locale]](Empty)
  
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
//    if (contentLocale.set_?) None // don’t duplicate
//  else locales.get(in) // if it’s a valid locale, it matches

  /**
   * Calculate the Locale
   */
  def getContentLocale : Locale = 
    if (contentLocale.set_?) {
      //println("content locale is "+contentLocale.get.getDisplayLanguage())
      contentLocale.get
    }
    else {
      //println("locale not set - using default!")
      defaultLocale 
    }
  
  def setSiteLocale(aLocale : Box[Locale]) = sessionSiteLocale.set(aLocale)
    
  def getSiteLocale : Locale = 
//    if ( sessionSiteLocale.set_? ) sessionSiteLocale.get
//    else defaultLocale
    sessionSiteLocale.openOr(defaultLocale )
    
  def calcLocaleFromURL(in: Box[HTTPRequest]): Locale = getContentLocale
  
//    if (contentLocale.set_?) contentLocale.get
//    else oldLocalizeFunc(in)

  def calcLocaleFromRequest(request: Box[HTTPRequest]) : Locale =
////      requestLocale(request.hashCode, (
//    if ( sessionSiteLocale.set_? ) {
//      println("using session locale "+sessionSiteLocale.get.getDisplayLanguage())
//      // stack overflow -> a good sign ;-)!
//      //println("S.locale "+S.locale)
//      sessionSiteLocale.get
//    }
//    else 
    // dummy
//    Locale.ENGLISH
    sessionSiteLocale.openOr {
      val requestLocale = 
      (for {
//        r <- request
//        p <- tryo(r.param("hl").head.split(Array('_', '-')))
        listOfLanguages <- tryo(S.getRequestHeader("Accept-Language").get.split(Array(',')).toList.map(_.split(Array('_', '-'))))
      } yield listOfLanguages.map {
        case Array(lang) => new Locale(lang)
        case Array(lang, country) => new Locale(lang, country)
      })
      //.openOr(defaultLocale)
      //sessionSiteLocale.set().openOr(defaultLocale )
//      sessionSiteLocale.get
      requestLocale match {
        case Full(aListOfLocales) => {
          val useLocale = aListOfLocales.head
          println("requested locales: "+aListOfLocales.mkString(","))
          println("use locale: "+useLocale.getDisplayLanguage(useLocale))
          setSiteLocale(Full(useLocale)); useLocale
        }
        case _ => {
          println("no locale in request")
          Locale.getDefault
        }
      }
    }
      //end loxalization block
  /**
   * Initialize the locale
   */
  def init() {
    // hook into Lift
    LiftRules.localeCalculator = calcLocaleFromRequest _
     // calcLocaleFromURL
    

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