package org.meritocracy.lib

object AutocompleteHelper {
  def auto(current: String, limit: Int, ls: List[String]): Seq[String] = {
    val list = current.split(",").map(x => x.trim)

    ls.filter(_.toLowerCase.startsWith(list.last.toLowerCase))
      .map { v =>
        if (list.length > 1) {
          list.dropRight(1).reduce[String] { (acc, n) => acc + ", " + n } + ", " + v
        } else { v }
      }
  }
  
  def getLast(current:String)=   current.split(",").map(x => x.trim).last

}