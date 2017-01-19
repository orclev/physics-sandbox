package sandbox.graphics

import java.util.*

import org.lwjgl.opengl.GL11

import org.lwjgl.opengl.GL20
import java.util.*

/**
 * Created by Kyle on 2017-01-17.
 */
class Program
  private constructor(private val programAttributes: MutableMap<String, Attribute>
      , private val programUniforms: MutableMap<String, Uniform>): AutoCloseable, Map<String, Attribute> by programAttributes {
  constructor() : this(HashMap(), HashMap())
  val ident by lazy {
    GL20.glCreateProgram()
  }
  private val attachedShaders: MutableList<Shader> = ArrayList()

  fun attachShader(shader: Shader) {
    GL20.glAttachShader(ident, shader.ident)
    attachedShaders.add(shader)
  }

  fun declareAttribute(name: String, arity: Int) {
    programAttributes.put(name, Attribute(this, name, arity))
  }

  fun declareUniform(name: String, size: Int) {
    programUniforms.put(name, Uniform(this, name, size, false))
  }

  fun declareMatrixUniform(name: String, size: Int) {
    programUniforms.put(name, Uniform(this, name, size, true))
  }

  override fun get(key: String): Attribute {
    return programAttributes[key] ?: throw IllegalStateException("Attempt to access undefined attribute $key")
  }

  fun getUniform(key: String): Uniform {
    return programUniforms[key] ?: throw IllegalStateException("Attempt to access undefined uniform $key")
  }

  fun link() {
    GL20.glLinkProgram(ident)
    if(!isLinked()) {
      throw ProgramLinkException(GL20.glGetProgramInfoLog(ident))
    }
    detachShaders()
    lookupAttributes()
    lookupUniforms()
  }

  fun isLinked(): Boolean {
    val status: IntArray = IntArray(1)
    GL20.glGetProgramiv(ident, GL20.GL_LINK_STATUS, status)
    return status[0] == GL11.GL_TRUE
  }

  private fun lookupAttributes() {
    for (attrib in programAttributes.values) {
      attrib.ident // Force it to not be lazy here
    }
  }

  private fun lookupUniforms() {
    for (uniform in programUniforms.values) {
      uniform.ident
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
}
