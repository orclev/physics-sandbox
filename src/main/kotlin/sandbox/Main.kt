@file:JvmName("Main")
package sandbox

import kotlinx.support.jdk7.use
import org.dyn4j.collision.AxisAlignedBounds
import sandbox.physics.GameWorld
import sandbox.physics.floor
import sandbox.physics.world

fun main(args: Array<String>) {
    val world = world(AxisAlignedBounds(640.0, 480.0)) {
        floor()
    }
    Renderer(640, 480, world).use { r: Renderer<GameWorld> ->
        r.loop { dt: Double ->
            state.updatev(dt)
            state.renderWorld()
        }
    }
}
