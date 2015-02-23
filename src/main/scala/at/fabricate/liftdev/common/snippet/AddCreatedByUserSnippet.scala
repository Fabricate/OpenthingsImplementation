package at.fabricate.liftdev.common
package snippet

import model.AddRating
import model.AddRatingMeta
import net.liftweb.util.CssSel
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq
import scala.xml.Text
import model.BaseEntity
import model.BaseMetaEntityWithTitleAndDescription
import model.BaseEntityWithTitleAndDescription
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.common.Empty
import at.fabricate.liftdev.common.model.AddCreatedByUser
import net.liftweb.common.Full
import net.liftweb.common.Box

trait AddCreatedByUserSnippet[T <: BaseEntityWithTitleAndDescription[T] with AddCreatedByUser[T]] extends BaseEntityWithTitleAndDescriptionSnippet[T] {
  
  def theUserSnippet : BaseEntityWithTitleAndDescriptionSnippet[_]
  
  
  abstract override def asHtml(item : ItemType) : CssSel = {
		 
    val boxedUser : Box[item.TheUserType] = item.createdByUser
    val initiatorSelectors : CssSel = boxedUser match {
      case Full(initiatingUser) => ("#initiator *+" #> <a href={theUserSnippet.urlToViewItem(initiatingUser)}>{"%s".format(initiatingUser.defaultTranslation.getObjectOrHead.title.get )}</a>  &
     "#contactinitiator [href]" #> theUserSnippet.urlToViewItem(initiatingUser))
      case _ => ("#initiator *+" #> "Unknown Initiator!"  &
     "#contactinitiator [href]" #> "")
    }
     initiatorSelectors &
     // chain the css selectors 
     (super.asHtml(item))
  }
  
  abstract override def toForm(item : ItemType) : CssSel = super.toForm(item)
}
