import com.github.tkqubo.html2md.ConversionRule
import com.github.tkqubo.html2md.helpers.NodeOps._
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._
import com.github.tkqubo.html2md.converters.MarkdownConverter

/**
  * Converts html text into markdown
  */
class PlaintextConverter extends MarkdownConverter {
  override val rules: Seq[ConversionRule] = Seq(

    // anything else
    { _: Element => true } -> { (content: String, e: Element) =>
      content
    }
  )
}
