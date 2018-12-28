package foo

class Bar {
  val x = new Foo
}

object Bar {
  def apply(): Bar = {
    new Bar
  }
}
