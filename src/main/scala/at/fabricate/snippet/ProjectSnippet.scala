package at.fabricate
package snippet

import model.Project





object ProjectSnippet extends BaseRichEntitySnippet[Project](Project) with AddCommentSnippet[Project] {
  override val itemBaseUrl = "project"

}