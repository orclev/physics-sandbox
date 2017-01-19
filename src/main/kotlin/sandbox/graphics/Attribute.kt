package sandbox.graphics

import org.lwjgl.opengl.GL20

/**
 * Created by Kyle on 2017-01-18.
 */
class Attribute(val program: Program, val name: String, val arity: Int) {
  val ident by lazy {
    if(!program.isLinked()) {
      throw IllegalStateException("Attempt to access GLSL attribute before containing program linked")
    }
    val attrib = GL20.glGetAttribLocation(program.ident, name)
    if(attrib == -1) {
      throw InvalidAttributeException(name)
    }
    attrib
  }

  class InvalidAttributeException(val name: String): RuntimeException(name)

  fun enable() {
    GL20.glEnableVertexAttribArray(ident)
  }

  fun disable() {
    GL20.glDisableVertexAttribArray(ident)
  }
}