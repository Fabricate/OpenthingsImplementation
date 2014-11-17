package at.fabricate
package model

import net.liftweb.mapper._

class Image extends LongKeyedMapper[Image] with IdPK {
  def getSingleton = Image

  object image extends MappedBinary(this)
  object lookup extends MappedUniqueId(this, 32) {
    override def dbIndexed_? = true
  }
  object saveTime extends MappedLong(this) {
    //override def defaultValue = millis
  }
  object mimeType extends MappedString(this, 256)
}

object Image extends Image with LongKeyedMetaMapper[Image]