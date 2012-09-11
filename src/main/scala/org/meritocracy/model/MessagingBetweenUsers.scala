package org.meritocracy.model
import net.liftweb.record.field._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.common._
import com.foursquare.rogue.Rogue._

class UserMessage extends MongoRecord[UserMessage] with ObjectIdPk[UserMessage] with Loggable {
  def meta = UserMessage
  object message extends TextareaField(this, 500)
  object user_id extends ObjectIdRefField(this, User)
  object base_id extends ObjectIdRefField(this, UserMessageBase)
  def getUsername = {
    this.user_id.obj.map{
      _.username.is
    }.getOrElse{
      logger.error("This id="+this.id.is +" does not have a proper user_id")
      ""
    }
  }

}

object UserMessage extends UserMessage with MongoMetaRecord[UserMessage] with Loggable {
  def add(user: User, base: UserMessageBase, message: String) = {
    UserMessage.createRecord
      .user_id(user.id.is)
      .base_id(base.id.is)
      .message(message)
      .saveTheRecord()
  }

}

class UserMessageBase extends MongoRecord[UserMessageBase] with ObjectIdPk[UserMessageBase] {
  def meta = UserMessageBase
  object user1_id extends ObjectIdRefField(this, User)
  object user2_id extends ObjectIdRefField(this, User)
  def showMessages(limit: Int) = {
    UserMessage.where(_.base_id eqs this.id.is).orderDesc(_.id).fetch(limit).reverse
  }
}

object UserMessageBase extends UserMessageBase with MongoMetaRecord[UserMessageBase] with Loggable {

  def add(user1: User, user2: User) = {
    val base = findByUsers(user1, user2)
    if (base.isEmpty) {
      UserMessageBase.createRecord
        .user1_id(user1.id.is)
        .user2_id(user2.id.is)
        .saveTheRecord()
    } else {
      base
    }
  }

  def findByUsers(user1: User, user2: User): Box[UserMessageBase] = {
    val ls1 = UserMessageBase.where(_.user1_id eqs user1.id.is).and(_.user2_id eqs user2.id.is).fetch()
    val ls2 = UserMessageBase.where(_.user1_id eqs user2.id.is).and(_.user2_id eqs user1.id.is).fetch()
    if (ls1.isEmpty) {
      ls2.headOption
    } else {
      ls1.headOption
    }
  }
}