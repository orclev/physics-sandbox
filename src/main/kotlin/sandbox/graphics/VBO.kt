package sandbox.graphics

import org.lwjgl.opengl.*
import java.nio.*
import kotlin.reflect.KClass

/**
 * Created by Kyle on 2017-01-18.
 */
abstract class VBO<T>(val type: Int, var arity: Int): AutoCloseable {
  val ident by lazy {
    val buff: IntArray = IntArray(1)
    GL15.glGenBuffers(buff)
    buff[0]
  }

  abstract fun pushData(data: T)

  protected inline fun activate() {
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ident)
  }

  inline fun unbindBuffer() {
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
  }

  fun withAttribute(attribute: Attribute, inner: () -> Unit) {
    if(attribute.arity != arity) {
      throw IllegalArgumentException("Attempt to bind VBO of arity $arity to attribute of arity ${attribute.arity}")
    }
    activate()
    attribute.enable()
    GL20.glVertexAttribPointer(attribute.ident, arity, type, false, 0, 0)
    inner()
    attribute.disable()
  }

  override fun close() {
    GL15.glDeleteBuffers(ident)
  }

  inline fun validateArity(size: () -> Int) {
    val i = size() % arity
    if(i != 0) {
      throw IllegalArgumentException(
          "Attempt to load wrong sized data into VBO of arity $arity. Data off by $i please insure buffer is a multiple of $arity")
    }
  }

  enum class BufferType {
    STATIC, DYNAMIC, STREAM;

    fun toGLConstant(): Int {
      return when(this) {
        STATIC -> GL15.GL_STATIC_DRAW
        DYNAMIC -> GL15.GL_DYNAMIC_DRAW
        STREAM -> GL15.GL_STREAM_DRAW
      }
    }
  }
}

inline fun <reified T : Number> T.getGLType(): Int {
  return classToGLType(T::class)
}

inline fun <reified T : Number> Array<T>.getGLType(): Int {
  return classToGLType(T::class)
}

fun <T : Number> classToGLType(klass: KClass<T>): Int {
  return when(klass) {
    Int::class -> GL11.GL_INT
    Short::class -> GL11.GL_SHORT
    Double::class -> GL11.GL_DOUBLE
    Float::class -> GL11.GL_FLOAT
    Byte::class -> GL11.GL_BYTE
    else -> throw UnsupportedOperationException("Can not convert numeric type ${klass.simpleName} to GL numeric type")
  }
}

class IntArrayVBO(private val draw: BufferType, arity: Int): VBO<IntArray>(GL11.GL_INT, arity) {
  override fun pushData(data: IntArray) {
    validateArity { data.size }
    activate()
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, draw.toGLConstant())
    unbindBuffer()
  }
}

class IntBufferVBO(private val draw: BufferType, arity: Int): VBO<IntBuffer>(GL11.GL_INT, arity) {
  override fun pushData(data: IntBuffer) {
    validateArity { data.remaining() }
    activate()
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, draw.toGLConstant())
    unbindBuffer()
  }
}

class ShortArrayVBO(private val draw: BufferType, arity: Int): VBO<ShortArray>(GL11.GL_SHORT, arity) {
  override fun pushData(data: ShortArray) {
    validateArity { data.size }
    activate()
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, draw.toGLConstant())
    unbindBuffer()
  }
}

class ShortBufferVBO(private val draw: BufferType, arity: Int): VBO<ShortBuffer>(GL11.GL_SHORT, arity) {
  override fun pushData(data: ShortBuffer) {
    validateArity { data.remaining() }
    activate()
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, draw.toGLConstant())
    unbindBuffer()
  }
}

class DoubleArrayVBO(private val draw: BufferType, arity: Int): VBO<DoubleArray>(GL11.GL_DOUBLE, arity) {
  override fun pushData(data: DoubleArray) {
    validateArity { data.size }
    activate()
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, draw.toGLConstant())
    unbindBuffer()
  }
}

class DoubleBufferVBO(private val draw: BufferType, arity: Int): VBO<DoubleBuffer>(GL11.GL_DOUBLE, arity) {
  override fun pushData(data: DoubleBuffer) {
    validateArity { data.remaining() }
    activate()
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, draw.toGLConstant())
    unbindBuffer()
  }
}

class FloatArrayVBO(private val draw: BufferType, arity: Int): VBO<FloatArray>(GL11.GL_FLOAT, arity) {
  override fun pushData(data: FloatArray) {
    validateArity { data.size }
    activate()
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, draw.toGLConstant())
    unbindBuffer()
  }
}

class FloatBufferVBO(private val draw: BufferType, size: Int): VBO<FloatBuffer>(GL11.GL_FLOAT, size) {
  override fun pushData(data: FloatBuffer) {
    validateArity { data.remaining() }
    activate()
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, draw.toGLConstant())
    unbindBuffer()
  }
}

class ByteBufferVBO(private val draw: BufferType, size: Int): VBO<ByteBuffer>(GL11.GL_BYTE, size) {
  override fun pushData(data: ByteBuffer) {
    activate()
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, draw.toGLConstant())
    unbindBuffer()
  }
}