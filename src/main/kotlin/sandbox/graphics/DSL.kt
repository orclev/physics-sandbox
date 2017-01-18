package sandbox.graphics

import sandbox.graphics.Shader.ShaderType.*
import java.io.File

/**
 * Created by Kyle on 2017-01-17.
 */
fun program(init: Program.() -> Unit): Program {
  val prog: Program = Program()
  prog.init()
  prog.link()
  return prog
}

fun Program.vertexShader(code: Program.() -> CharSequence) {
  this.attachShader(Shader(VERTEX, this.code()))
}

fun Program.vertexShader(path: String) {
  this.attachShader(Shader(VERTEX, Program::class.java.classLoader.getResourceAsStream(path)))
}

fun Program.fragmentShader(code: Program.() -> CharSequence) {
  this.attachShader(Shader(FRAGMENT, this.code()))
}

fun Program.fragmentShader(path: String) {
  this.attachShader(Shader(FRAGMENT, Program::class.java.classLoader.getResourceAsStream(path)))
}

fun Program.geometryShader(code: Program.() -> CharSequence) {
  this.attachShader(Shader(GEOMETRY, this.code()))
}

fun Program.geometryShader(path: String) {
  this.attachShader(Shader(GEOMETRY, Program::class.java.classLoader.getResourceAsStream(path)))
}