package at.fabricate.liftdev.common
package model

import at.fabricate.liftdev.common.lib.EnumWithDescriptionAndObject
import scala.xml.Elem
import net.liftweb.http.S

	// add a difficulty (one of many that comes from a list of string options)
    object StateEnum extends EnumWithDescriptionAndObject[Elem] {
        
    /*
    val stateText = "project is in"
    val state1 = "concept / idea / draft state"
    val state2 = "early state of development"
    val state3 = "advanced state of development"
    val state4 = "evolved (medium state)"
    val state5 = "advanced state"
    val state6 = "mature state"
    * 
    */
  
    val stateText = "project_state_head"
    val state1 = "project_state_1"
    val state2 = "project_state_2"
    val state3 = "project_state_3"
    val state4 = "project_state_4"
    val state5 = "project_state_5"
    val state6 = "project_state_6"
    val state7 = "project_state_7"
    
        private def wrapSpanWithClass(theClass : String,theText: String) : Elem = 
        <li class="left">
			<span class={theClass}></span>
			<h5 data-lift="L10n.i">{stateText}</h5>
			<h6 data-lift="L10n.i">{theText}</h6>
		</li>
			
			//S.?(
  
	val concept = MultiLanguageValue(state1,wrapSpanWithClass("icon-indication-bar0",state1))
	val dev_early = MultiLanguageValue(state2,wrapSpanWithClass("icon-indication-bar1",state2))
	val dev_advanced = MultiLanguageValue(state3,wrapSpanWithClass("icon-indication-bar2",state3))
	val evolved = MultiLanguageValue(state4,wrapSpanWithClass("icon-indication-bar3",state4))
	val advanced = MultiLanguageValue(state5,wrapSpanWithClass("icon-indication-bar4",state5))
	val mature = MultiLanguageValue(state6,wrapSpanWithClass("icon-indication-bar5",state6))
	val undefined = MultiLanguageValue(state7,wrapSpanWithClass("icon-flag",state7))
	
	val downToMatureList = mature  :: Nil
    val downToAdvanced = advanced   :: downToMatureList
    val downToEvolved = evolved  :: downToAdvanced
    val downToDevAdvanced = dev_advanced     :: downToEvolved
    val downToDevEarly = dev_early     :: downToDevAdvanced
    val downToConcept = concept    :: downToDevEarly
    
      def numberToState(number : Int) = number match {
	  case 1 => Some(concept)	  
	  case 2 => Some(dev_early)
	  case 3 => Some(dev_advanced)
	  case 4 => Some(evolved)
	  case 5 => Some(advanced)
	  case 6 => Some(mature)
	  case 7 => Some(undefined)
	  case _ => None
	  }
	/*
  def textDifficulty(number : Int) = number match {
	  case 1 => Full(kids)	  
	  case 2 => Full(starter)
	  case 3 => Full(average)
	  case 4 => Full(advanced)
	  case 5 => Full(expert)
	  case 6 => Full(genius)
	  case _ => Empty
	  }	
	  * 
	  */
	 def numberStrToState(number : String) = numberToState(number.toInt)
	 
	 // Extractor
	 // do not override the default extractor
	 //def unapply(id: String): Option[ExtendedValue] = numberStrToDifficulty(id)
	}