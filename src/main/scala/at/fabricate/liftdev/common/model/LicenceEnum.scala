package at.fabricate.liftdev.common
package model

import at.fabricate.liftdev.common.lib.EnumWithDescriptionAndObject
import scala.xml.Elem

        object LicenceEnum extends EnumWithDescriptionAndObject[Elem] {
      
      private def wrapLicenceLink(linkTarget : String, linkText : String, iconClasses : List[String]) : Elem = 
      <a href={linkTarget} target="_blank">{linkText} {iconClasses.map(iClass => iconClass(iClass)) }</a>
//      : _ *
//      List[Elem]
      
      var commercialLicences : List[Value] = List()
      
      var derivableLicences : List[Value] = List()
      
      var allLicences : List[Value] = List()
    
      private def iconClass(theClass : String) : Elem = <span class={theClass}></span>
//      <a href="https://creativecommons.org/licenses/by-nc/3.0/" target="_blank">Attribution 4.0 International <span class="icon-cc"></span> <span class="icon-cc-by"></span></a>
	// create CC Licences
      for (
          (versionShort, versionLong) <- List(
//              "1.0"->"1.0 Generic","2.0"->"2.0 Generic","2.5"->"2.5 Generic",
              "3.0"->"3.0 Unported","4.0"->"4.0 International"); 
          middleVal <- List("","nc");
          lastVal <- List("","sa","nd")
          ) yield {
        var link = "by"
        var title = "BY"
        var icons = "icon-cc-by" :: "icon-cc" :: Nil
        if (middleVal.length > 1) {
          link = link + "-" + middleVal
          title = title + "-" + middleVal.toUpperCase
          icons = "icon-cc-"+middleVal :: icons
        }
        if (lastVal.length > 1) {
          link = link + "-" + lastVal
          title = title + "-" + lastVal.toUpperCase
          icons = "icon-cc-"+lastVal :: icons
        }
      var licence = Value("Creatice Commons %s %s".format(versionShort,title),wrapLicenceLink(
	    "https://creativecommons.org/licenses/%s/%s/".format(link,versionShort),
	    "Attribution %s".format(versionLong),
	    icons.reverse
	    ))
	  if (middleVal != "nc") commercialLicences = licence :: commercialLicences
	  if (lastVal != "nd" ) derivableLicences = licence :: derivableLicences
	  allLicences = licence :: allLicences
	  licence
      }
      val publicDomain = Value("Public Domain Dedication",wrapLicenceLink(
	    "https://creativecommons.org/publicdomain/zero/1.0/",
	    "Public Domain Dedication CC0 1.0 Universal (CC0 1.0) ",
	    List("icon-cc","icon-cc-zero")
	    ))
	    commercialLicences = publicDomain :: commercialLicences
	    derivableLicences = publicDomain :: derivableLicences
	    allLicences = publicDomain :: allLicences
	    
	}