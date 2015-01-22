package at.fabricate.liftdev.common
package snippet

import net.liftweb.util._
import net.liftweb.common._
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
import at.fabricate.liftdev.common.model.AddTags
import at.fabricate.liftdev.common.model.MatchByID
import at.fabricate.liftdev.common.model.GeneralTag
import at.fabricate.liftdev.common.model.GeneralTagMeta
import net.liftweb.http.js.JsCmd
import at.fabricate.liftdev.common.model.AddTagsMeta
import at.fabricate.openthings.model.Project
import net.liftweb.http.RequestVar
import net.liftweb.http.S

trait AddTagsSnippet[T <: (BaseEntityWithTitleAndDescription[T] with AddTags[T])] extends BaseEntityWithTitleAndDescriptionSnippet[T] {
  
  object selectedTags extends RequestVar("")
//  val tagItem = TheItem.asInstanceOf[AddTags[T]]
//  
//  type LocalTheTagType = tagItem.TheTagType
  //Project.getTagMapper
  
//    val tagItem = TheItem.asInstanceOf[AddTagsMeta[T]]
    
//    if (tagItem == null) println("tag item is null!")
  
//  val tagToItemMapper = TheItem.getTagMapper
//  
//  val theTagObject  = TheItem.theTagObject  // TheItem.theTagObject //
//   with MatchByID[T]
//  : GeneralTagMeta[LocalTheTagType] with MatchByID[LocalTheTagType]
//  var allTags = ("-1"->"please select a valid tag")::theTagObject.findAll.map(item => (item.primaryKeyField.toString, item.title.toString))
  
// 	 def bindNewRatingCSS(item : ItemType) : CssSel = {
// 	   	 def createNewItem : item.TheRating = item.TheRating.create.ratedItem(item) 
//		 
//		 listOfRatingOptionsCSStoInt.map({
//		   case (css, value) => {
//		     val newRating = createNewItem
//		   	 newRating.rating.set(value)
//		     "#%s [onclick]".format(css) #> SHtml.ajaxInvoke(() => {
//	       saveAndDisplayAjaxMessages(newRating,
//	           () => {
//				 // update the ratings
//				SetHtml("rating", generateDisplayRating(item)) &
////				 // hide the form
//				SetHtml("newrating",NodeSeq.Empty )
////				JsCmds.SetValById("newcomtitle", "") &
////				JsCmds.SetValById("newcommessage", "")
//	           }, 
//	           errors => {
//	             errors.map(println(_))
//	             JsCmds.Alert("adding rating failed! " )
//	           },"ratingMessages")
//			          } )
//		   }
//		 }).reduce(_ & _)
// 	 }
  
//  def loadTags(item : ItemType)( ids : List[String]) : List[item.TheTags] = ids.map(id => item.theTagObject.findByKey(id.toLong).get)
//  def loadTag( id : String)  = theTagObject.findByKey(id.toLong)

  
  abstract override def asHtml(item : ItemType) : CssSel = {
		 
//		println("chaining asHtml from AddCommentSnippet")
     ("#aTag *" #> item.tags.map(itm => {
//       itm.theTag.asInstanceOf[item.TheTagType].title.asHtml) //theTagObject
       val lookupTag = item.theTagObject.findByKey(itm.theTag.get) 
       lookupTag.map(_.title(S.locale).asHtml)openOr(Text("tag does not exist")) //dmap (Text("tag does not exist"))( (_:item.TheTagType ).title.asHtml)//foundTag : item.TheTagType => foundTag
     } )
         ) &
         // add the onsite editing stuff
         this.localToForm(item) &
     // chain the css selectors 
     (super.asHtml(item))
  }
  
    abstract override def toForm(item : ItemType) : CssSel = {
      this.localToForm(item)  &
     // chain the css selectors 
     (super.toForm(item))
    }
      def localToForm(item : ItemType) : CssSel = {
//		 var newTagTitle = ""
//		println("chaining asHtml from AddCommentSnippet")
//     ("#tags" #> SHtml.multiSelect(allTags, List(), item.tags ++= loadTag(item)(_) )
        val locale = S.locale
        
		def loadTag(localItem: ItemType)( id : String)  = localItem.theTagObject.findByKey(id.toLong)
		def loadTagFromTitle(localItem: ItemType)( title : String)  = localItem.theTagObject.findAll().filter(_.title(locale) == title)
		
		//val theTagObject = item.theTagObject
		val formerTags = item.tags.map(tagMapping => loadTag(item)(tagMapping.theTag.toString)map(_.title(locale).get) openTheBox )//loadTag(tagMapping.taggedItem))
		//var selectedTags : String = ""
		
		val allTags = item.theTagObject.findAll.map(theItem => (theItem.title(locale).toString))
 

		   
//		def createNewTag(localItem: ItemType)() : JsCmd = {
//		   println("created tag "+newTagTitle)
//		   localItem.theTagObject.create.title(newTagTitle).save
//		   JsCmds.Noop
//		 }
//		
//		def submitSelectedTagsList(localItem: ItemType)(selectedTags : List[String]) : JsCmd = {
//		  println("received tag-list"+selectedTags.mkString(","))
//		  selectedTags.map(
//		      stringTag => loadTag(localItem)(stringTag).map(theSelectedTag => localItem.getTagMapper.create.taggedItem(localItem).theTag(theSelectedTag).save )
//		      )
//		  JsCmds.Noop
//		}
		
		def submitSelectedTags(localItem: ItemType, selectedTags : String)() : JsCmd = {
		  val localFormerTags = localItem.tags.map(tagMapping => loadTag(localItem)(tagMapping.theTag.toString)map(_.title(S.locale).get) openTheBox )//loadTag(tagMapping.taggedItem))
		  val selectedTagsList =  selectedTags.split(",").map(_.trim)
		  val addTags = selectedTagsList.filter(!localFormerTags.contains(_))
		  val removeTags = localFormerTags.filter(!selectedTagsList.contains(_))
		  
		  
		  println("received: "+selectedTags)
		  
		  println("oldTags: "+localFormerTags.mkString(","))
		  println("addTags: "+addTags.mkString(","))
		  println("removeTags: "+removeTags.mkString(","))
		  // remove tags
		  //println("received tag-list"+selectedTags.mkString(","))
		  removeTags.map(
		      stringTag => {
		        val tagToRemove = loadTagFromTitle(localItem)(stringTag)
		        //tagToRemove.head.
		        val tagID = tagToRemove match {
		          case theTag::Nil => theTag.id.get
		          case _ => -1
		        }
		        //localItem.tags.filterNot(_.theTag.get == tagID)
		        localItem.tags.find(_.theTag.get == tagID).get.delete_!
		        localItem.tags.save
		        localItem.save
		      }
		      )
		  addTags.map(
		      stringTag => {
		        if (stringTag != null && stringTag.length>1){
			        val theSelectedTag = loadTagFromTitle(localItem)(stringTag) match {
			          case theTag :: Nil => theTag
			          case _ => {
			              val newTag = localItem.theTagObject.create
			              newTag.title(S.locale)(stringTag)
			              newTag
			          }
			        }
			        theSelectedTag.save
			        localItem.getTagMapper.create.taggedItem(localItem).theTag(theSelectedTag).save
		        }
		      }
		  )
		  JsCmds.Noop
		}
		   
     ("#tags" #> SHtml.text(formerTags.mkString("",",",""), submittedTags => {println("rec: "+submittedTags);submitSelectedTags(item,submittedTags)()}) &
       "#listAllTags" #>  allTags.mkString(", "))
      //"#tagshidden" #> SHtml.hidden(submitSelectedTags(item, selectedTags.get)) )
      
//      &
//     //SHtml.multiSelect(getAllTags(item), List(),submitSelectedTags(item),  "size"->"5") &
////         selected => selected.map(
////         _ match {
////       case item.theTagObject.
//////       theTagObject.getSingleton.MatchItemByID()
////     }
////       tag => loadTag(tag).map(theSelectedTag => tagToItemMapper.create.taggedItem(item).theTag(theSelectedTag).save ) // +=  _:item.TheTags 
////       ),  "size"->"5") &
//       "#newTagTitle" #> SHtml.ajaxText("", tagTitle => {newTagTitle=tagTitle;JsCmds.Noop}) &
//       "#newTagButton [onclick]" #> SHtml.ajaxInvoke(createNewTag(item))
////     }
//         
//         )  
  }
}
