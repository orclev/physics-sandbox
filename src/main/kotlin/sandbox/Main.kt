@file:JvmName("Main")
package sandbox

import kotlinx.support.jdk7.use
import org.dyn4j.collision.AxisAlignedBounds
import sandbox.graphics.*
import sandbox.physics.GameWorld
import sandbox.physics.demo
import sandbox.physics.world

fun main(args: Array<String>) {
    val world = world(AxisAlignedBounds(640.0, 480.0)) {
        demo()
    }

    Renderer(640, 480, world, ::generateProgram).use { r: Renderer<GameWorld> ->
        world.initializeVBOs()
        r.loop { dt: Double ->
            state.updatev(dt)
            state.renderWorld(program, program["coord2d"], program.getUniform("f_color"), program.getUniform("mvp"))
        }
    }
}

fun generateProgram(): Program {
    return program {
        vertexShader("basic.v.glsl")
        declareAttribute("coord2d", 2)
        declareMatrixUniform("mvp", 4)
        fragmentShader("basic.f.glsl")
        declareUniform("f_color", 4)
    }
}
