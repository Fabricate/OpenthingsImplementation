package at.fabricate.liftdev.common
package lib

import java.util.Locale

class MatchLanguage{
    def unapply(language: String): Option[Locale] =
      Locale.getAvailableLocales.find(_.toString == language)
  }