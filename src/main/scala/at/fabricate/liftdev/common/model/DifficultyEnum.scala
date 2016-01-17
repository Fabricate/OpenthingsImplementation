package at.fabricate.liftdev.common
package model

import at.fabricate.liftdev.common.lib.EnumWithDescriptionAndObject
import scala.xml.Elem
import net.liftweb.common.Full
import net.liftweb.common.Empty

	// add a difficulty (one of many that comes from a list of string options)
    object DifficultyEnum extends EnumWithDescriptionAndObject[Elem] {
  
    val difficultyText = "suitable for"
    val stage1 = "Kids"
    val stage2 = "Starter"
    val stage3 = "Average"
    val stage4 = "Advanced"
    val stage5 = "Expert"
    val stage6 = "Genius"
    
      
      private def wrapSpanWithClass(theClass : String,theText: String) : Elem = 
        <li class="left">
			<span class={theClass}></span>
			<h5>{difficultyText}</h5>
			<h6>{theText}</h6>
		</li>
    
	val kids = Value(stage1,wrapSpanWithClass("icon-indication-bar0",stage1))
	val starter = Value(stage2,wrapSpanWithClass("icon-indication-bar1",stage2))
	val average = Value(stage3,wrapSpanWithClass("icon-indication-bar2",stage3))
	val advanced = Value(stage4,wrapSpanWithClass("icon-indication-bar3",stage4))
	val expert = Value(stage5,wrapSpanWithClass("icon-indication-bar4",stage5))
	val genius = Value(stage6,wrapSpanWithClass("icon-indication-bar5",stage6))
	
	val upToKidsList = kids :: Nil
    val upToStarterList = starter  :: upToKidsList
    val upToAverageList = average  :: upToStarterList
    val upToAdvancedList = advanced   :: upToAverageList
    val upToExpertList = expert   :: upToAdvancedList
    val upToGeniusList = genius   :: upToExpertList
    
  def numberToDifficulty(number : Int) = number match {
	  case 1 => Some(kids)	  
	  case 2 => Some(starter)
	  case 3 => Some(average)
	  case 4 => Some(advanced)
	  case 5 => Some(expert)
	  case 6 => Some(genius)
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
	 def numberStrToDifficulty(number : String) = numberToDifficulty(number.toInt)
	 
	 // Extractor
	 // do not override the default extractor
	 //def unapply(id: String): Option[ExtendedValue] = numberStrToDifficulty(id)
	}