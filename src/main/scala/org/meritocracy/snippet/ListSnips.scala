package org.meritocracy.snippet
import scala.xml.{ NodeSeq, Text }
import net.liftweb._
import http._
import util._
import common._
import Helpers._
import js.JsCmds._
import org.meritocracy.model._

class ListOfUsers {
  def render = {

    User.currentUser.map {
      user =>
        "#listOfUsers" #> User.findAll
          .filter(_.id.is.toString != user.id.is.toString)
          .map {
            other =>
              "li *" #> {
                "#username *" #> other.username.is &
                  "#message *" #> SHtml.ajaxButton(Text("send message"), () => {
                    UserMessageBase.add(user, other).map {
                      base => S.redirectTo("/messages/" + base.id.is.toString)
                    }
                    Noop
                  })
              }

          }
    }.getOrElse(NodeSeq.Empty)
  }
}