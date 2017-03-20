package at.fabricate.liftdev.common
package snippet

import net.liftweb.http.DispatchSnippet
import model.BaseEntityWithTitleDescriptionIconAndCommonFields
import scala.xml.NodeSeq
import net.liftweb.mapper.StartAt
import net.liftweb.mapper.MaxRows
import net.liftweb.mapper.Descending
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.IdPK
import net.liftweb.http.S
import model.MatchByID
import scala.xml.Text
import net.liftweb.mapper.Mapper
import net.liftweb.util.FieldError
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsCmd
import net.liftweb.util.Helpers._
import net.liftweb.http.js.jquery.JqJsCmds.DisplayMessage
import net.liftweb.util.CssSel
import net.liftweb.http.SHtml
//import at.fabricate.lib.MapperBinder
import net.liftweb.mapper.KeyedMapper
import net.liftweb.mapper.LongKeyedMapper
//import model.Project
import model.BaseEntityWithTitleDescriptionAndIcon



// if the comments also want to be paginated with the help of this script, 
// a new subtype can be created where T is MappedType and U is MappedMetaType 
// HINT: to redirect and to sort pagination at least a KeyedMapper is needed!
trait BaseEntityWithTitleDescriptionAndIconSnippet[T <: BaseEntityWithTitleDescriptionAndIcon[T]] extends BaseEntityWithTitleAndDescriptionSnippet[T] {


  
    	 
  // internal helper fields that will be chained to create the complete css selector
  //   abstract override
   abstract override def toForm(item : ItemType) : CssSel = {
   (
       "#icon" #> item.icon .toForm
   ) &
        (super.toForm(item))
   }
  
   //   abstract override
   abstract override def asHtml(item : ItemType) : CssSel = {
   (
       "#icon [src]" #> item.icon .url 
   ) &
   (super.asHtml(item))
     

  }

}
