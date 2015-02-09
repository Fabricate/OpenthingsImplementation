package at.fabricate.liftdev.common
package snippet

import net.liftweb.common.Logger
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
import net.liftweb.mapper.KeyedMapper
import net.liftweb.mapper.LongKeyedMapper



// if the comments also want to be paginated with the help of this script, 
// a new subtype can be created where T is MappedType and U is MappedMetaType 
// HINT: to redirect and to sort pagination at least a KeyedMapper is needed!
trait BaseEntityWithTitleDescriptionIconAndCommonFieldsSnippet[T <: BaseEntityWithTitleDescriptionIconAndCommonFields[T]] extends BaseEntityWithTitleAndDescriptionSnippet[T] 
with BaseEntityWithTitleDescriptionAndIconSnippet[T]
with AddCommentSnippet[T]
with AddRatingSnippet[T]
with AddCreatedByUserSnippet[T]
with AddTagsSnippet[T]
//with AddCategoriesSnippet[T]
{
   
//  def localDispatch : DispatchIt = {    
//    case "list" => renderIt(_)
//    case "renderIt" => renderIt(_)
//    case "edit" => edit _
//    case "create" => create _
//    case "view" => view(_)
//    case "paginate" => paginate _
//    case "paginatecss" => paginatecss(_)
//  }
//    orElse super.dispatch
    
  
    
  
//    override def count = 10

//  override def page = List()
 
		  
    

  
    	 
  // internal helper fields that will be chained to create the complete css selector
  //   abstract override
   abstract override def toForm(item : ItemType) : CssSel = {
//     		println("chaining asHtml from BaseRichEntitySnippet")

   (
//       "#icon" #> item.icon .toForm & // will go to the baseiconentitysnippet later on
//    "#initiator *"  #> {<strong>Made by:</strong> item.de} &
    "#licence"  #> item.licence.toForm &
    "#difficulty"  #> item.difficulty.toForm &
    "#state"  #> item.state.toForm
   ) &
        (super.toForm(item))
   }
  
   //   abstract override
   abstract override def asHtml(item : ItemType) : CssSel = {
//     		println("chaining asHtml from BaseRichEntitySnippet")

   (
//       "#icon [src]" #> item.icon .url & // will go to the baseiconentitysnippet later on
//    "#initiator *+"  #> {<strong>Made by:</strong> item.de} &
    "#licence *"  #> item.licence.asHtml &
    "#difficulty"  #> item.difficulty.asHtml  &
    "#state"  #> item.state.asHtml 
   ) &
   (super.asHtml(item))
     
//     , {
//     "#icon [src]" #> item.icon .url &
//     "#comment" #> project.comments.map(comment => bindCommentCSS(comment))  &
//     "#newcomment" #> bindNewCommentCSS
//   })
     
//         "#item" #> {MapperBinder.bindMapper(item,{
//             "#save" #> SHtml.submit( "save", () => 
////               saveAndRedirectToNewInstance((item, success: () => Unit, errors: List[FieldError] => Unit) => saveAndDisplayMessages(item,success,errors, "") , item,
//               saveAndRedirectToNewInstance(saveAndDisplayMessages(_,_:()=>Unit,_:List[FieldError]=>Unit, "") , item)
//               )
//        }) _ } 
  }

}
