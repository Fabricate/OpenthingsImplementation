package at.fabricate
package lib

import at.fabricate.model.Image
import net.liftweb.mapper.By
import net.liftweb.http._
import net.liftweb.common._
import at.fabricate.model.User
import net.liftweb.mapper.By
import net.liftweb.util.BasicTypesHelpers._
import net.liftweb.util._

object ImageLogic {
  
  // TODO: unsafe, what if the image does not exist
  object UserImage{
    def unapply(in: String): Option[User] =
    		User.find(By(User.id, in.toInt ))
  }
  
    def matcher: LiftRules.DispatchPF = {
    case r @ Req("serve" :: "userimage" :: UserImage(user) ::
                 Nil, _, GetRequest) => () => Full(InMemoryResponse(user.userImage.get,
                               List("Content-Type" -> "image/jpeg",
                                    "Content-Length" ->
                                    user.userImage.get.length.toString), Nil, 200))  
  }
    
  /*
   * 
   * () => servImage(user, r)
                 
    def servImage(user: User, r: Req): Box[LiftResponse] = 
      
   * 
   * {
    if (user)
    Full(InMemoryResponse(new Array[Byte](0),
                          List("Last-Modified" ->
                               toInternetDate(img.saveTime.is)), Nil, 304))
    else
      
                                   //"Last-Modified" ->
                                   // toInternetDate(img.saveTime.is),
  object TestImage {
    def unapply(in: String): Option[Image] =
    Image.find(By(Image.lookup, in.trim))
  }

  def matcher: LiftRules.DispatchPF = {
    case r @ Req("image_logic" :: TestImage(img) ::
                 Nil, _, GetRequest) => () => servImage(img, r)
  }

  def servImage(img: Image, r: Req): Box[LiftResponse] = {
    if (r.testIfModifiedSince(img.saveTime))
    Full(InMemoryResponse(new Array[Byte](0),
                          List("Last-Modified" ->
                               toInternetDate(img.saveTime.is)), Nil, 304))
    else Full(InMemoryResponse(img.image.is,
                               List("Last-Modified" ->
                                    toInternetDate(img.saveTime.is),
                                    "Content-Type" -> img.mimeType.is,
                                    "Content-Length" ->
                                    img.image.is.length.toString), Nil, 200))
  }
  * 
  */
}