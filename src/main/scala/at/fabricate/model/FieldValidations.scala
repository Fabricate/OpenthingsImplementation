package at.fabricate
package model

import scala.xml.Text
import net.liftweb.util.FieldError
import net.liftweb.util.FieldIdentifier
import java.util.Date
import java.text.SimpleDateFormat


object FieldValidation {
    /**Definition der Validationsbedingung "Feld darf nicht leer sein"*/
	def notEmpty(field: FieldIdentifier)(content: String) = {
		if (content.trim.length == 0)
		  	// TODO : combine text with properties and field.displayName
			List(FieldError(field, Text("Feld \"Inhalt\" darf nicht leer sein")))
    	 else
    		 List[FieldError]()
	  	} 
    /**Validation für ein Kalenderdatum.*/
	// TODO : useful validation, maybe earliest and latest date in constructor?
  	def isValidDate(field: FieldIdentifier)(date: Date): List[FieldError] = {
	    if (date == null){
	  	  return List(FieldError(field, Text("Datum fehlt")))
      }
  	  /**Frühestes erlaubtes Datum*/
  	  val earliestDate =  new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2009")
	    /**Spätestes erlaubtes Datum*/
      val latestDate 	 = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2020")
	    if (date.before(earliestDate) || date.after(latestDate)){
  	   	return List(FieldError(field, Text(
  	   	  "Ungültiges Datum: Datum bereits vergangen oder zu weit in der Zukunft")))
      }
	    Nil
    }
}