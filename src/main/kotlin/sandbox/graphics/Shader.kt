package sandbox.graphics

import org.lwjgl.opengl.*
import java.io.File
import java.io.InputStream

/**
 * Created by Kyle on 2017-01-17.
 */
class Shader(val type: ShaderType, val source: CharSequence): AutoCloseable {
  val ident by lazy {
    GL20.glCreateShader(type.toGLConstant())
  }

  init {
    GL20.glShaderSource(ident, source)
    GL20.glCompileShader(ident)
    if(!isCompiled()) {
      throw ShaderCompilationException(GL20.glGetShaderInfoLog(ident))
    }
  }

  constructor(type: ShaderType, stream: InputStream): this(type, stream.reader().readText())

  fun isCompiled(): Boolean {
    val status: IntArray = IntArray(1)
    GL20.glGetShaderiv(ident, GL20.GL_COMPILE_STATUS, status)
    return status[0] == GL11.GL_TRUE
  }

  override fun close() {
    GL20.glDeleteShader(ident)
  }

  class ShaderCompilationException(val log: String): RuntimeException(log)

  enum class ShaderType {
    VERTEX, FRAGMENT, TESS_CONTROL, TESS_EVALUATION, GEOMETRY, COMPUTE;

    fun toGLConstant(): Int {
      return when(this) {
        VERTEX -> GL20.GL_VERTEX_SHADER
        FRAGMENT -> GL20.GL_FRAGMENT_SHADER
        GEOMETRY -> GL32.GL_GEOMETRY_SHADER
        TESS_CONTROL -> GL40.GL_TESS_CONTROL_SHADER
        TESS_EVALUATION -> GL40.GL_TESS_EVALUATION_SHADER
        COMPUTE -> GL43.GL_COMPUTE_SHADER
      }
    }
  }
}
