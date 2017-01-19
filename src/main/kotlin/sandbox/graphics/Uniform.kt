package sandbox.graphics

import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * Created by Kyle on 2017-01-18.
 */
class Uniform(val program: Program, val name: String, val size: Int, val matrix: Boolean = false) {
  val ident by lazy {
    if(!program.isLinked()) {
      throw IllegalStateException("Attempt to access GLSL attribute before containing program linked")
    }
    val uniform = GL20.glGetUniformLocation(program.ident, name)
    if(uniform == -1) {
      throw InvalidUniformException(name)
    }
    uniform
  }

  init {
    if(matrix && size == 1) {
      throw IllegalArgumentException("Matrix uniforms must be at least 2 elements in length")
    }
    if(size > 4) {
      throw IllegalArgumentException("Max size for uniforms is 4")
    }
  }

  fun validate(total: () -> Int) {
    val t = total()
    if(matrix) {
      if(t != size * size) {
        throw IllegalArgumentException("Array size^2 must match uniform size, attempt to use $t element array for $size element uniform")
      }
    } else {
      if(t != size) {
        throw IllegalArgumentException("Array size must match uniform size, attempt to use $t element array for $size element uniform")
      }
    }
  }

  fun set(value: IntArray) {
    validate { value.size }
    if(matrix) {
      set(value.map(Int::toFloat).toFloatArray())
    }
    when(size) {
      1 -> GL20.glUniform1iv(ident, value)
      2 -> GL20.glUniform2iv(ident, value)
      3 -> GL20.glUniform3iv(ident, value)
      4 -> GL20.glUniform4iv(ident, value)
    }
  }

  fun set(value: IntBuffer) {
    validate { value.remaining() }
    if(matrix) {
      set(value.array())
    }
    when(size) {
      1 -> GL20.glUniform1iv(ident, value)
      2 -> GL20.glUniform2iv(ident, value)
      3 -> GL20.glUniform3iv(ident, value)
      4 -> GL20.glUniform4iv(ident, value)
    }
  }

  fun set(value: FloatArray) {
    validate { value.size }
    when(size) {
      1 -> GL20.glUniform1fv(ident, value)
      2 -> if (matrix) GL20.glUniformMatrix2fv(ident, false, value) else GL20.glUniform2fv(ident, value)
      3 -> if (matrix) GL20.glUniformMatrix3fv(ident, false, value) else GL20.glUniform3fv(ident, value)
      4 -> if (matrix) GL20.glUniformMatrix4fv(ident, false, value) else GL20.glUniform4fv(ident, value)
    }
  }

  fun set(value: FloatBuffer) {
    validate { value.remaining() }
    when(size) {
      1 -> GL20.glUniform1fv(ident, value)
      2 -> if (matrix) GL20.glUniformMatrix2fv(ident, false, value) else GL20.glUniform2fv(ident, value)
      3 -> if (matrix) GL20.glUniformMatrix3fv(ident, false, value) else GL20.glUniform3fv(ident, value)
      4 -> if (matrix) GL20.glUniformMatrix4fv(ident, false, value) else GL20.glUniform4fv(ident, value)
    }
  }

  fun set(value: Matrix4f) {
    if(!matrix || size != 4) {
      throw IllegalArgumentException("Can only initialize size 4 matrix uniform from Matrix4f")
    }
    val fb = BufferUtils.createFloatBuffer(16)
    value.get(fb)
    GL20.glUniformMatrix4fv(ident, false, fb)
  }

  fun set(value: Number) {
    validate { 1 }
    when(value) {
      is Byte -> GL20.glUniform1i(ident, value as Int)
      is Int -> GL20.glUniform1i(ident, value)
      is Long -> GL20.glUniform1i(ident, value as Int)
      is Float -> GL20.glUniform1f(ident, value)
      is Double -> GL20.glUniform1f(ident, value as Float)
    }
  }

  class InvalidUniformException(val name: String): RuntimeException(name)
}