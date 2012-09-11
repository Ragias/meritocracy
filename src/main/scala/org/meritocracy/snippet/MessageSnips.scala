package org.meritocracy.snippet
import scala.xml.{ NodeSeq, Text }
import net.liftweb._
import http._
import util._
import common._
import Helpers._
import js.JsCmds._
import org.meritocracy.model._
import org.meritocracy.lib._
import com.fmpwizard.cometactor.pertab.namedactor.{InsertNamedComet , CometListerner}
import net.liftweb.actor.LiftActor
import org.meritocracy.comet.Base


class UserMessengerComet extends InsertNamedComet with Loggable {
  override lazy val name = Site.messagesLoc.currentValue.map(_.id.is.toString).openOr("")
  override lazy val cometClass = "UserMessenger"

}

class UserSendMessage extends Loggable{
  def render = {
    var message = ""
    User.currentUser.map {
      user =>
        Site.messagesLoc.currentValue.map {
          base =>
            CometListerner.listenerFor(Full(base.id.toString)) match {
                case a: LiftActor => {
                  logger.info("We send the id=" + base.id.toString)
                  a ! Base(base.id.toString,10)
                
                }
                case _ => logger.info("No actor to send an update")
              }
            "#textMessage" #> SHtml.ajaxText("", s => {
              message = s
            }) &
              "#sendMessage" #> SHtml.ajaxButton(Text("Send"), () => {
                UserMessage.add(user, base, message)
                CometListerner.listenerFor(Full(base.id.toString)) match {
                case a: LiftActor => {
                  logger.info("We send the id=" + base.id.toString)
                  a ! Base(base.id.toString,10)
                
                }
                case _ => logger.info("No actor to send an update")
              }
                
             SetValById("textMessage","")   
              })
        }.getOrElse(NodeSeq.Empty)
    }.getOrElse(NodeSeq.Empty)
  }
}