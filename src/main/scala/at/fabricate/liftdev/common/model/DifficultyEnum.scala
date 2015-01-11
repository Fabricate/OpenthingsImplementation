package at.fabricate.liftdev.common
package model

import at.fabricate.liftdev.common.lib.EnumWithDescriptionAndObject
import scala.xml.Elem

	// add a difficulty (one of many that comes from a list of string options)
    object DifficultyEnum extends EnumWithDescriptionAndObject[Elem] {
      
      private def wrapSpanWithClass(theClass : String) : Elem = <span class={theClass}></span>
    
	val kids = Value("Kids",wrapSpanWithClass("icon-difficulty1"))
	val starter = Value("Starter",wrapSpanWithClass("icon-difficulty2"))
	val average = Value("Average",wrapSpanWithClass("icon-difficulty3"))
	val advanced = Value("Advanced",wrapSpanWithClass("icon-difficulty4"))
	val expert = Value("Expert",wrapSpanWithClass("icon-difficulty5"))
	val genius = Value("Genius",wrapSpanWithClass("icon-difficulty6"))
	
	val upToKidsList = kids :: Nil
    val upToStarterList = starter  :: upToKidsList
    val upToAverageList = average  :: upToStarterList
    val upToAdvancedList = advanced   :: upToAverageList
    val upToExpertList = expert   :: upToAdvancedList
    val upToGeniusList = genius   :: upToExpertList
	}