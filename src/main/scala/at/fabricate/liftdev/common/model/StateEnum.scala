package at.fabricate.liftdev.common
package model

import at.fabricate.liftdev.common.lib.EnumWithDescriptionAndObject
import scala.xml.Elem

	// add a difficulty (one of many that comes from a list of string options)
    object StateEnum extends EnumWithDescriptionAndObject[Elem] {
      
      private def wrapSpanWithClass(theClass : String) : Elem = <span class={theClass}></span>
    
	val concept = Value("concept / idea",wrapSpanWithClass("icon-state1"))
	val early_dev = Value("early development state",wrapSpanWithClass("icon-state2"))
	val evolved = Value("evolved (medium state)",wrapSpanWithClass("icon-state3"))
	val late_dev = Value("late development state",wrapSpanWithClass("icon-state4"))
	val mature = Value("mature",wrapSpanWithClass("icon-state5"))
	}