package at.fabricate.liftdev.common
package lib

import scala.xml.Text
import net.liftweb.util.FieldError
import net.liftweb.util.FieldIdentifier
import java.util.Date
import java.text.SimpleDateFormat
import net.liftweb.mapper.MappedField


object FieldValidation {
      /**Definition der Validationsbedingung "Feld darf nicht leer sein"*/
	def minLength(field: MappedField[_,_], length: Int)(content: String) = {
		if (content == null || content.trim.length < length)
			List(FieldError(field, Text("\"%s\" must have more than %s characters".format(field.displayName, length))))
    	 else
    		 List[FieldError]()
	  	}
  def maxLength(field: MappedField[_,_], length: Int)(content: String) = {
    if (content != null && content.trim.length > length)
      List(FieldError(field, Text("\"%s\" must have less than %s characters".format(field.displayName, length))))
    else
      List[FieldError]()
  }
  /**Definition der Validationsbedingung "Feld darf nicht leer sein"*/
	def notEmpty(field: MappedField[_,_])(content: String) = minLength(field, 0)(content)
	
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
