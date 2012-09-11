package org.meritocracy.comet

import net.liftweb._
import http._
import util._
import Helpers._
import com.fmpwizard.cometactor.pertab.namedactor.{ NamedCometActor, CometListerner }
import net.liftweb.common._
import org.meritocracy.model._
import scala.xml.NodeSeq

case class Base(baseId: String, limit: Int)


class UserMessenger extends NamedCometActor with Loggable{
  private var limit = 10
  private var baseId = ""
    
  override def lowPriority: PartialFunction[Any, Unit] = {
    case Base(id, lim) => {
      logger.info("We received the id=" + id)
      baseId = id
      limit = lim
      reRender();
    }
    case _ => error("The message for SearchGrouChat is not correct")
  }
  
  def render = {
    var out = NodeSeq.Empty
    for {
      umb <- UserMessageBase.find(baseId)
    } yield {
      out = <ul id="groupMessages"> {
        umb.showMessages(limit).map {
          x => <li> { x.getUsername + " : " + x.message.is } </li>
        }
      }</ul>

    }
    out
  }
}