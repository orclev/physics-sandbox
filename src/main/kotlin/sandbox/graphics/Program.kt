package sandbox.graphics

import java.util.*

import org.lwjgl.opengl.GL11

import org.lwjgl.opengl.GL20
import java.util.*

/**
 * Created by Kyle on 2017-01-17.
 */
class Program private constructor(private val programAttributes: MutableMap<String, Int>): AutoCloseable, Map<String, Int> by programAttributes {
  constructor() : this(HashMap())
  private val ident by lazy {
    GL20.glCreateProgram()
  }
  private val attachedShaders: MutableList<Shader> = ArrayList()

  fun attachShader(shader: Shader) {
    GL20.glAttachShader(ident, shader.ident)
    attachedShaders.add(shader)
  }

  fun declareAttribute(name: String) {
    programAttributes.put(name, -1)
  }

  fun link() {
    GL20.glLinkProgram(ident)
    if(!isLinked()) {
      throw ProgramLinkException(GL20.glGetProgramInfoLog(ident))
    }
    detachShaders()
    lookupAttributes()
  }

  fun isLinked(): Boolean {
    val status: IntArray = IntArray(1)
    GL20.glGetProgramiv(ident, GL20.GL_LINK_STATUS, status)
    return status[0] == GL11.GL_TRUE
  }

  private fun lookupAttributes() {
    for (name: String in programAttributes.keys) {
      val attrib = GL20.glGetAttribLocation(ident, name)
      if(attrib == -1) {
        throw InvalidAttributeException(name)
      }
      programAttributes.set(name, attrib)
    }
  }

  private fun detachShaders() {
    for (shader in attachedShaders) {
      GL20.glDetachShader(ident, shader.ident)
    }
    attachedShaders.clear()
  }

  override fun close() {
    detachShaders()
    GL20.glDeleteProgram(ident)
  }

  class ProgramLinkException(val log: String): RuntimeException(log)
  class InvalidAttributeException(val name: String): RuntimeException(name)
}
