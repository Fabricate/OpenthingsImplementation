package at.fabricate.liftdev.common
package model

import at.fabricate.liftdev.common.lib.EnumWithDescriptionAndObject
import scala.xml.Elem

	// add a difficulty (one of many that comes from a list of string options)
    object StateEnum extends EnumWithDescriptionAndObject[Elem] {
          
    val stateText = "project is in"
    val state1 = "concept / idea / draft state"
    val state2 = "early state of development"
    val state3 = "advanced state of development"
    val state4 = "evolved (medium state)"
    val state5 = "advanced state"
    val state6 = "mature state"
      
        private def wrapSpanWithClass(theClass : String,theText: String) : Elem = 
        <li class="left">
			<span class={theClass}></span>
			<h5>{stateText}</h5>
			<h6>{theText}</h6>
		</li>
  
	val concept = Value(state1,wrapSpanWithClass("icon-indication-bar0",state1))
	val dev_early = Value(state2,wrapSpanWithClass("icon-indication-bar1",state2))
	val dev_advanced = Value(state3,wrapSpanWithClass("icon-indication-bar2",state3))
	val evolved = Value(state4,wrapSpanWithClass("icon-indication-bar3",state4))
	val advanced = Value(state5,wrapSpanWithClass("icon-indication-bar4",state5))
	val mature = Value(state6,wrapSpanWithClass("icon-indication-bar5",state6))
	
	val downToMatureList = mature  :: Nil
    val downToAdvanced = advanced   :: downToMatureList
    val downToEvolved = evolved  :: downToAdvanced
    val downToDevAdvanced = dev_advanced     :: downToEvolved
    val downToDevEarly = dev_early     :: downToDevAdvanced
    val downToConcept = concept    :: downToDevEarly
	}