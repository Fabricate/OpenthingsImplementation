package at.fabricate.liftdev.common
package model

import at.fabricate.liftdev.common.lib.EnumWithDescriptionAndObject
import scala.xml.Elem

	// add a difficulty (one of many that comes from a list of string options)
    object StateEnum extends EnumWithDescriptionAndObject[Elem] {
      
      private def wrapSpanWithClass(theClass : String) : Elem = <span class={theClass}></span>
    
	val concept = Value("concept / idea / draft",wrapSpanWithClass("icon-state1"))
	val dev_early = Value("early state of development",wrapSpanWithClass("icon-state2"))
	val dev_advanced = Value("advanced state of development",wrapSpanWithClass("icon-state3"))
	val evolved = Value("evolved (medium state)",wrapSpanWithClass("icon-state4"))
	val advanced = Value("advanced",wrapSpanWithClass("icon-state5"))
	val mature = Value("mature",wrapSpanWithClass("icon-state6"))
	
	val downToMatureList = mature  :: Nil
    val downToAdvanced = advanced   :: downToMatureList
    val downToEvolved = evolved  :: downToAdvanced
    val downToDevAdvanced = dev_advanced     :: downToEvolved
    val downToDevEarly = dev_early     :: downToDevAdvanced
    val downToConcept = concept    :: downToDevEarly
	}