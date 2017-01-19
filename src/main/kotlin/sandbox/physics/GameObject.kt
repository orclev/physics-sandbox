package sandbox.physics

import org.dyn4j.collision.Fixture
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.Polygon
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import sandbox.graphics.*
import java.util.*

class GameObject : Body() {
    /** The color of the object  */
    var color = floatArrayOf(
          Math.random().toFloat() * 0.5f + 0.5f
        , Math.random().toFloat() * 0.5f + 0.5f
        , Math.random().toFloat() * 0.5f + 0.5f
        , 1.0f)
    var lineColor = color.mapIndexed { i, v -> if (i != 3) v * 0.8f else v }.toFloatArray()

    /**
     * Default constructor.
     */
    init {
        this.color[0] = Math.random().toFloat() * 0.5f + 0.5f
        this.color[1] = Math.random().toFloat() * 0.5f + 0.5f
        this.color[2] = Math.random().toFloat() * 0.5f + 0.5f
        this.color[3] = 1.0f
    }

    protected val fixtureVBOs: MutableMap<Fixture,DoubleArrayVBO> = HashMap()
    protected val colorVBO: FloatArrayVBO by lazy {
        FloatArrayVBO(VBO.BufferType.STATIC, 4)
    }

    fun createVBOs() {
        for (fixture in this.fixtures) {
            val convex = fixture.shape
            if (convex is Polygon) {
                fixtureVBOs.put(fixture, DoubleArrayVBO(VBO.BufferType.DYNAMIC, 2))
            }
        }
        colorVBO.pushData(color)
    }

    fun updateFixtureVBOs() {
        for (fixture in this.fixtures) {
            val convex = fixture.shape
            if (convex is Polygon) {
                val vertices = convex.vertices.flatMap {
                    val trans = transform.getTransformed(it)
                    listOf(trans.x, trans.y)
                }
                val vbo = fixtureVBOs[fixture] ?: throw IllegalStateException("Could not find VBO for fixture ${fixture.id}")
                vbo.pushData(vertices.toDoubleArray())
            }
        }
    }

    /**
     * Draws the body.
     *
     *
     * Only coded for polygons.
     * @param gl the OpenGL graphics context
     */
    fun renderObject(program: Program, vertexAttribute: Attribute, colorUniform: Uniform) {
        GL20.glUseProgram(program.ident)

        for (fixture in this.fixtures) {
            val convex = fixture.shape
            if (convex is Polygon) {
                val vbo = fixtureVBOs[fixture] ?: throw IllegalStateException("Could not find VBO for fixture ${fixture.id}")
                vbo.withAttribute(vertexAttribute) {
                    colorUniform.set(this.color)
                    glDrawArrays(GL_POLYGON, 0, convex.vertices.count())

                    colorUniform.set(this.lineColor)
                    glDrawArrays(GL_LINE_LOOP, 0, convex.vertices.count())
                }
            }
        }
    }
}