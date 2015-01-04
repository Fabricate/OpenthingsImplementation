package at.fabricate.liftdev.common
package lib

import net.liftweb.common.Full
import net.liftweb.common.Empty

  class MatchPath(path : List[String]){
	def unapply(in : List[String]) : Option[List[String]] = 
//	  path.map(string => new MatchString(string))
	  if (in == path)
	    Full(in)
	  else
	    Empty
}

  class MatchString(matchingString : String){
    def unapply(in: String): Option[String] =
    		if (in == matchingString)
    		  Full(in)
    		else
    		  Empty
  }
