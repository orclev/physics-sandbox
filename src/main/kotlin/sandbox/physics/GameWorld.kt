package sandbox.physics

import org.dyn4j.collision.Bounds
import org.dyn4j.dynamics.World
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import sandbox.graphics.Attribute
import sandbox.graphics.Program
import sandbox.graphics.Uniform
import java.nio.FloatBuffer
import org.lwjgl.BufferUtils.createFloatBuffer
import org.lwjgl.BufferUtils



class GameWorld(bounds: Bounds) : World(bounds) {
    /** The scale 45 pixels per meter  */
    val SCALE = 45.0f

    /** The conversion factor from nano to base  */
    val NANO_TO_BASE = 1.0e9

    fun initializeVBOs() {
        this.bodies
            .map { it as GameObject }
            .forEach { it.createVBOs() }
    }

    fun renderWorld(program: Program, vertexAttribute: Attribute, colorUniform: Uniform, mvpUniform: Uniform) {
        mvpUniform.set(Matrix4f().ortho2D(-400.0f,400.0f,-300.0f,300.0f).scale(SCALE))

        this.bodies
            .map { it as GameObject }
            .forEach {
                it.updateFixtureVBOs()
                it.renderObject(program, vertexAttribute, colorUniform)
            }
    }
}